#include <stdio.h>
#include <string.h>

#include <string>
#include <jvmti.h>

#include "globals.h"
#include "profiler.h"

static ConfigurationOptions* CONFIGURATION = new ConfigurationOptions();
static Profiler* prof;


// This has to be here, or the VM turns off class loading events.
// And AsyncGetCallTrace needs class loading events to be turned on!
void JNICALL OnClassLoad(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread,
        jclass klass) {
    IMPLICITLY_USE(jvmti_env);
    IMPLICITLY_USE(jni_env);
    IMPLICITLY_USE(thread);
    IMPLICITLY_USE(klass);
}

// Calls GetClassMethods on a given class to force the creation of
// jmethodIDs of it.
void CreateJMethodIDsForClass(jvmtiEnv *jvmti, jclass klass) {
    jint method_count;
    JvmtiScopedPtr<jmethodID> methods(jvmti);
    jvmtiError e = jvmti->GetClassMethods(klass, &method_count, methods.GetRef());
    if (e != JVMTI_ERROR_NONE && e != JVMTI_ERROR_CLASS_NOT_PREPARED) {
        // JVMTI_ERROR_CLASS_NOT_PREPARED is okay because some classes may
        // be loaded but not prepared at this point.
        JvmtiScopedPtr<char> ksig(jvmti);
        JVMTI_ERROR((jvmti->GetClassSignature(klass, ksig.GetRef(), NULL)));
        logError("Failed to create method IDs for methods in class %s with error %d ",
                 ksig.Get(), e);
    }
}

void JNICALL OnVMInit(jvmtiEnv *jvmti, JNIEnv *jniEnv, jthread thread) {
    IMPLICITLY_USE(thread);
    // Forces the creation of jmethodIDs of the classes that had already
    // been loaded (eg java.lang.Object, java.lang.ClassLoader) and
    // OnClassPrepare() misses.
    jint class_count;
    JvmtiScopedPtr<jclass> classes(jvmti);
    JVMTI_ERROR((jvmti->GetLoadedClasses(&class_count, classes.GetRef())));
    jclass *classList = classes.Get();
    for (int i = 0; i < class_count; ++i) {
        jclass klass = classList[i];
        CreateJMethodIDsForClass(jvmti, klass);
    }
#if !defined(__APPLE__) && !defined(__FreeBSD__)
    if (CONFIGURATION->start)
        prof->start(jniEnv);
#endif
}

void JNICALL OnClassPrepare(jvmtiEnv *jvmti_env, JNIEnv *jni_env,
        jthread thread, jclass klass) {
    IMPLICITLY_USE(jni_env);
    IMPLICITLY_USE(thread);
    // We need to do this to "prime the pump", as it were -- make sure
    // that all of the methodIDs have been initialized internally, for
    // AsyncGetCallTrace.  I imagine it slows down class loading a mite,
    // but honestly, how fast does class loading have to be?
    CreateJMethodIDsForClass(jvmti_env, klass);
}

void JNICALL OnVMDeath(jvmtiEnv *jvmti_env, JNIEnv *jni_env) {
    IMPLICITLY_USE(jvmti_env);
    IMPLICITLY_USE(jni_env);

    prof->stop();
}

static bool PrepareJvmti(jvmtiEnv *jvmti) {
    // Set the list of permissions to do the various internal VM things
    // we want to do.
    jvmtiCapabilities caps;

    memset(&caps, 0, sizeof(caps));
    caps.can_generate_all_class_hook_events = 1;

    caps.can_get_source_file_name = 1;
    caps.can_get_line_numbers = 1;
    caps.can_get_bytecodes = 1;
    caps.can_get_constant_pool = 1;
#if defined(__APPLE__) || defined(__FreeBSD__)
    caps.can_generate_native_method_bind_events = 1;
#endif

    jvmtiCapabilities all_caps;
    int error;

    if (JVMTI_ERROR_NONE ==
            (error = jvmti->GetPotentialCapabilities(&all_caps))) {
        // This makes sure that if we need a capability, it is one of the
        // potential capabilities.  The technique isn't wonderful, but it
        // is compact and as likely to be compatible between versions as
        // anything else.
        char *has = reinterpret_cast<char *>(&all_caps);
        const char *should_have = reinterpret_cast<const char *>(&caps);
        for (int i = 0; i < sizeof(all_caps); i++) {
            if ((should_have[i] != 0) && (has[i] == 0)) {
                return false;
            }
        }

        // This adds the capabilities.
        if ((error = jvmti->AddCapabilities(&caps)) != JVMTI_ERROR_NONE) {
            logError("Failed to add capabilities with error %d\n", error);
            return false;
        }
    }
    return true;
}

