#include "log_writer.h"

#include <cstdio>
#include <cstdlib>
#include <thread>
#include <iostream>

using std::copy;

bool isLittleEndian() {
    short int number = 0x1;
    char *numPtr = (char *) &number;
    return (numPtr[0] == 1);
}

static bool IS_LITTLE_ENDIAN = isLittleEndian();

LogWriter::LogWriter(std::string &fileName, int rotateNum, int rotateSizeMB, jvmtiEnv *jvmti) :
    fileName(fileName),
    rotateNum(rotateNum),
    rotateSize(rotateSizeMB * 1024 * 1024),
    size(0),
    file(NULL),
    output_(NULL),
    frameInfoFoo(NULL), jvmti_(jvmti) {
    file = new std::ofstream(fileName, std::ofstream::out | std::ofstream::binary);
    output_ = this->file;
    if (output_->fail()) {
        // The JVM will still continue to run though; could call abort() to terminate the JVM abnormally.
        logError("ERROR: Failed to open file %s for writing\n", fileName.c_str());
    }
}

LogWriter::LogWriter(ostream &output, int rotateNum, int rotateSizeMB, GetFrameInformation frameLookup, jvmtiEnv *jvmti) :
    fileName(),
    rotateNum(rotateNum),
    rotateSize(rotateSizeMB * 1024 * 1024),
    size(0),
    file(NULL),
    output_(&output),
    frameInfoFoo(frameLookup),
    jvmti_(jvmti) {
    // Old interface for backward compatibility and testing purposes
}

template<typename T>
void LogWriter::writeValue(ostream & fout, const T &value) {
    if (IS_LITTLE_ENDIAN) {
        const char *data = reinterpret_cast<const char *>(&value);
        for (int i = sizeof(value) - 1; i >= 0; i--) {
            fout.put(data[i]);
        }
    } else {
        fout.write(reinterpret_cast<const char *>(&value), sizeof(value));
    }
    size += sizeof(value);
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
    ostream & fout = getOut();
    recordTraceStart(fout, trace.num_frames, (map::HashType)trace.env_id, ts, info);

    for (int i = 0; i < trace.num_frames; i++) {
        JVMPI_CallFrame frame = trace.frames[i];
        method_id methodId = (method_id) frame.method_id;

        // lineno is in fact BCI, needs converting to lineno
        jint bci = frame.lineno;
        if (bci > 0) {
            jint lineno = getLineNo(bci, frame.method_id);
            recordFrame(fout, bci, lineno, methodId);
        }
        else {
            recordFrame(fout, bci, methodId);
        }
        inspectMethod(fout, methodId, frame);
    }
}

void LogWriter::inspectMethod(ostream & fout, const method_id methodId, const JVMPI_CallFrame &frame) {
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

void LogWriter::inspectThread(ostream & fout, map::HashType &threadId, ThreadBucketPtr& info) {
    std::string threadName;

    if (info.defined()) {
        threadId = (map::HashType) info->tid;
        threadName = info->name;
    }

    if (knownThreads.find(threadId) != knownThreads.end()) {
        return;
    }

    knownThreads.insert(threadId);

    fout.put(THREAD_META);
    size++;
    writeValue(fout, threadId);
    writeWithSize(fout, threadName.c_str());
    fout.flush();
}

void LogWriter::recordTraceStart(ostream & fout, const jint numFrames, map::HashType envHash, ThreadBucketPtr& info) {
    map::HashType threadId = -envHash;

    inspectThread(fout, threadId, info);

    fout.put(TRACE_START);
    size++;
    writeValue(fout, numFrames);
    writeValue(fout, threadId);
    fout.flush();
}

void LogWriter::recordTraceStart(ostream & fout, const jint numFrames, map::HashType envHash, const timespec &ts, ThreadBucketPtr& info) {
    map::HashType threadId = -envHash; // mark unrecognized threads with negative id's
    
    inspectThread(fout, threadId, info);

    fout.put(TRACE_WITH_TIME);
    size++;
    writeValue(fout, numFrames);
    writeValue(fout, threadId);
    writeValue(fout, (int64_t)ts.tv_sec);
    writeValue(fout, (int64_t)ts.tv_nsec);
    fout.flush();
}

void LogWriter::recordFrame(ostream & fout, const jint bci, const jint lineNumber, const method_id methodId) {
    fout.put(FRAME_FULL);
    size++;
    writeValue(fout, bci);
    writeValue(fout, lineNumber);
    writeValue(fout, methodId);
    fout.flush();
}

// kept for old format tests
void LogWriter::recordFrame(ostream & fout, const jint bci, const method_id methodId) {
    fout.put(FRAME_BCI_ONLY);
    size++;
    writeValue(fout, bci);
    writeValue(fout, methodId);
    fout.flush();
}

void LogWriter::writeWithSize(ostream & fout, const char *value) {
    jint size = (jint) strlen(value);
    writeValue(fout, size);
    fout.write(value, size);
    this->size+=sizeof(jint);
}

void LogWriter::recordNewMethod(const map::HashType methodId, const char *fileName,
        const char *className, const char *methodName) {
    ostream & fout = getOut();
    fout.put(NEW_METHOD);
    size++;
    writeValue(fout, methodId);
    writeWithSize(fout, fileName);
    writeWithSize(fout, className);
    writeWithSize(fout, methodName);
    fout.flush();
}

void LogWriter::recordNewMethod(const map::HashType methodId, const char *fileName, 
    const char *className, const char *genericClassName, 
    const char *methodName, const char *methodSignature, const char *genericMethodSignature) {
    ostream & fout = getOut();
    fout.put(NEW_METHOD_SIGNATURE);
    size++;
    writeValue(fout, methodId);
    writeWithSize(fout, fileName);
    writeWithSize(fout, className);
    writeWithSize(fout, genericClassName ? genericClassName : "");
    writeWithSize(fout, methodName);
    writeWithSize(fout, methodSignature);
    writeWithSize(fout, genericMethodSignature ? genericMethodSignature : "");
    fout.flush();
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

ostream& LogWriter::getOut() {
    // std::thread::id this_id = std::this_thread::get_id();
    if (!fileName.empty() && size >= rotateSize) {
        // rotate
        file->close();
        delete file;
        file = NULL;
        size = 0;
        // std::cout <<" threadId: " << this_id << " rotating file start >>> \n";
        for (int i = rotateNum; i > 0; --i) {
            // rename files: delete logN, rename logN-1 to logN; ...; delete log1, log to log1
            char buff[1024];
            if (i > 1) {
                snprintf(buff, sizeof(buff), "%s.%d", fileName.c_str(), i - 1);
            } else {
                snprintf(buff, sizeof(buff), "%s", fileName.c_str());
            }
            char buff_target[1024];
            snprintf(buff_target, sizeof(buff_target), "%s.%d", fileName.c_str(), i);

            // std::cout <<" threadId: " << this_id << " remove target " << buff_target << " \n";
            std::remove(buff_target);
            std::rename(buff, buff_target);
            // std::cout <<" threadId: " << this_id << " rename from " << buff << " to " <<  buff_target << " \n";
        }
        // std::cout <<" threadId: " << "rotating file end <<< \n";
        // recreate log
        file = new std::ofstream(fileName, std::ofstream::out | std::ofstream::binary);
        output_ = file;
        return *output_;
    } else {
        // std::cout <<" threadId: " << "rotateSize: " << rotateSize <<
        //      ", rotateNum: " << rotateNum << " current size: " << size << "\n";
        return *output_;
    }
}

LogWriter::~LogWriter() {
    if (file != NULL) {
        file->close();
        delete file;
        file = NULL;
    }
}
