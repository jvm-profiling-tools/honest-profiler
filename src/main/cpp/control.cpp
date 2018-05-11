/*
 * Copyright (c) 2015 Richard Warburton (richard.warburton@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

#include <jvmti.h>

#include "profiler.h"

extern "C"
JNIEXPORT jboolean JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_start(JNIEnv *env, jclass klass) {
    Profiler *prof = getProfiler();

    return prof->start(env);
}

extern "C"
JNIEXPORT void JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_stop(JNIEnv *env, jclass klass) {
    Profiler *prof = getProfiler();

    prof->stop();
}

extern "C"
JNIEXPORT jboolean JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_isRunning(JNIEnv *env, jclass klass) {
    Profiler *prof = getProfiler();

    return prof->isRunning();
}

extern "C"
JNIEXPORT jint JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_getSamplingIntervalMin(JNIEnv *env, jclass klass) {
    Profiler *prof = getProfiler();

    return prof->getSamplingIntervalMin();
}

extern "C"
JNIEXPORT jint JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_getSamplingIntervalMax(JNIEnv *env, jclass klass) {
    Profiler *prof = getProfiler();

    return prof->getSamplingIntervalMax();
}

extern "C"
JNIEXPORT jint JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_getMaxFramesToCapture(JNIEnv *env, jclass klass) {
    Profiler *prof = getProfiler();

    return prof->getMaxFramesToCapture();
}

extern "C"
JNIEXPORT jstring JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_getFilePath(JNIEnv *env, jclass klass) {
    Profiler *prof = getProfiler();

    return env->NewStringUTF(prof->getFilePath().c_str());
}

extern "C"
JNIEXPORT void JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_setFilePath(JNIEnv *env, jclass klass, jstring filePath) {
    Profiler *prof = getProfiler();

    if (filePath == NULL) {
    	prof->setFilePath(NULL);
    } else {
    	const char *nativeString = env->GetStringUTFChars(filePath, 0);
    	prof->setFilePath((char*)nativeString);
    	env->ReleaseStringUTFChars(filePath, nativeString);
    }
}

extern "C"
JNIEXPORT void JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_setSamplingInterval(JNIEnv *env, jclass klass, jint intervalMin, jint intervalMax) {
    Profiler *prof = getProfiler();

    prof->setSamplingInterval(intervalMin, intervalMax);
}

extern "C"
JNIEXPORT void JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_setMaxFramesToCapture(JNIEnv *env, jclass klass, jint maxFramesToCapture) {
    Profiler *prof = getProfiler();

    prof->setMaxFramesToCapture(maxFramesToCapture);
}

extern "C"
JNIEXPORT jint JNICALL Java_com_insightfullogic_honest_1profiler_core_control_Agent_getCurrentNativeThreadId(JNIEnv *env, jclass klass) {

    return gettid();
}
