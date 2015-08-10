#include "signal_handler.h"

namespace {

// Helper class to store and reset errno when in a signal handler.
    class ErrnoRaii {
    public:
        ErrnoRaii() {
            stored_errno_ = errno;
        }

        ~ErrnoRaii() {
            errno = stored_errno_;
        }

    private:
        int stored_errno_;

        DISALLOW_COPY_AND_ASSIGN(ErrnoRaii);
    };
} // namespace

bool SignalHandler::updateSigprofInterval() {
    bool res = updateSigprofInterval(timingIntervals[intervalIndex]);
    intervalIndex = (intervalIndex + 1) % NUMBER_OF_INTERVALS;
    return res;
}

bool SignalHandler::updateSigprofInterval(const int timingInterval) {
    if (timingInterval == currentInterval)
        return true;
    static struct itimerval timer;
    timer.it_interval.tv_sec = 0;
    timer.it_interval.tv_usec = timingInterval;
    timer.it_value = timer.it_interval;
    if (setitimer(ITIMER_PROF, &timer, 0) == -1) {
        logError("Scheduling profiler interval failed with error %d\n", errno);
        return false;
    }
    currentInterval = timingInterval;
    return true;
}

struct sigaction SignalHandler::SetAction(void (*action)(int, siginfo_t *, void *)) {
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
        logError("Scheduling profiler action failed with error %d\n", errno);
        return old_handler;
    }

    return old_handler;
}

