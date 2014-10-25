#include <assert.h>
#include <dlfcn.h>
#include <jvmti.h>
#include <jni.h>
#include <stdint.h>

#ifdef __APPLE__
#include <pthread.h>
#endif

#include <signal.h>

#ifndef GLOBALS_H
#define GLOBALS_H

void logError(const char *__restrict format, ...);

const int DEFAULT_SAMPLING_INTERVAL = 1;

struct ConfigurationOptions {
    /** Interval in microseconds */
    int samplingInterval;
    char* logFilePath;

    void initializeDefaults() {
        samplingInterval = DEFAULT_SAMPLING_INTERVAL;
        logFilePath = NULL;
    }
};

#define AGENTEXPORT __attribute__((visibility("default"))) JNIEXPORT

// Gets us around -Wunused-parameter
#define IMPLICITLY_USE(x) (void) x;

// Wrap JVMTI functions in this in functions that expect a return
// value and require cleanup.
#define JVMTI_ERROR_CLEANUP_1(error, retval, cleanup)                          \
  {                                                                            \
    int err;                                                                   \
    if ((err = (error)) != JVMTI_ERROR_NONE) {                                 \
      logError("JVMTI error %d\n", err);                                       \
      cleanup;                                                                 \
      return (retval);                                                         \
    }                                                                          \
  }

// Wrap JVMTI functions in this in functions that expect a return value.
#define JVMTI_ERROR_1(error, retval)                                           \
  JVMTI_ERROR_CLEANUP_1(error, retval, /* nothing */)

// Wrap JVMTI functions in this in void functions.
#define JVMTI_ERROR(error) JVMTI_ERROR_CLEANUP(error, /* nothing */)

// Wrap JVMTI functions in this in void functions that require cleanup.
#define JVMTI_ERROR_CLEANUP(error, cleanup)                                    \
  {                                                                            \
    int err;                                                                   \
    if ((err = (error)) != JVMTI_ERROR_NONE) {                                 \
      logError("JVMTI error %d\n", err);                                       \
      cleanup;                                                                 \
      return;                                                                  \
    }                                                                          \
  }

#define DISALLOW_COPY_AND_ASSIGN(TypeName)                                     \
  TypeName(const TypeName &);                                                  \
  void operator=(const TypeName &)

#define DISALLOW_IMPLICIT_CONSTRUCTORS(TypeName)                               \
  TypeName();                                                                  \
  DISALLOW_COPY_AND_ASSIGN(TypeName)

// Short version: reinterpret_cast produces undefined behavior in many
// cases where memcpy doesn't.
template<class Dest, class Source>
inline Dest bit_cast(const Source &source) {
    // Compile time assertion: sizeof(Dest) == sizeof(Source)
    // A compile error here means your Dest and Source have different sizes.
    typedef char VerifySizesAreEqual[sizeof(Dest) == sizeof(Source) ? 1 : -1]
            __attribute__((unused));

    Dest dest;
    memcpy(&dest, &source, sizeof(dest));
    return dest;
}

template<class T>
class JvmtiScopedPtr {
public:
    explicit JvmtiScopedPtr(jvmtiEnv *jvmti) : jvmti_(jvmti), ref_(NULL) {
    }

    JvmtiScopedPtr(jvmtiEnv *jvmti, T *ref) : jvmti_(jvmti), ref_(ref) {
    }

    ~JvmtiScopedPtr() {
        if (NULL != ref_) {
            JVMTI_ERROR(jvmti_->Deallocate((unsigned char *) ref_));
        }
    }

    T **GetRef() {
        assert(ref_ == NULL);
        return &ref_;
    }

    T *Get() {
        return ref_;
    }

    void AbandonBecauseOfError() {
        ref_ = NULL;
    }

private:
    jvmtiEnv *jvmti_;
    T *ref_;

    DISALLOW_IMPLICIT_CONSTRUCTORS(JvmtiScopedPtr);
};

// Accessors for a JNIEnv for this thread.
class Accessors {
public:
#ifdef __APPLE__
  // As of 8/2013, Darwin doesn't support __thread.  We love you,
  // Darwin!
  static void SetCurrentJniEnv(JNIEnv *env) {
    static bool once = false;
    int err;
    if ((err = pthread_setspecific(key_, reinterpret_cast<void *>(env))) != 0 &&
        !once) {
      once = true;
      perror("Was not able to set JNIEnv for at least one thread: ");
    }
  }

  static JNIEnv *CurrentJniEnv() {
    JNIEnv *p = reinterpret_cast<JNIEnv *>(pthread_getspecific(key_));
    return p;
  }

  static void Init() {
    if (pthread_key_create(&key_, NULL) != 0) {
      perror("Unable to init thread-local storage.  Profiling won't work:");
    }
  }

  static void Destroy() {
    if (pthread_key_delete(key_) != 0) {
      // Meh.
    }
  }
#else

    static void SetCurrentJniEnv(JNIEnv *env) {
        env_ = env;
    }

    static JNIEnv *CurrentJniEnv() {
        return env_;
    }

    static void Init() {
    }

    static void Destroy() {
    }

#endif

    template<class FunctionType>
    static inline FunctionType GetJvmFunction(const char *function_name) {
        // get address of function, return null if not found
        return bit_cast<FunctionType>(dlsym(RTLD_DEFAULT, function_name));
    }

private:
#ifdef __APPLE__
  static pthread_key_t key_;
#else
    // This is very dangerous.  __thread is not async-safe when used in
    // a shared library, because it calls malloc the first time a given
    // thread accesses it.  This is unlikely to cause problems in
    // straightforward Java apps, but a real fix involves either a fix
    // to glibc or to the Java launcher, and casual users will have a
    // hard time with this.
    static __thread JNIEnv
    *
    env_;
#endif
};

// Things that should probably be user-configurable

// Number of times per second that we profile
static const int kNumInterrupts = 100;

// Maximum number of frames to store from the stack traces sampled.
static const int kMaxFramesToCapture = 128;

void bootstrapHandle(int signum, siginfo_t *info, void *context);

#endif // GLOBALS_H
