#ifndef PROFILER_H
#define PROFILER_H

#include <signal.h>
#include <unistd.h>
#include <chrono>
#include <sstream>
#include <string>
#include <memory>

#include "thread_map.h"
#include "signal_handler.h"
#include "stacktraces.h"
#include "processor.h"
#include "log_writer.h"

using namespace std::chrono;
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

template <bool blocking = true>
class SimpleSpinLockGuard {
private:
    std::atomic_bool& f;
    bool rel;

public:
    SimpleSpinLockGuard(std::atomic_bool& field, bool relaxed = false) : f(field), rel(relaxed) {
        bool expectedState = false;
        while (!f.compare_exchange_weak(expectedState, true, std::memory_order_acquire)) {
            expectedState = false;
            sched_yield();
        }
    }

    ~SimpleSpinLockGuard() {
        f.store(false, rel ? std::memory_order_relaxed : std::memory_order_release);
    }
};

template <>
class SimpleSpinLockGuard<false> {
public:
    SimpleSpinLockGuard(std::atomic_bool& field) {
        field.load(std::memory_order_acquire);
    }

    ~SimpleSpinLockGuard() {}
};

class Profiler {
public:
    explicit Profiler(JavaVM *jvm, jvmtiEnv *jvmti, ConfigurationOptions &configuration, ThreadMap &tMap)
        : jvm_(jvm), jvmti_(jvmti), tMap_(tMap), liveConfiguration(configuration), ongoingConf(false) {
        pid = (long) getpid();

        writer = nullptr; 
        processor = nullptr;

        // explicitly call setters to validate input params
        setSamplingInterval(liveConfiguration.samplingIntervalMin, liveConfiguration.samplingIntervalMax);
        setMaxFramesToCapture(liveConfiguration.maxFramesToCapture);

        configure();
    }

    bool start(JNIEnv *jniEnv);

    void stop();

    void handle(int signum, siginfo_t *info, void *context);

    bool isRunning();

    /* Several getters and setters for externals APIs */

    std::string getFilePath();

    int getSamplingIntervalMin();

    int getSamplingIntervalMax();

    int getMaxFramesToCapture();

    void setFilePath(char *newFilePath);

    void setSamplingInterval(int intervalMin, int intervalMax);

    void setMaxFramesToCapture(int maxFramesToCapture);

    ~Profiler();

private:
    JavaVM *const jvm_;
    jvmtiEnv *const jvmti_;

    ThreadMap &tMap_;

    ConfigurationOptions configuration_;
    ConfigurationOptions liveConfiguration;

    std::unique_ptr<LogWriter> writer;
    std::unique_ptr<Processor> processor;
    
    bool reloadConfig;
    long pid;

    // indicates change of internal state
    std::atomic<bool> ongoingConf;

    static void current_utc_time(timespec *ts);

    void configure();

    bool __is_running();

    DISALLOW_COPY_AND_ASSIGN(Profiler);
};

#endif // PROFILER_H
