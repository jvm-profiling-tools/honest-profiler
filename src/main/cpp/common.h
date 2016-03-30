#ifndef HONEST_PROFILER_COMMON_H
#define HONEST_PROFILER_COMMON_H

#include <jvmti.h>
#include "globals.h"

jthread newThread(JNIEnv *jniEnv, const char *threadName);

JNIEnv *getJNIEnv(JavaVM *jvm);

#endif
