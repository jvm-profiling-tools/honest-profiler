#include <signal.h>
#include <fstream>

#include "globals.h"
#include "stacktraces.h"
#include "processor.h"
#include "log_writer.h"

#ifndef PROFILER_H
#define PROFILER_H

using std::ofstream;

class SignalHandler {
public:
    SignalHandler() {
    }

    struct sigaction SetAction(void (*sigaction)(int, siginfo_t *, void *));

    bool SetSigprofInterval(int sec, int usec);

private:
    DISALLOW_COPY_AND_ASSIGN(SignalHandler);
};


class Profiler {
public:
    explicit Profiler(jvmtiEnv *jvmti) : jvmti_(jvmti) {
        // main object graph instantiated here
        // these objects all live for the lifecycle of the program

        const char *fileName = "log.hpl";
        logFile = new ofstream(fileName, ofstream::out | ofstream::binary);

        writer = new LogWriter(*logFile, &Profiler::lookupFrameInformation, jvmti);
        buffer = new CircularQueue(*writer);
        processor = new Processor(jvmti, *writer, *buffer);
    }

    bool start(JNIEnv *jniEnv);

    void stop();

    void handle(int signum, siginfo_t *info, void *context);

    ~Profiler() {
        delete buffer;
        delete logFile;
        delete writer;
        delete processor;
    }

private:
    jvmtiEnv *jvmti_;

    ostream *logFile;

    LogWriter *writer;

    CircularQueue *buffer;

    Processor *processor;

    SignalHandler handler_;

    static int failures_[kNumCallTraceErrors + 1]; // they are indexed from 1

    static bool lookupFrameInformation(const JVMPI_CallFrame &frame,
            jvmtiEnv *jvmti,
            MethodListener &logWriter);

    DISALLOW_COPY_AND_ASSIGN(Profiler);
};

#endif // PROFILER_H
