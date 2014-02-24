#include <thread>
#include <chrono>
#include <atomic>

#include <iostream>

#include "processor.h"

// TODO: proper error handling
static void error(std::string message) { std::cout << message; }

static jthread newThread(JNIEnv *jniEnv) {
  jclass thrClass;
  jmethodID cid;
  jthread res;

  thrClass = jniEnv->FindClass("java/lang/Thread");
  if (thrClass == NULL) {
    error("Cannot find Thread class\n");
  }
  cid = jniEnv->GetMethodID(thrClass, "<init>", "()V");
  if (cid == NULL) {
    error("Cannot find Thread constructor method\n");
  }
  res = jniEnv->NewObject(thrClass, cid);
  if (res == NULL) {
    error("Cannot create new Thread object\n");
  }
  return res;
}

void Processor::run() {
  while (true) {
    while (buffer_.pop())
      ;

    if (!isRunning.load()) {
      return;
    }

    // TODO: make this configurable
    std::this_thread::sleep_for(std::chrono::milliseconds(100));
  }
}

void Processor::start(JNIEnv *jniEnv) {
  std::cout << "Start\n";
  jthread thread = newThread(jniEnv);
  jvmtiStartFunction callback =
      [](jvmtiEnv * jvmti_env, JNIEnv * jni_env, void * arg) {
    IMPLICITLY_USE(jvmti_env);
    IMPLICITLY_USE(jni_env);
    Processor *processor = (Processor *)arg;
    processor->run();
  }
  ;
  jvmti_->RunAgentThread(thread, callback, this, JVMTI_THREAD_NORM_PRIORITY);
}

void Processor::stop() {
  isRunning.store(false);
  std::cout << "Stop\n";
}
