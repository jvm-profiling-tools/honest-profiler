#include "profiler.h"

ASGCTType Asgct::asgct_;

TRACE_DEFINE_BEGIN(Profiler, kTraceProfilerTotal)
    TRACE_DEFINE("start failed")
    TRACE_DEFINE("start succeeded")
    TRACE_DEFINE("set sampling interval failed")
    TRACE_DEFINE("set sampling interval succeeded")
    TRACE_DEFINE("set stack frames to capture failed")
    TRACE_DEFINE("set stack frames to capture succeeded")
    TRACE_DEFINE("set new file failed")
    TRACE_DEFINE("set new file succeeded")
    TRACE_DEFINE("stop failed")
    TRACE_DEFINE("stop succeeded")
TRACE_DEFINE_END(Profiler, kTraceProfilerTotal);

void Profiler::handle(int signum, siginfo_t *info, void *context) {
    IMPLICITLY_USE(signum);
    IMPLICITLY_USE(info);
    timespec spec;
    
    if (jvm_) {
        JNIEnv *jniEnv = getJNIEnv(jvm_);
        TimeUtils::current_utc_time(&spec); // sample current time
        processor->handle(jniEnv, spec, jniEnv ? tMap_.get(jniEnv) : ThreadBucketPtr(nullptr), context);
    }
}

bool Profiler::start(JNIEnv *jniEnv) {
    SimpleSpinLockGuard<true> guard(ongoingConf);
    /* within critical section */
    
    if (__is_running()) {
        TRACE(Profiler, kTraceProfilerStartFailed);
        logError("WARN: Start called but sampling is already running\n");
        return true;
    }
    
    TRACE(Profiler, kTraceProfilerStartOk);
    
    if (reloadConfig) configure();
    
    return processor->start(jniEnv);
}

void Profiler::stop() {
    /* Make sure it doesn't overlap with configure */
    SimpleSpinLockGuard<true> guard(ongoingConf);

    if (!__is_running()) {
        TRACE(Profiler, kTraceProfilerStopFailed);
        return;
    }

    processor->stop();
}

bool Profiler::isRunning() {
    /* Make sure it doesn't overlap with configure */
    SimpleSpinLockGuard<true> guard(ongoingConf, false);
    bool res = __is_running();
    return res;
}

// non-blocking version (cen be called once spin-lock with acquire semantics is grabed)
bool Profiler::__is_running() {
    return processor && processor->isRunning();
}

void Profiler::setFilePath(char *newFilePath) {
    /* Make sure it doesn't overlap with other sets */
    SimpleSpinLockGuard<true> guard(ongoingConf);

    if (__is_running()) {
        TRACE(Profiler, kTraceProfilerSetFileFailed);
        logError("WARN: Unable to modify running profiler\n");
        return;
    }

    TRACE(Profiler, kTraceProfilerSetFileOk);
    
    liveConfiguration.logFilePath.assign(newFilePath);
    reloadConfig = true;
}

void Profiler::setSamplingInterval(int intervalMin, int intervalMax) {
    /* Make sure it doesn't overlap with other sets */
    SimpleSpinLockGuard<true> guard(ongoingConf);

    if (__is_running()) {
        TRACE(Profiler, kTraceProfilerSetIntervalFailed);
        logError("WARN: Unable to modify running profiler\n");
        return;
    }

    TRACE(Profiler, kTraceProfilerSetIntervalOk);

    int min = intervalMin > 0 ? intervalMin : DEFAULT_SAMPLING_INTERVAL;
    int max = intervalMax > 0 ? intervalMax : DEFAULT_SAMPLING_INTERVAL;
    liveConfiguration.samplingIntervalMin = std::min(min, max);
    liveConfiguration.samplingIntervalMax = std::max(min, max);
    reloadConfig = true;
}

void Profiler::setMaxFramesToCapture(int maxFramesToCapture) {
    /* Make sure it doesn't overlap with other sets */
    SimpleSpinLockGuard<true> guard(ongoingConf);

    if (__is_running()) {
        TRACE(Profiler, kTraceProfilerSetFramesFailed);
        logError("WARN: Unable to modify running profiler\n");
        return;
    }

    TRACE(Profiler, kTraceProfilerSetFramesOk);

    int res = (maxFramesToCapture > 0 && maxFramesToCapture < MAX_FRAMES_TO_CAPTURE) ?
              maxFramesToCapture : DEFAULT_MAX_FRAMES_TO_CAPTURE;
    liveConfiguration.maxFramesToCapture = res;
    reloadConfig = true;
}

/* return copy of the string */
std::string Profiler::getFilePath() {
    /* Make sure it doesn't overlap with setFilePath */
    SimpleSpinLockGuard<true> guard(ongoingConf, true); // relaxed store
    return liveConfiguration.logFilePath;
}

int Profiler::getSamplingIntervalMin() {
    SimpleSpinLockGuard<false> guard(ongoingConf); // nonblocking
    return liveConfiguration.samplingIntervalMin;
}

int Profiler::getSamplingIntervalMax() {
    SimpleSpinLockGuard<false> guard(ongoingConf); // nonblocking
    return liveConfiguration.samplingIntervalMax;
}

int Profiler::getMaxFramesToCapture() {
    SimpleSpinLockGuard<false> guard(ongoingConf); // nonblocking
    return liveConfiguration.maxFramesToCapture;
}

void Profiler::configure() {
    /* nested critical section, no need to acquire or CAS */
    bool needsUpdate = processor == NULL;

    needsUpdate = needsUpdate || configuration_.logFilePath != liveConfiguration.logFilePath;
    if (needsUpdate) {
        if (liveConfiguration.logFilePath.empty()) {
            std::ostringstream fileBuilder;
            long epochMillis = duration_cast<milliseconds>(system_clock::now().time_since_epoch()).count();
            fileBuilder << "log-" << pid << "-" << epochMillis << ".hpl";
            std::string filename(fileBuilder.str());
            configuration_.logFilePath = filename;
            liveConfiguration.logFilePath = std::move(filename);
        } else {
            configuration_.logFilePath = liveConfiguration.logFilePath;
        }
        writer = std::unique_ptr<LogWriter>(new LogWriter(liveConfiguration.logFilePath, liveConfiguration.rotateNum, liveConfiguration.rotateSizeMB, jvmti_));
    }

    needsUpdate = needsUpdate ||
                  configuration_.maxFramesToCapture != liveConfiguration.maxFramesToCapture ||
                  configuration_.samplingIntervalMin != liveConfiguration.samplingIntervalMin ||
                  configuration_.samplingIntervalMax != liveConfiguration.samplingIntervalMax;
    if (needsUpdate) {
        configuration_.maxFramesToCapture = liveConfiguration.maxFramesToCapture;
        configuration_.samplingIntervalMin = liveConfiguration.samplingIntervalMin;
        configuration_.samplingIntervalMax = liveConfiguration.samplingIntervalMax;
        processor = std::unique_ptr<Processor>(new Processor(jvmti_, *writer.get(), configuration_));
    }
    reloadConfig = false;
}

Profiler::~Profiler() {}
