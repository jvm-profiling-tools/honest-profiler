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

#include "trace.h"

const int kTraceProfilerTotal = 10;

const int kTraceProfilerStartFailed = 0;
const int kTraceProfilerStartOk = 1;
const int kTraceProfilerSetIntervalFailed = 2;
const int kTraceProfilerSetIntervalOk = 3;
const int kTraceProfilerSetFramesFailed = 4;
const int kTraceProfilerSetFramesOk = 5;
const int kTraceProfilerSetFileFailed = 6;
const int kTraceProfilerSetFileOk = 7;
const int kTraceProfilerStopFailed = 8;
const int kTraceProfilerStopOk = 9;

TRACE_DECLARE(Profiler, kTraceProfilerTotal);


class Profiler {
public:
    explicit Profiler(JavaVM *jvm, jvmtiEnv *jvmti, ConfigurationOptions *configuration) 
            : jvm_(jvm), jvmti_(jvmti), liveConfiguration(configuration),
            logFile(NULL), writer(NULL), buffer(NULL), processor(NULL), handler_(NULL),
            ongoingConf(false) {
        // main object graph instantiated here
        // these objects all live for the lifecycle of the program
        configuration_ = new ConfigurationOptions();
        pid = (long) getpid();

        // explicitly call setters to validate input params
        setSamplingInterval(liveConfiguration->samplingIntervalMin, 
            liveConfiguration->samplingIntervalMax);
        setMaxFramesToCapture(liveConfiguration->maxFramesToCapture);
        
        configure();
    }

    bool start(JNIEnv *jniEnv);

    void stop();

    void handle(int signum, siginfo_t *info, void *context);

    bool isRunning();

    /* Several getters and setters for externals APIs */

    std::string getFilePath();

    int getSamplingIntervalMin() const;

    int getSamplingIntervalMax() const;

    int getMaxFramesToCapture() const;

    void setFilePath(char *newFilePath);

    void setSamplingInterval(int intervalMin, int intervalMax);

    void setMaxFramesToCapture(int maxFramesToCapture);

    ~Profiler();

private:
    JavaVM *jvm_;

    jvmtiEnv *jvmti_;

    ConfigurationOptions *configuration_;

    ConfigurationOptions *liveConfiguration;

    ostream *logFile;

    LogWriter *writer;

    CircularQueue *buffer;

    Processor *processor;

    SignalHandler* handler_;

    bool reloadConfig;

    long pid;

    // indicates change of internal state
    std::atomic<bool> ongoingConf;

    static bool lookupFrameInformation(const JVMPI_CallFrame &frame,
            jvmtiEnv *jvmti,
            MethodListener &logWriter);

    void configure();

    bool __is_running();

    DISALLOW_COPY_AND_ASSIGN(Profiler);
};

#endif // PROFILER_H
