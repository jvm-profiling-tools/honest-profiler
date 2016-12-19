#include "profiler.h"

ASGCTType Asgct::asgct_;

TRACE_DEFINE_BEGIN(Profiler, kTraceProfilerTotal)
    TRACE_DEFINE("start failed")
    TRACE_DEFINE("start succeeded")
    TRACE_DEFINE("set sampling interval failed")
    TRACE_DEFINE("set sampling interval succeeded")
    TRACE_DEFINE("set stack frames to capture failed")
    TRACE_DEFINE("set stack frames to capture succeeded")
    TRACE_DEFINE("set new file failed")
    TRACE_DEFINE("set new file succeeded")
    TRACE_DEFINE("stop failed")
    TRACE_DEFINE("stop succeeded")
TRACE_DEFINE_END(Profiler, kTraceProfilerTotal);

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
    SimpleSpinLockGuard<false> guard(ongoingConf); // sync buffer
    ThreadBucket *threadInfo;
    timespec spec;

    // sample data structure
    STATIC_ARRAY(frames, JVMPI_CallFrame, configuration_->maxFramesToCapture, MAX_FRAMES_TO_CAPTURE);

    JVMPI_CallTrace trace;
    trace.frames = frames;
    JNIEnv *jniEnv = getJNIEnv(jvm_);
    TimeUtils::current_utc_time(&spec); // sample current time
    if (jniEnv == NULL) {
        trace.num_frames = -3; // ticks_unknown_not_Java
        threadInfo = nullptr;
    } else {
        trace.env_id = jniEnv;
        ASGCTType asgct = Asgct::GetAsgct();
        (*asgct)(&trace, configuration_->maxFramesToCapture, context);
        threadInfo = tMap_.get(jniEnv);
    }

    // log all samples, failures included, let the post processing sift through the data
    buffer->push(spec, trace, threadInfo);
}

bool Profiler::start(JNIEnv *jniEnv) {
    SimpleSpinLockGuard<true> guard(ongoingConf);
    /* within critical section */

    if (__is_running()) {
        TRACE(Profiler, kTraceProfilerStartFailed);
        logError("WARN: Start called but sampling is already running\n");
        return true;
    }

    TRACE(Profiler, kTraceProfilerStartOk);

    if (reloadConfig)
        configure();

    // reference back to Profiler::handle on the singleton
    // instance of Profiler
    handler_->SetAction(&bootstrapHandle);
    processor->start(jniEnv);
    bool res = handler_->updateSigprofInterval();
    return res;
}

void Profiler::stop() {
    /* Make sure it doesn't overlap with configure */
    SimpleSpinLockGuard<true> guard(ongoingConf);

    if (!__is_running()) {
        TRACE(Profiler, kTraceProfilerStopFailed);
        return;
    }

    handler_->stopSigprof();
    processor->stop();
    signal(SIGPROF, SIG_IGN);
}

bool Profiler::isRunning() {
    /* Make sure it doesn't overlap with configure */
    SimpleSpinLockGuard<true> guard(ongoingConf, false);
    bool res = __is_running();
    return res;
}

// non-blocking version (cen be called once spin-lock with acquire semantics is grabed)
bool Profiler::__is_running() {
    return processor && processor->isRunning();
}

void Profiler::setFilePath(char *newFilePath) {
    /* Make sure it doesn't overlap with other sets */
    SimpleSpinLockGuard<true> guard(ongoingConf);

    if (__is_running()) {
        TRACE(Profiler, kTraceProfilerSetFileFailed);
        logError("WARN: Unable to modify running profiler\n");
        return;
    }

    TRACE(Profiler, kTraceProfilerSetFileOk);

    if (liveConfiguration->logFilePath &&
            liveConfiguration->logFilePath != configuration_->logFilePath)
        safe_free_string(liveConfiguration->logFilePath);

    /* make local copy of string */
    liveConfiguration->logFilePath = newFilePath ? safe_copy_string(newFilePath, NULL) : NULL;
    reloadConfig = true;
}

void Profiler::setSamplingInterval(int intervalMin, int intervalMax) {
    /* Make sure it doesn't overlap with other sets */
    SimpleSpinLockGuard<true> guard(ongoingConf);

    if (__is_running()) {
        TRACE(Profiler, kTraceProfilerSetIntervalFailed);
        logError("WARN: Unable to modify running profiler\n");
        return;
    }

    TRACE(Profiler, kTraceProfilerSetIntervalOk);

    int min = intervalMin > 0 ? intervalMin : DEFAULT_SAMPLING_INTERVAL;
    int max = intervalMax > 0 ? intervalMax : DEFAULT_SAMPLING_INTERVAL;
    liveConfiguration->samplingIntervalMin = std::min(min, max);
    liveConfiguration->samplingIntervalMax = std::max(min, max);
    reloadConfig = true;
}

void Profiler::setMaxFramesToCapture(int maxFramesToCapture) {
    /* Make sure it doesn't overlap with other sets */
    SimpleSpinLockGuard<true> guard(ongoingConf);

    if (__is_running()) {
        TRACE(Profiler, kTraceProfilerSetFramesFailed);
        logError("WARN: Unable to modify running profiler\n");
        return;
    }

    TRACE(Profiler, kTraceProfilerSetFramesOk);

    int res = (maxFramesToCapture > 0 && maxFramesToCapture < MAX_FRAMES_TO_CAPTURE) ?
              maxFramesToCapture : DEFAULT_MAX_FRAMES_TO_CAPTURE;
    liveConfiguration->maxFramesToCapture = res;
    reloadConfig = true;
}

/* return copy of the string */
std::string Profiler::getFilePath() {
    /* Make sure it doesn't overlap with setFilePath */
    SimpleSpinLockGuard<true> guard(ongoingConf, true); // relaxed store
    std::string res;

    if (liveConfiguration->logFilePath)
        res = std::string(liveConfiguration->logFilePath);

    return res;
}

int Profiler::getSamplingIntervalMin() {
    SimpleSpinLockGuard<false> guard(ongoingConf); // nonblocking
    return liveConfiguration->samplingIntervalMin;
}

int Profiler::getSamplingIntervalMax() {
    SimpleSpinLockGuard<false> guard(ongoingConf); // nonblocking
    return liveConfiguration->samplingIntervalMax;
}

int Profiler::getMaxFramesToCapture() {
    SimpleSpinLockGuard<false> guard(ongoingConf); // nonblocking
    return liveConfiguration->maxFramesToCapture;
}

void Profiler::configure() {
    /* nested critical section, no need to acquire or CAS */
    bool needsUpdate = processor == NULL;

    needsUpdate = needsUpdate || configuration_->logFilePath != liveConfiguration->logFilePath;
    if (needsUpdate) {
        if (logFile) delete logFile;
        if (writer) delete writer;
        if (configuration_->logFilePath)
            safe_free_string(configuration_->logFilePath);

        char *fileName = liveConfiguration->logFilePath;
        string fileNameStr;
        if (fileName == NULL) {
            ostringstream fileBuilder;
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
    reloadConfig = false;
}

Profiler::~Profiler() {
    SimpleSpinLockGuard<false> guard(ongoingConf); // nonblocking
    /* liveConfiguration is managed in agent.cpp */
    if (liveConfiguration->logFilePath == configuration_->logFilePath)
        configuration_->logFilePath = NULL;
    delete processor;
    delete handler_;
    delete buffer;
    delete writer;
    delete logFile;
    delete configuration_;
}
