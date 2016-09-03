#include "common.h"

jthread newThread(JNIEnv *jniEnv, const char *threadName) {
    jclass thrClass;
    jmethodID cid;
    jthread res;

    thrClass = jniEnv->FindClass("java/lang/Thread");
    if (thrClass == NULL) {
        logError("WARN: Cannot find Thread class\n");
    }
    cid = jniEnv->GetMethodID(thrClass, "<init>", "()V");
    if (cid == NULL) {
        logError("WARN: Cannot find Thread constructor method\n");
    }
    res = jniEnv->NewObject(thrClass, cid);
    if (res == NULL) {
        logError("WARN: Cannot create new Thread object\n");
    } else {
        jmethodID mid = jniEnv->GetMethodID(thrClass, "setName", "(Ljava/lang/String;)V");
        jniEnv->CallObjectMethod(res, mid, jniEnv->NewStringUTF(threadName));
    }
    return res;
}

JNIEnv *getJNIEnv(JavaVM *jvm) {
    JNIEnv *jniEnv = NULL;
    int getEnvStat = jvm->GetEnv((void **) &jniEnv, JNI_VERSION_1_6);
    // check for issues
    if (getEnvStat == JNI_EDETACHED || getEnvStat == JNI_EVERSION) {
        jniEnv = NULL;
    }
    return jniEnv;
}
