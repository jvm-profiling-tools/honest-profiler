#include <cstdlib>

#include "log_writer.h"

using std::copy;

bool isLittleEndian() {
    short int number = 0x1;
    char *numPtr = (char *) &number;
    return (numPtr[0] == 1);
}

static bool IS_LITTLE_ENDIAN = isLittleEndian();

LogWriter::LogWriter(std::string &fileName, jvmtiEnv *jvmti) : 
    file(fileName, std::ofstream::out | std::ofstream::binary), output_(this->file), 
    frameInfoFoo(NULL), jvmti_(jvmti) {
    if (output_.fail()) {
        // The JVM will still continue to run though; could call abort() to terminate the JVM abnormally.
        logError("ERROR: Failed to open file %s for writing\n", fileName.c_str());
    }
}

LogWriter::LogWriter(ostream &output, GetFrameInformation frameLookup, jvmtiEnv *jvmti) :
    file(), output_(output), frameInfoFoo(frameLookup), jvmti_(jvmti) {
    // Old interface for backward compatibility and testing purposes
}

template<typename T>
void LogWriter::writeValue(const T &value) {
    if (IS_LITTLE_ENDIAN) {
        const char *data = reinterpret_cast<const char *>(&value);
        for (int i = sizeof(value) - 1; i >= 0; i--) {
            output_.put(data[i]);
        }
    } else {
        output_.write(reinterpret_cast<const char *>(&value), sizeof(value));
    }
}

static jint bci2line(jint bci, jvmtiLineNumberEntry *table, jint entry_count) {
	jint line_number = -101;
	if ( entry_count == 0 ) {
		return line_number;
	}
	line_number = -102;
    // We're looking for a line whose 'start_location' is nearest AND >= BCI
	// We assume the table is sorted by 'start_location'
    // Do a binary search to quickly approximate 'start_index" in table
	int half = entry_count >> 1;
    int start_index = 0;
    while ( half > 0 ) {
        jint start_location = table[start_index + half].start_location;
        if ( bci > start_location ) {
            start_index = start_index + half;
        } else if ( bci == start_location ) {
        	// gotcha
            return table[start_index + half].line_number;
        }
        half = half >> 1;
    }

    /* Now start the table search from approximated start_index */
    for (int i = start_index ; i < entry_count ; i++ ) {
    	// start_location > BCI: means line starts after the BCI, we'll take the previous match
        if ( bci < table[i].start_location ) {
            break;
        }
        else if (bci == table[i].start_location) {
        	// gotcha
        	return table[i].line_number;
        }
        line_number = table[i].line_number;
    }
    return line_number;
}

jint LogWriter::getLineNo(jint bci, jmethodID methodId) {
  if(bci <= 0) {
      return bci;
    }

    JvmtiScopedPtr<jvmtiLineNumberEntry> jvmti_table(jvmti_);
    jint entry_count;

    JVMTI_ERROR_CLEANUP_RET_NO_MESSAGE(
        jvmti_->GetLineNumberTable(methodId, &entry_count, jvmti_table.GetRef()),
        -100,
        jvmti_table.AbandonBecauseOfError());

    jint lineno = bci2line(bci, jvmti_table.Get(), entry_count);


    return lineno;
}

void LogWriter::record(const JVMPI_CallTrace &trace, ThreadBucketPtr info) {
    timespec spec;
    TimeUtils::current_utc_time(&spec);

    record(spec, trace, std::move(info));
}

void LogWriter::record(const timespec &ts, const JVMPI_CallTrace &trace, ThreadBucketPtr info) {
    recordTraceStart(trace.num_frames, (map::HashType)trace.env_id, ts, info);

    for (int i = 0; i < trace.num_frames; i++) {
        JVMPI_CallFrame frame = trace.frames[i];
        method_id methodId = (method_id) frame.method_id;

        // lineno is in fact BCI, needs converting to lineno
        jint bci = frame.lineno;
        if (bci > 0) {
            jint lineno = getLineNo(bci, frame.method_id);
            recordFrame(bci, lineno, methodId);
        }
        else {
            recordFrame(bci, methodId);
        }
        inspectMethod(methodId, frame);
    }
}

void LogWriter::inspectMethod(const method_id methodId, const JVMPI_CallFrame &frame) {
    if (knownMethods.count(methodId) > 0) {
        return;
    }

    knownMethods.insert(methodId);

    if (frameInfoFoo) { 
        frameInfoFoo(frame, *this);
    } else {
        lookupFrameInformation(frame);
    }
}

