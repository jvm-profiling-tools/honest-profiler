#include <jvmti.h>
#include "log_writer.h"

#ifndef PROCESSOR_H
#define PROCESSOR_H

class Processor final {

public:
  explicit Processor(jvmtiEnv *jvmti, LogWriter &logWriter,
                     CircularQueue &buffer)
      : jvmti_(jvmti), logWriter_(logWriter), buffer_(buffer), isRunning(true) {
  }

  void start(JNIEnv *jniEnv);

  void run();

  void stop();

private:
  jvmtiEnv *jvmti_;

  LogWriter &logWriter_;

  CircularQueue &buffer_;

  std::atomic_bool isRunning;

  void startCallback(jvmtiEnv *jvmti_env, JNIEnv *jni_env, void *arg);

  DISALLOW_COPY_AND_ASSIGN(Processor);
};

#endif // PROCESSOR_H
