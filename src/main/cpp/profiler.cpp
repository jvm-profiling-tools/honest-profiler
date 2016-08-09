#include "profiler.h"
#include <sys/time.h>

ASGCTType Asgct::asgct_;


bool Profiler::lookupFrameInformation(const JVMPI_CallFrame &frame,
        jvmtiEnv *jvmti,
        MethodListener &logWriter) {
    jint error;
    JvmtiScopedPtr<char> methodName(jvmti);

    error = jvmti->GetMethodName(frame.method_id, methodName.GetRef(), NULL, NULL);
    if (error != JVMTI_ERROR_NONE) {
        methodName.AbandonBecauseOfError();
        if (error == JVMTI_ERROR_INVALID_METHODID) {
            static int once = 0;
            if (!once) {
                once = 1;
                logError("One of your monitoring interfaces "
                         "is having trouble resolving its stack traces.  "
                         "GetMethodName on a jmethodID involved in a stacktrace "
                         "resulted in an INVALID_METHODID error which usually "
                         "indicates its declaring class has been unloaded.\n");
                logError("Unexpected JVMTI error %d in GetMethodName\n", error);
            }
        }
        return false;
    }

    // Get class name, put it in signature_ptr
    jclass declaring_class;
    JVMTI_ERROR_RET(
            jvmti->GetMethodDeclaringClass(frame.method_id, &declaring_class), false);

    JvmtiScopedPtr<char> signature_ptr2(jvmti);
    JVMTI_ERROR_CLEANUP_RET(
            jvmti->GetClassSignature(declaring_class, signature_ptr2.GetRef(), NULL),
            false, signature_ptr2.AbandonBecauseOfError());

    // Get source file, put it in source_name_ptr
    char *fileName;
    JvmtiScopedPtr<char> source_name_ptr(jvmti);
    static char file_unknown[] = "UnknownFile";
    if (JVMTI_ERROR_NONE !=
            jvmti->GetSourceFileName(declaring_class, source_name_ptr.GetRef())) {
        source_name_ptr.AbandonBecauseOfError();
        fileName = file_unknown;
    } else {
        fileName = source_name_ptr.Get();
    }

    logWriter.recordNewMethod((method_id) frame.method_id, fileName,
            signature_ptr2.Get(), methodName.Get());

    return true;
}

void Profiler::handle(int signum, siginfo_t *info, void *context) {
    IMPLICITLY_USE(signum);
    IMPLICITLY_USE(info);

    // sample data structure
    STATIC_ARRAY(frames, JVMPI_CallFrame, configuration_->maxFramesToCapture, MAX_FRAMES_TO_CAPTURE);

    JVMPI_CallTrace trace;
    trace.frames = frames;
    JNIEnv *jniEnv = getJNIEnv(jvm_);
    if (jniEnv == NULL) {
    	trace.num_frames = -3; // ticks_unknown_not_Java
    } else {
  		trace.env_id = jniEnv;
	  	ASGCTType asgct = Asgct::GetAsgct();
		  (*asgct)(&trace, configuration_->maxFramesToCapture, context);
    }
    // log all samples, failures included, let the post processing sift through the data
  	buffer->push(trace);
}

bool Profiler::start(JNIEnv *jniEnv) {
    if (isRunning()) {
        logError("WARN: Start called but sampling is already running\n");
        return true;
    }
    configure();
    // reference back to Profiler::handle on the singleton
    // instance of Profiler
    handler_->SetAction(&bootstrapHandle);
    processor->start(jniEnv);
    return handler_->updateSigprofInterval();
}

void Profiler::stop() {
    if (initialized) {
        handler_->stopSigprof();
        processor->stop();
        signal(SIGPROF, SIG_IGN);
    }
}

bool Profiler::isRunning() const {
    return processor && processor->isRunning();
}

void Profiler::setFilePath(char *newFilePath) {
    if (isRunning()) {
        logError("WARN: Unable to modify running profiler\n");
        return;
    }
    liveConfiguration->logFilePath = newFilePath;
}

void Profiler::setSamplingInterval(int intervalMin, int intervalMax) {
    if (isRunning()) {
        logError("WARN: Unable to modify running profiler\n");
        return;
    }
    liveConfiguration->samplingIntervalMin = intervalMin;
    liveConfiguration->samplingIntervalMin = intervalMax;
}

void Profiler::setMaxFramesToCapture(int maxFramesToCapture) {
    if (isRunning()) {
        logError("WARN: Unable to modify running profiler\n");
        return;
    }
    liveConfiguration->maxFramesToCapture = maxFramesToCapture;
}

void Profiler::configure() {
    bool needsUpdate = !initialized;
    
    needsUpdate = needsUpdate || configuration_->logFilePath != liveConfiguration->logFilePath;
    if (needsUpdate) {
        if (logFile) delete logFile;
        if (writer) delete writer;
        if (configuration_->logFilePath) delete configuration_->logFilePath;
        
        char *fileName = liveConfiguration->logFilePath;
        string fileNameStr;
        if (fileName == NULL) {
            ostringstream fileBuilder;
            long pid = (long) getpid();
            long epochMillis = duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
            fileBuilder << "log-" << pid << "-" << epochMillis << ".hpl";
            fileNameStr = fileBuilder.str();
            fileName = (char*)fileNameStr.c_str();
            configuration_->logFilePath = liveConfiguration->logFilePath = safe_copy_string(fileName, NULL);
        } else {
            configuration_->logFilePath = liveConfiguration->logFilePath;
        }

        logFile = new ofstream(fileName, ofstream::out | ofstream::binary);
        if (logFile->fail()) {
            // The JVM will still continue to run though; could call abort() to terminate the JVM abnormally.
            logError("ERROR: Failed to open file %s for writing\n", fileName);
        }
        writer = new LogWriter(*logFile, &Profiler::lookupFrameInformation, jvmti_);
    }
    
    needsUpdate = needsUpdate || configuration_->maxFramesToCapture != liveConfiguration->maxFramesToCapture;
    if (needsUpdate) {
        if (buffer) delete buffer;
        configuration_->maxFramesToCapture = liveConfiguration->maxFramesToCapture;
        buffer = new CircularQueue(*writer, configuration_->maxFramesToCapture);
    }
    
    needsUpdate = needsUpdate || 
        configuration_->samplingIntervalMin != liveConfiguration->samplingIntervalMin || 
        configuration_->samplingIntervalMax != liveConfiguration->samplingIntervalMax;
    if (needsUpdate) {
        if (processor) delete processor;
        if (handler_) delete handler_;
        configuration_->samplingIntervalMin = liveConfiguration->samplingIntervalMin;
        configuration_->samplingIntervalMax = liveConfiguration->samplingIntervalMax;
        handler_ = new SignalHandler(configuration_->samplingIntervalMin, configuration_->samplingIntervalMax);
        int processor_interval = Size * configuration_->samplingIntervalMin / 1000 / 2;
        processor = new Processor(jvmti_, *writer, *buffer, *handler_, processor_interval > 0 ? processor_interval : 1);
    }
    initialized = true;
}
