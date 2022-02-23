#include <assert.h>
#include <dlfcn.h>
#include <jvmti.h>
#include <jni.h>
#include <stdint.h>
#include <signal.h>
#include <time.h>
#include <string>

#ifdef __MACH__
#   include <mach/clock.h>
#   include <mach/mach.h>
#endif

#ifndef GLOBALS_H
#define GLOBALS_H

class Profiler;

void logError(const char *__restrict format, ...);

Profiler *getProfiler();
void setProfiler(Profiler *p);

const int DEFAULT_SAMPLING_INTERVAL = 1;
const int DEFAULT_MAX_FRAMES_TO_CAPTURE = 128;
const int MAX_FRAMES_TO_CAPTURE = 2048;
const int DEFAULT_ROTATE_SIZE_MB = 5;
const int DEFAULT_ROTATE_NUM = 5;

#if defined(STATIC_ALLOCATION_ALLOCA)
  #define STATIC_ARRAY(NAME, TYPE, SIZE, MAXSZ) TYPE *NAME = (TYPE*)alloca((SIZE) * sizeof(TYPE))
#elif defined(STATIC_ALLOCATION_PEDANTIC)
  #define STATIC_ARRAY(NAME, TYPE, SIZE, MAXSZ) TYPE NAME[MAXSZ]
#else
  #define STATIC_ARRAY(NAME, TYPE, SIZE, MAXSZ) TYPE NAME[SIZE]
#endif

#define STR_SIZE(VALUE, NEXT) ((NEXT == 0) ? strlen(VALUE) : (size_t) (NEXT - VALUE))

char *safe_copy_string(const char *value, const char *next);

struct ConfigurationOptions {
    /** Interval in microseconds */
    int samplingIntervalMin, samplingIntervalMax;
    /** RotateSize in MB */
    int rotateNum, rotateSizeMB;
    std::string logFilePath;
    std::string host;
    std::string port;
    bool start;
    int maxFramesToCapture;

    ConfigurationOptions() :
            samplingIntervalMin(DEFAULT_SAMPLING_INTERVAL),
            samplingIntervalMax(DEFAULT_SAMPLING_INTERVAL),
            rotateNum(DEFAULT_ROTATE_NUM),
            rotateSizeMB(DEFAULT_ROTATE_SIZE_MB),
            logFilePath(""),
            host(""),
            port(""),
            start(true),
            maxFramesToCapture(DEFAULT_MAX_FRAMES_TO_CAPTURE) {
    }

    ConfigurationOptions(const ConfigurationOptions &config) :
            samplingIntervalMin(config.samplingIntervalMin),
            samplingIntervalMax(config.samplingIntervalMax),
            rotateNum(config.rotateNum),
            rotateSizeMB(config.rotateSizeMB),
            logFilePath(config.logFilePath),
            host(config.host),
            port(config.port),
            start(config.start),
            maxFramesToCapture(config.maxFramesToCapture) {
    }

    virtual ~ConfigurationOptions() {
    }
};

#define AGENTEXPORT __attribute__((visibility("default"))) JNIEXPORT

// Gets us around -Wunused-parameter
#define IMPLICITLY_USE(x) (void) x;

// Wrap JVMTI functions in this in functions that expect a return
// value and require cleanup but no error message
#define JVMTI_ERROR_CLEANUP_RET_NO_MESSAGE(error, retval, cleanup)             \
  {                                                                            \
    int err;                                                                   \
    if ((err = (error)) != JVMTI_ERROR_NONE) {                                 \
      cleanup;                                                                 \
      return (retval);                                                         \
    }                                                                          \
  }
// Wrap JVMTI functions in this in functions that expect a return
// value and require cleanup.
#define JVMTI_ERROR_MESSAGE_CLEANUP_RET(error, message, retval, cleanup)       \
  {                                                                            \
    int err;                                                                   \
    if ((err = (error)) != JVMTI_ERROR_NONE) {                                 \
      logError(message, err);                                                  \
      cleanup;                                                                 \
      return (retval);                                                         \
    }                                                                          \
  }

#define JVMTI_ERROR_CLEANUP_RET(error, retval, cleanup)                        \
    JVMTI_ERROR_MESSAGE_CLEANUP_RET(error, "JVMTI error %d\n", retval, cleanup)

// Wrap JVMTI functions in this in functions that expect a return value.
#define JVMTI_ERROR_RET(error, retval)                                           \
  JVMTI_ERROR_CLEANUP_RET(error, retval, /* nothing */)

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

// Accessors for getting the Jvm function for AsyncGetCallTrace.
class Accessors {
public:
    template<class FunctionType>
    static inline FunctionType GetJvmFunction(const char *function_name) {
        // get address of function, return null if not found
        return bit_cast<FunctionType>(dlsym(RTLD_DEFAULT, function_name));
    }
};

void bootstrapHandle(int signum, siginfo_t *info, void *context);

#ifdef __MACH__
static clock_serv_t osx_clock;
#endif

class TimeUtils {
public:
  static void init() {
#ifdef __MACH__
    host_get_clock_service(mach_host_self(), CALENDAR_CLOCK, &osx_clock);
#endif
  }

  static void current_utc_time(timespec *ts) {
#ifdef __MACH__
    mach_timespec_t mts;
    clock_get_time(osx_clock, &mts);
    ts->tv_sec = mts.tv_sec;
    ts->tv_nsec = mts.tv_nsec;
#else
    clock_gettime(CLOCK_REALTIME, ts);
#endif
  }
};

#endif // GLOBALS_H
