#ifndef PROCESSOR_H
#define PROCESSOR_H

#include <jvmti.h>
#include "common.h"
#include "log_writer.h"
#include "signal_handler.h"

#include "trace.h"

const int kTraceProcessorTotal = 3;

const int kTraceProcessorStart = 0;
const int kTraceProcessorStop = 1;
const int kTraceProcessorRunning = 2;

TRACE_DECLARE(Processor, kTraceProcessorTotal);

class Processor {

public:
    explicit Processor(jvmtiEnv* jvmti, LogWriter& logWriter, const ConfigurationOptions &conf)
        : jvmti_(jvmti), config(conf), logWriter_(logWriter), 
          buffer(logWriter_, config.maxFramesToCapture), 
          handler(config.samplingIntervalMin, config.samplingIntervalMax),
          isRunning_(false) {
        interval_ = Size * config.samplingIntervalMin / 1000 / 2;
        interval_ = interval_ > 0 ? interval_ : 1;
    }

    bool start(JNIEnv *jniEnv);

    void run();

    void stop();

    bool isRunning() const;

    void handle(JNIEnv *jni_env, const timespec& ts, ThreadBucketPtr threadInfo, void *context);

private:
    jvmtiEnv *const jvmti_;

    const ConfigurationOptions &config;

    LogWriter& logWriter_;
    CircularQueue buffer;
    SignalHandler handler;

    std::atomic_bool isRunning_;
    std::atomic_flag workerDone;

    int interval_;

    void startCallback(jvmtiEnv *jvmti_env, JNIEnv *jni_env, void *arg);

    DISALLOW_COPY_AND_ASSIGN(Processor);
};

#endif // PROCESSOR_H