sigset_t prof_signal_mask;

void (*actual_JVM_StartThread)(JNIEnv *, jthread) = NULL;

void JVM_StartThread_Interposer(JNIEnv *jni_env, jobject jthread) {
    pthread_sigmask(SIG_BLOCK, &prof_signal_mask, NULL);
    actual_JVM_StartThread(jni_env, jthread);
    pthread_sigmask(SIG_UNBLOCK, &prof_signal_mask, NULL);
}

//Set up interposition of calls to Thread::run0
void JNICALL
OnNativeMethodBind(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread,
                 jmethodID method, void *address, void **new_address_ptr) {
    if (actual_JVM_StartThread != NULL) {
        return;
    }

    char *name_ptr, *signature_ptr;

    int err = jvmti_env->GetMethodName(method, &name_ptr, &signature_ptr, NULL);
    if (err != JNI_OK) {
        logError("Error %i retrieving method name", err);
        return;
    }
    if (strcmp(name_ptr, "start0") == 0 && strcmp(signature_ptr, "()V") == 0) {
        jclass declaringClass;
        int err = jvmti_env->GetMethodDeclaringClass(method, &declaringClass);
        if (err != JNI_OK) {
            logError("Error %i retrieving class", err);
            goto end;
        }
        jclass clazz = jni_env->GetObjectClass(declaringClass);
        jmethodID getSimpleNameMethod = jni_env->GetMethodID(clazz,
            "getSimpleName", "()Ljava/lang/String;");
        jstring jClassName = (jstring) jni_env->CallObjectMethod(declaringClass,
            getSimpleNameMethod);

        const char *className = jni_env->GetStringUTFChars(jClassName, 0);
        if (strcmp(className, "Thread") == 0) {
            *new_address_ptr = (void*) &JVM_StartThread_Interposer;
            actual_JVM_StartThread = (void (*)(JNIEnv *, jthread)) address;
        }
        jni_env->ReleaseStringUTFChars(jClassName, className);
    }
end:
    jvmti_env->Deallocate((unsigned char *) name_ptr);
    jvmti_env->Deallocate((unsigned char *) signature_ptr);
}

volatile bool main_started = false;

void JNICALL
OnThreadStart(jvmtiEnv *jvmti_env, JNIEnv *jni_env, jthread thread) {
    if (!main_started) {
        jvmtiThreadInfo thread_info;
        int error = jvmti_env->GetThreadInfo(thread, &thread_info);
        if (error == JNI_OK) {
            if (strcmp(thread_info.name, "main") == 0) {
                main_started = true;
                if (CONFIGURATION->start)
                    prof->start(jni_env);
            }
        }
    }
    pthread_sigmask(SIG_UNBLOCK, &prof_signal_mask, NULL);
}

