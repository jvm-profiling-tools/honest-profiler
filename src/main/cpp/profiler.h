#ifndef PROFILER_H
#define PROFILER_H

#include <signal.h>
#include <unistd.h>
#include <chrono>
#include <sstream>
#include <string>

#include "globals.h"
#include "signal_handler.h"
#include "stacktraces.h"
#include "processor.h"
#include "log_writer.h"

using namespace std::chrono;
using std::ofstream;
using std::ostringstream;
using std::string;

class Profiler {
public:
    explicit Profiler(JavaVM *jvm, jvmtiEnv *jvmti, ConfigurationOptions *configuration) : jvm_(jvm), configuration_(configuration), handler_(configuration_->samplingInterval) {
        // main object graph instantiated here
        // these objects all live for the lifecycle of the program

        long pid = (long) getpid();
        long epochMillis = duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();

        char* fileName = configuration->logFilePath;
        string fileNameStr;
        if (fileName == NULL) {
            ostringstream fileBuilder;
            fileBuilder << "log-" << pid << "-" << epochMillis << ".hpl";
            fileNameStr = fileBuilder.str();
            fileName = (char *) fileNameStr.c_str();
        }

        logFile = new ofstream(fileName, ofstream::out | ofstream::binary);

        writer = new LogWriter(*logFile, &Profiler::lookupFrameInformation, jvmti);
        buffer = new CircularQueue(*writer);
        processor = new Processor(jvmti, *writer, *buffer, handler_);
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
    JavaVM *jvm_;

    ConfigurationOptions *configuration_;

    ostream *logFile;

    LogWriter *writer;

    CircularQueue *buffer;

    Processor *processor;

    SignalHandler handler_;

    static bool lookupFrameInformation(const JVMPI_CallFrame &frame,
            jvmtiEnv *jvmti,
            MethodListener &logWriter);

    JNIEnv *getJNIEnv();

    DISALLOW_COPY_AND_ASSIGN(Profiler);
};

#endif // PROFILER_H