void LogWriter::inspectThread(map::HashType &threadId, ThreadBucketPtr& info) {
    std::string threadName;

    if (info.defined()) {
        threadId = (map::HashType) info->tid;
        threadName = info->name;
    }

    if (knownThreads.find(threadId) != knownThreads.end()) {
        return;
    }

    knownThreads.insert(threadId);

    output_.put(THREAD_META);
    writeValue(threadId);
    writeWithSize(threadName.c_str());
    output_.flush();
}

void LogWriter::recordTraceStart(const jint numFrames, map::HashType envHash, ThreadBucketPtr& info) {
    map::HashType threadId = -envHash;

    inspectThread(threadId, info);

    output_.put(TRACE_START);
    writeValue(numFrames);
    writeValue(threadId);
    output_.flush();
}

void LogWriter::recordTraceStart(const jint numFrames, map::HashType envHash, const timespec &ts, ThreadBucketPtr& info) {
    map::HashType threadId = -envHash; // mark unrecognized threads with negative id's
    
    inspectThread(threadId, info);

    output_.put(TRACE_WITH_TIME);
    writeValue(numFrames);
    writeValue(threadId);
    writeValue((int64_t)ts.tv_sec);
    writeValue((int64_t)ts.tv_nsec);
    output_.flush();
}

void LogWriter::recordFrame(const jint bci, const jint lineNumber, const method_id methodId) {
    output_.put(FRAME_FULL);
    writeValue(bci);
    writeValue(lineNumber);
    writeValue(methodId);
    output_.flush();
}

// kept for old format tests
void LogWriter::recordFrame(const jint bci, const method_id methodId) {
    output_.put(FRAME_BCI_ONLY);
    writeValue(bci);
    writeValue(methodId);
    output_.flush();
}

void LogWriter::writeWithSize(const char *value) {
    jint size = (jint) strlen(value);
    writeValue(size);
    output_.write(value, size);
}

void LogWriter::recordNewMethod(const map::HashType methodId, const char *fileName,
        const char *className, const char *methodName) {
    output_.put(NEW_METHOD);
    writeValue(methodId);
    writeWithSize(fileName);
    writeWithSize(className);
    writeWithSize(methodName);
    output_.flush();
}

void LogWriter::recordNewMethod(const map::HashType methodId, const char *fileName, 
    const char *className, const char *genericClassName, 
    const char *methodName, const char *methodSignature, const char *genericMethodSignature) {
    output_.put(NEW_METHOD_SIGNATURE);
    writeValue(methodId);
    writeWithSize(fileName);
    writeWithSize(className);
    writeWithSize(genericClassName ? genericClassName : "");
    writeWithSize(methodName);
    writeWithSize(methodSignature);
    writeWithSize(genericMethodSignature ? genericMethodSignature : "");
    output_.flush();
}

bool LogWriter::lookupFrameInformation(const JVMPI_CallFrame &frame) {
    jint error;
    JvmtiScopedPtr<char> methodName(jvmti_), methodSignature(jvmti_), methodGenericSignature(jvmti_);

    error = jvmti_->GetMethodName(frame.method_id, methodName.GetRef(), methodSignature.GetRef(), methodGenericSignature.GetRef());
    if (error != JVMTI_ERROR_NONE) {
        methodName.AbandonBecauseOfError();
        methodSignature.AbandonBecauseOfError();
        methodGenericSignature.AbandonBecauseOfError();
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
        jvmti_->GetMethodDeclaringClass(frame.method_id, &declaring_class), false);

    JvmtiScopedPtr<char> classSignature(jvmti_), classSignatureGeneric(jvmti_);
    JVMTI_ERROR_CLEANUP_RET(
        jvmti_->GetClassSignature(declaring_class, classSignature.GetRef(), classSignatureGeneric.GetRef()),
        false, { classSignature.AbandonBecauseOfError(); classSignatureGeneric.AbandonBecauseOfError(); });

    // Get source file, put it in source_name_ptr
    char *fileName;
    JvmtiScopedPtr<char> source_name_ptr(jvmti_);
    static char file_unknown[] = "UnknownFile";
    if (JVMTI_ERROR_NONE != jvmti_->GetSourceFileName(declaring_class, source_name_ptr.GetRef())) {
        source_name_ptr.AbandonBecauseOfError();
        fileName = file_unknown;
    } else {
        fileName = source_name_ptr.Get();
    }

    recordNewMethod(
        (method_id) frame.method_id,
        fileName,
        classSignature.Get(),
        classSignatureGeneric.Get(),
        methodName.Get(),
        methodSignature.Get(),
        methodGenericSignature.Get()
    );

    return true;
}