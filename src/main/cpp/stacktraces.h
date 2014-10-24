#include "globals.h"

#ifndef STACKTRACES_H
#define STACKTRACES_H

// To implement the profiler, we rely on an undocumented function called
// AsyncGetCallTrace in the Java virtual machine, which is used by Sun
// Studio Analyzer, and designed to get stack traces asynchronously.
// It uses the old JVMPI interface, so we must reconstruct the
// neccesary bits of that here.

// For a Java frame, the lineno is the bci of the method, and the
// method_id is the jmethodID.  For a JNI method, the lineno is -3,
// and the method_id is the jmethodID.
typedef struct {
    jint lineno;
    jmethodID method_id;
} JVMPI_CallFrame;

typedef struct {
    // JNIEnv of the thread from which we grabbed the trace
    JNIEnv *env_id;
    // < 0 if the frame isn't walkable
    jint num_frames;
    // The frames, callee first.
    JVMPI_CallFrame *frames;
} JVMPI_CallTrace;

typedef void (*ASGCTType)(JVMPI_CallTrace *, jint, void *);

const int kNumCallTraceErrors = 10;

enum CallTraceErrors {
    // 0 is reserved for native stack traces.  This includes JIT and GC threads.
            kNativeStackTrace = 0,
    // The JVMTI class load event is disabled (a prereq for AsyncGetCallTrace)
            kNoClassLoad = -1,
    // For traces in GC
            kGcTraceError = -2,
    // We can't figure out what the top (non-Java) frame is
            kUnknownNotJava = -3,
    // The frame is not Java and not walkable
            kNotWalkableFrameNotJava = -4,
    // We can't figure out what the top Java frame is
            kUnknownJava = -5,
    // The frame is Java and not walkable
            kNotWalkableFrameJava = -6,
    // Unknown thread state (not in Java or native or the VM)
            kUnknownState = -7,
    // The JNIEnv is bad - this likely means the thread has exited
            kTicksThreadExit = -8,
    // The thread is being deoptimized, so the stack is borked
            kDeoptHandler = -9,
    // We're in a safepoint, and can't do reporting
            kSafepoint = -10,
};

// Wrapper to hold reference to AsyncGetCallTrace function
class Asgct {
public:
    static void SetAsgct(ASGCTType asgct) {
        asgct_ = asgct;
    }

    // AsyncGetCallTrace function, to be dlsym'd.
    static ASGCTType GetAsgct() {
        return asgct_;
    }

private:
    static ASGCTType asgct_;

    DISALLOW_IMPLICIT_CONSTRUCTORS(Asgct);
};

#endif // STACKTRACES_H
