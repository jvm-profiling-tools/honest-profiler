#include <thread>
#include <iostream>
#include "processor.h"

#ifdef WINDOWS
#include <windows.h>
#else

#include <unistd.h>

#endif

const uint MILLIS_IN_MICRO = 1000;

void sleep_for_millis(uint period) {
#ifdef WINDOWS
    Sleep(period);
#else
    usleep(period * MILLIS_IN_MICRO);
#endif
}

void Processor::run() {
    int popped = 0;

    while (true) {
        while (buffer_.pop()) {
            ++popped;
        }

        if (popped > 200) {
            if (!handler_.updateSigprofInterval()) {
                break;
            }
            popped = 0;
        }

        if (!isRunning_.load()) {
            break;
        }

        sleep_for_millis(interval_);
    }

    handler_.stopSigprof();
}

void callbackToRunProcessor(jvmtiEnv *jvmti_env, JNIEnv *jni_env, void *arg) {
    IMPLICITLY_USE(jvmti_env);
    IMPLICITLY_USE(jni_env);
    //Avoid having the processor thread also receive the PROF signals
    sigset_t mask;
    sigemptyset(&mask);
    sigaddset(&mask, SIGPROF);
    if (pthread_sigmask(SIG_BLOCK, &mask, NULL) < 0) {
        logError("ERROR: failed to set processor thread signal mask\n");
    }
    Processor *processor = (Processor *) arg;
    processor->run();
}

void Processor::start(JNIEnv *jniEnv) {
    jvmtiError result;

    std::cout << "Starting sampling\n";
    isRunning_.store(true);
    jthread thread = newThread(jniEnv, "Honest Profiler Processing Thread");
    jvmtiStartFunction callback = callbackToRunProcessor;
    result = jvmti_->RunAgentThread(thread, callback, this, JVMTI_THREAD_NORM_PRIORITY);

    if (result != JVMTI_ERROR_NONE) {
        logError("ERROR: Running agent thread failed with: %d\n", result);
    }
}

void Processor::stop() {
    isRunning_.store(false);
    std::cout << "Stopping sampling\n";
}

bool Processor::isRunning() const {
    return isRunning_.load();
}
