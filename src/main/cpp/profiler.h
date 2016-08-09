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
    explicit Profiler(JavaVM *jvm, jvmtiEnv *jvmti, ConfigurationOptions *configuration) 
            : jvm_(jvm), jvmti_(jvmti), configuration_(configuration),
            logFile(NULL), writer(NULL), buffer(NULL), processor(NULL), handler_(NULL), initialized(false) {
        // main object graph instantiated here
        // these objects all live for the lifecycle of the program
        liveConfiguration = new ConfigurationOptions(*configuration);
    }

    bool start(JNIEnv *jniEnv);

    void stop();

    void handle(int signum, siginfo_t *info, void *context);

    bool isRunning() const;

    /* Several getters and setters for externals APIs */

    char* getFilePath() const { return liveConfiguration->logFilePath; }

    int getSamplingIntervalMin() const { return liveConfiguration->samplingIntervalMin; }

    int getSamplingIntervalMax() const { return liveConfiguration->samplingIntervalMax; }

    int getMaxFramesToCapture() const { return liveConfiguration->maxFramesToCapture; }

    void setFilePath(char *newFilePath);

    void setSamplingInterval(int intervalMin, int intervalMax);

    void setMaxFramesToCapture(int maxFramesToCapture);

    ~Profiler() {
        if (initialized) {
            delete buffer;
            delete logFile;
            delete writer;
            delete processor;
            delete handler_;
            delete liveConfiguration;
        }
    }

private:
    void configure();

    JavaVM *jvm_;

    jvmtiEnv *jvmti_;

    ConfigurationOptions *configuration_;

    ConfigurationOptions *liveConfiguration;

    ostream *logFile;

    LogWriter *writer;

    CircularQueue *buffer;

    Processor *processor;

    SignalHandler* handler_;

    bool initialized;

    static bool lookupFrameInformation(const JVMPI_CallFrame &frame,
            jvmtiEnv *jvmti,
            MethodListener &logWriter);

    static string generateFileName();

    DISALLOW_COPY_AND_ASSIGN(Profiler);
};

#endif // PROFILER_H
