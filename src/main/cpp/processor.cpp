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

TRACE_DEFINE_BEGIN(Processor, kTraceProcessorTotal)
    TRACE_DEFINE("start processor")
    TRACE_DEFINE("stop processor")
    TRACE_DEFINE("chech that processor is running")
TRACE_DEFINE_END(Processor, kTraceProcessorTotal);

void Processor::run() {
    int popped = 0;

    while (true) {
        while (buffer.pop()) {
            ++popped;
        }
        if (popped > 200) {
            if (!handler.updateSigprofInterval()) {
                break;
            }
            popped = 0;
        }
        if (!isRunning_.load(std::memory_order_relaxed)) {
            while (buffer.pop()); // make all items are processed and released
            break;
        }
        sleep_for_millis(interval_);
    }

    // SIGPROF is already stopped in Profiler::stop, no need to call handler.stopSigprof();
    workerDone.clear(std::memory_order_release);
    // no shared data access after this point, can be safely deleted
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

bool Processor::start(JNIEnv *jniEnv) {
    TRACE(Processor, kTraceProcessorStart);
    jvmtiError result;

    std::cout << "Starting sampling\n";
    isRunning_.store(true, std::memory_order_relaxed); // sequential
    workerDone.test_and_set(std::memory_order_relaxed); // initial is true
    handler.SetAction(&bootstrapHandle);

    if (jniEnv) {
        jthread thread = newThread(jniEnv, "Honest Profiler Processing Thread");
        jvmtiStartFunction callback = callbackToRunProcessor;
        result = jvmti_->RunAgentThread(thread, callback, this, JVMTI_THREAD_NORM_PRIORITY);
    
        if (result != JVMTI_ERROR_NONE) {
            logError("ERROR: Running agent thread failed with: %d\n", result);
        }
        return handler.updateSigprofInterval();
    } 
    workerDone.clear(std::memory_order_relaxed);
    return true;
}

void Processor::stop() {
    TRACE(Processor, kTraceProcessorStop);

    handler.stopSigprof();
    isRunning_.store(false, std::memory_order_seq_cst);
    std::cout << "Stopping sampling\n";
    while (workerDone.test_and_set(std::memory_order_seq_cst)) sched_yield();
    signal(SIGPROF, SIG_IGN);
}

bool Processor::isRunning() const {
    TRACE(Processor, kTraceProcessorRunning);
    return isRunning_.load(std::memory_order_relaxed);
}

void Processor::handle(JNIEnv *jniEnv, const timespec& ts, ThreadBucketPtr threadInfo, void *context) {
    // sample data structure
    STATIC_ARRAY(frames, JVMPI_CallFrame, config.maxFramesToCapture, MAX_FRAMES_TO_CAPTURE);

    JVMPI_CallTrace trace;
    trace.frames = frames;

    if (jniEnv == nullptr) {
        trace.num_frames = -3; // ticks_unknown_not_Java
    } else {
        trace.env_id = jniEnv;
        ASGCTType asgct = Asgct::GetAsgct();
        (*asgct)(&trace, config.maxFramesToCapture, context);
    }

    // log all samples, failures included, let the post processing sift through the data
    buffer.push(ts, trace, std::move(threadInfo));
}