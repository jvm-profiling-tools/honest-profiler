#ifndef PROCESSOR_H
#define PROCESSOR_H

#include <jvmti.h>
#include "log_writer.h"
#include "signal_handler.h"

class Processor {

public:
    explicit Processor(jvmtiEnv* jvmti, LogWriter& logWriter,
            CircularQueue& buffer, SignalHandler& handler, int interval)
            : jvmti_(jvmti), logWriter_(logWriter), buffer_(buffer), isRunning_(true), handler_(handler), interval_(interval) {
    }

    void start(JNIEnv *jniEnv);

    void run();

    void stop();

    bool isRunning() const;

private:
    jvmtiEnv* jvmti_;

    LogWriter& logWriter_;

    CircularQueue& buffer_;

    std::atomic_bool isRunning_;

    SignalHandler& handler_;

    int interval_;

    void startCallback(jvmtiEnv *jvmti_env, JNIEnv *jni_env, void *arg);

    DISALLOW_COPY_AND_ASSIGN(Processor);
};

#endif // PROCESSOR_H
