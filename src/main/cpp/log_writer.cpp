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

// TODO: implement
static int64_t getThreadId(JNIEnv *env_id) {
    return (int64_t) env_id;
}

jint LogWriter::getLineNo(jint bci, jmethodID methodId) {
	if(bci <= 0) {
		return bci;
	}
	// TODO: cache line number tables?
	// Caching is risky as the growth is not capped. An alternative is to serialize the line table once per method
	// but might bloat log? current approach is brute..
	jvmtiLineNumberEntry* jvmti_table;
	jint entry_count;
	jvmtiError err = jvmti_->GetLineNumberTable(methodId, &entry_count, &jvmti_table);
	if (err != JVMTI_ERROR_NONE) {
		return -100;
	}
	jint min_d = 64*1024;
	jint lineno = -101;
	for(int i=0;i<entry_count-1;i++) {
		if (jvmti_table[i].start_location == bci) {
			delete(jvmti_table);
			return jvmti_table[i].line_number;
		}
		else {
			jint d = abs(jvmti_table[i].start_location - bci);
			if (min_d > d) {
				lineno =  jvmti_table[i].line_number;
				min_d = d;
			}
		}
	}
	delete(jvmti_table);
	return lineno;
}

void LogWriter::record(const JVMPI_CallTrace &trace) {
    int64_t threadId = getThreadId(trace.env_id);
    recordTraceStart(trace.num_frames, threadId);

    for (int i = 0; i < trace.num_frames; i++) {
        JVMPI_CallFrame frame = trace.frames[i];
        method_id methodId = (method_id) frame.method_id;
	   // lineno is in fact BCI, needs converting to lineno
	    jint lineno = getLineNo(frame.lineno, frame.method_id);
        recordFrame(lineno, methodId);
        inspectMethod(methodId, frame);
    }
}

void LogWriter::inspectMethod(const method_id methodId,
        const JVMPI_CallFrame &frame) {
    if (knownMethods.count(methodId) > 0) {
        return;
    }

    knownMethods.insert(methodId);
    frameLookup_(frame, jvmti_, *this);
}

void LogWriter::recordTraceStart(const jint numFrames, const int64_t threadId) {
    output_.put(TRACE_START);
    writeValue(numFrames);
    writeValue(threadId);
    output_.flush();
}

void LogWriter::recordFrame(const jint lineNumber, const method_id methodId) {
    output_.put(FRAME);
    writeValue(lineNumber);
    writeValue(methodId);
    output_.flush();
}

void LogWriter::writeWithSize(const char *value) {
    jint size = (jint) strlen(value);
    writeValue(size);
    output_.write(value, size);
}

void LogWriter::recordNewMethod(const int64_t methodId, const char *fileName,
        const char *className, const char *methodName) {
    output_.put(NEW_METHOD);
    writeValue(methodId);
    writeWithSize(fileName);
    writeWithSize(className);
    writeWithSize(methodName);
    output_.flush();
}