static bool RegisterJvmti(jvmtiEnv *jvmti) {
    sigemptyset(&prof_signal_mask);
    sigaddset(&prof_signal_mask, SIGPROF);
    // Create the list of callbacks to be called on given events.
    jvmtiEventCallbacks *callbacks = new jvmtiEventCallbacks();
    memset(callbacks, 0, sizeof(jvmtiEventCallbacks));

    callbacks->VMInit = &OnVMInit;
    callbacks->VMDeath = &OnVMDeath;

    callbacks->ClassLoad = &OnClassLoad;
    callbacks->ClassPrepare = &OnClassPrepare;

    callbacks->NativeMethodBind = &OnNativeMethodBind;
    callbacks->ThreadStart = &OnThreadStart;

    JVMTI_ERROR_1(
            (jvmti->SetEventCallbacks(callbacks, sizeof(jvmtiEventCallbacks))),
            false);

    jvmtiEvent events[] = {JVMTI_EVENT_CLASS_LOAD, JVMTI_EVENT_CLASS_PREPARE,
        JVMTI_EVENT_VM_DEATH, JVMTI_EVENT_VM_INIT
#if defined(__APPLE__) || defined(__FreeBSD__)
        ,JVMTI_EVENT_THREAD_START,
        JVMTI_EVENT_NATIVE_METHOD_BIND
#endif
    };

    size_t num_events = sizeof(events) / sizeof(jvmtiEvent);

    // Enable the callbacks to be triggered when the events occur.
    // Events are enumerated in jvmstatagent.h
    for (int i = 0; i < num_events; i++) {
        JVMTI_ERROR_1(
                (jvmti->SetEventNotificationMode(JVMTI_ENABLE, events[i], NULL)),
                false);
    }

    return true;
}

static void parseArguments(char *options, ConfigurationOptions &configuration) {
    configuration.initializeDefaults();
    char* next = options;
    for (char *key = options; next != NULL; key = next + 1) {
        char *value = strchr(key, '=');
        next = strchr(key, ',');
        if (value == NULL) {
            logError("No value for key %s\n", key);
            continue;
        } else {
            value++;
            if (strstr(key, "intervalMin") == key) {
                configuration.samplingIntervalMin = atoi(value);
            } else if (strstr(key, "intervalMax") == key) {
                configuration.samplingIntervalMax = atoi(value);
            } else if (strstr(key, "interval") == key) {
                configuration.samplingIntervalMin = configuration.samplingIntervalMax = atoi(value);
            } else if (strstr(key, "logPath") == key) {
                size_t  size = (next == 0) ? strlen(key) : (size_t) (next - value);
                configuration.logFilePath = (char*) malloc(size * sizeof(char));
                strncpy(configuration.logFilePath, value, size);
            } else if (strstr(key, "start") == key) {
                configuration.start = atoi(value);
            } else {
                logError("Unknown configuration option: %s\n", key);
            }
        }
    }
}

AGENTEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    IMPLICITLY_USE(reserved);
    int err;
    jvmtiEnv *jvmti;
    parseArguments(options, *CONFIGURATION);

    if ((err = (jvm->GetEnv(reinterpret_cast<void **>(&jvmti), JVMTI_VERSION))) !=
            JNI_OK) {
        logError("JVMTI initialisation Error %d\n", err);
        return 1;
    }

    /*
      JNIEnv *jniEnv;
      if ((err = (vm->GetEnv(reinterpret_cast<void **>(&jniEnv),
      JNI_VERSION_1_6))) != JNI_OK) {
        logError("JNI Error %d\n", err);
        return 1;
      }
      */

    if (!PrepareJvmti(jvmti)) {
        logError("Failed to initialize JVMTI.  Continuing...\n");
        return 0;
    }

    if (!RegisterJvmti(jvmti)) {
        logError("Failed to enable JVMTI events.  Continuing...\n");
        // We fail hard here because we may have failed in the middle of
        // registering callbacks, which will leave the system in an
        // inconsistent state.
        return 1;
    }

    Asgct::SetAsgct(Accessors::GetJvmFunction<ASGCTType>("AsyncGetCallTrace"));

    prof = new Profiler(jvm, jvmti, CONFIGURATION);

    return 0;
}

AGENTEXPORT void JNICALL Agent_OnUnload(JavaVM *vm) {
    IMPLICITLY_USE(vm);
}

void bootstrapHandle(int signum, siginfo_t *info, void *context) {
    prof->handle(signum, info, context);
}

void logError(const char *__restrict format, ...) {
    va_list arg;

    va_start(arg, format);
    fprintf(stderr, format, arg);
    va_end(arg);
}

Profiler *getProfiler() {
    return prof;
}
