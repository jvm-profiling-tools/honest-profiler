#include "profiler.h"

#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>

#ifdef __APPLE__
// See comment in Accessors class
pthread_key_t Accessors::key_;
#else
__thread JNIEnv *Accessors::env_;
#endif

ASGCTType Asgct::asgct_;

int Profiler::failures_[kNumCallTraceErrors + 1];

namespace {

// Helper class to store and reset errno when in a signal handler.
class ErrnoRaii {
public:
  ErrnoRaii() { stored_errno_ = errno; }
  ~ErrnoRaii() { errno = stored_errno_; }

private:
  int stored_errno_;

  DISALLOW_COPY_AND_ASSIGN(ErrnoRaii);
};
} // namespace

bool Profiler::lookupFrameInformation(const JVMPI_CallFrame &frame,
                                      jvmtiEnv *jvmti,
                                      MethodListener &logWriter) {
  jint error;
  JvmtiScopedPtr<char> methodName(jvmti);

  error =
      jvmti->GetMethodName(frame.method_id, methodName.GetRef(), NULL, NULL);
  if (error != JVMTI_ERROR_NONE) {
    methodName.AbandonBecauseOfError();
    if (error == JVMTI_ERROR_INVALID_METHODID) {
      static int once = 0;
      if (!once) {
        once = 1;
        fprintf(stderr, "One of your monitoring interfaces "
                        "is having trouble resolving its stack traces.  "
                        "GetMethodName on a jmethodID involved in a stacktrace "
                        "resulted in an INVALID_METHODID error which usually "
                        "indicates its declaring class has been unloaded.\n");
        fprintf(stderr, "Unexpected JVMTI error %d in GetMethodName", error);
      }
    }
    return false;
  }

  // Get class name, put it in signature_ptr
  jclass declaring_class;
  JVMTI_ERROR_1(
      jvmti->GetMethodDeclaringClass(frame.method_id, &declaring_class), false);

  JvmtiScopedPtr<char> signature_ptr2(jvmti);
  JVMTI_ERROR_CLEANUP_1(
      jvmti->GetClassSignature(declaring_class, signature_ptr2.GetRef(), NULL),
      false, signature_ptr2.AbandonBecauseOfError());

  // Get source file, put it in source_name_ptr
  char *fileName;
  JvmtiScopedPtr<char> source_name_ptr(jvmti);
  static char file_unknown[] = "UnknownFile";
  if (JVMTI_ERROR_NONE !=
      jvmti->GetSourceFileName(declaring_class, source_name_ptr.GetRef())) {
    source_name_ptr.AbandonBecauseOfError();
    fileName = file_unknown;
  } else {
    fileName = source_name_ptr.Get();
  }

  logWriter.recordNewMethod((method_id)frame.method_id, fileName,
                            signature_ptr2.Get(), methodName.Get());

  return true;
  /*if (line_number != NULL) {
    // TODO: is frame.lineno correct?  GetLineNumber
    // expects a BCI.
    *line_number = GetLineNumber(frame.method_id, frame.lineno);
  }*/
}

void Profiler::handle(int signum, siginfo_t *info, void *context) {
  IMPLICITLY_USE(signum);
  IMPLICITLY_USE(info);
  ErrnoRaii err_storage; // stores and resets errno

  JNIEnv *env = Accessors::CurrentJniEnv();
  if (env == NULL) {
    // native / JIT / GC thread, which isn't attached to the JVM.
    failures_[0]++;
    return;
  }

  JVMPI_CallFrame frames[kMaxFramesToCapture];
  safe_reset(frames, sizeof(JVMPI_CallFrame) * kMaxFramesToCapture);

  JVMPI_CallTrace trace;
  trace.frames = frames;
  trace.env_id = env;

  ASGCTType asgct = Asgct::GetAsgct();
  (*asgct)(&trace, kMaxFramesToCapture, context);

  if (trace.num_frames < 0) {
    int idx = -trace.num_frames;
    // fprintf(stderr, "error: %d %lu\n", idx, (int64_t) trace.env_id);
    if (idx > kNumCallTraceErrors) {
      return;
    }
    failures_[idx]++;
  } else {
    buffer->push(trace);
  }
}

// This method schedules the SIGPROF timer to go off every sec
// seconds, usec microseconds.
bool SignalHandler::SetSigprofInterval(int sec, int usec) {
  static struct itimerval timer;
  timer.it_interval.tv_sec = sec;
  timer.it_interval.tv_usec = usec;
  timer.it_value = timer.it_interval;
  if (setitimer(ITIMER_PROF, &timer, 0) == -1) {
    fprintf(stderr, "Scheduling profiler interval failed with error %d\n",
            errno);
    return false;
  }
  return true;
}

struct sigaction SignalHandler::SetAction(void (*action)(int, siginfo_t *,
                                                         void *)) {
  struct sigaction sa;
#ifdef __clang__
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdisabled-macro-expansion"
#endif
  sa.sa_handler = NULL;
  sa.sa_sigaction = action;
  sa.sa_flags = SA_RESTART | SA_SIGINFO;
#ifdef __clang__
#pragma clang diagnostic pop
#endif

  sigemptyset(&sa.sa_mask);

  struct sigaction old_handler;
  if (sigaction(SIGPROF, &sa, &old_handler) != 0) {
    fprintf(stderr, "Scheduling profiler action failed with error %d\n", errno);
    return old_handler;
  }

  return old_handler;
}

bool Profiler::start(JNIEnv *jniEnv) {
  int usec_wait = 1;

  memset(failures_, 0, sizeof(failures_));

  // reference back to Profiler::handle on the singleton
  // instance of Profiler
  handler_.SetAction(&bootstrapHandle);
  processor->start(jniEnv);
  return handler_.SetSigprofInterval(0, usec_wait);
}

void Profiler::stop() {
  handler_.SetSigprofInterval(0, 0);
  processor->stop();
  signal(SIGPROF, SIG_IGN);
}
