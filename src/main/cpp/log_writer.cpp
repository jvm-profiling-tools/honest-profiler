#include "log_writer.h"
#include <cstdlib>
using std::copy;

bool isLittleEndian() {
    short int number = 0x1;
    char *numPtr = (char *) &number;
    return (numPtr[0] == 1);
}

static bool IS_LITTLE_ENDIAN = isLittleEndian();

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

void LogWriter::record(const JVMPI_CallTrace &trace, ThreadBucket *info) {
    timespec spec;
    TimeUtils::current_utc_time(&spec);

    record(spec, trace, info);
}

void LogWriter::record(const timespec &ts, const JVMPI_CallTrace &trace, ThreadBucket *info) {
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

    if (info != nullptr)
        info->release();
}

void LogWriter::inspectMethod(const method_id methodId,
        const JVMPI_CallFrame &frame) {
    if (knownMethods.count(methodId) > 0) {
        return;
    }

    knownMethods.insert(methodId);
    frameLookup_(frame, jvmti_, *this);
}

void LogWriter::inspectThread(map::HashType &threadId, ThreadBucket *info) {
    char *threadName = (char*)"";

    if (info) {
        threadId = (map::HashType) info->tid;
        threadName = info->name;
    }

    if (knownThreads.find(threadId) != knownThreads.end()) {
        return;
    }

    knownThreads.insert(threadId);

    output_.put(THREAD_META);
    writeValue(threadId);
    writeWithSize(threadName);
    output_.flush();
}

void LogWriter::recordTraceStart(const jint numFrames, map::HashType envHash, ThreadBucket *info) {
    map::HashType threadId = -envHash;

    inspectThread(threadId, info);

    output_.put(TRACE_START);
    writeValue(numFrames);
    writeValue(threadId);
    output_.flush();
}

void LogWriter::recordTraceStart(const jint numFrames, map::HashType envHash, const timespec &ts, ThreadBucket *info) {
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
