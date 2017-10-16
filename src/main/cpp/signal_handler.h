#ifndef SIGNAL_HANDLER_H
#define SIGNAL_HANDLER_H

#include <signal.h>
#include <stdlib.h>
#include <sys/time.h>

#include <fstream>
#include <unistd.h>
#include <chrono>

#include <array>
#include <iterator>

#include "globals.h"

const int NUMBER_OF_INTERVALS = 1024;

class SignalHandler {
public:
    SignalHandler(const int samplingIntervalMin, const int samplingIntervalMax) 
        : intervalIndex(0), currentInterval(-1), timingIntervals() {
        srand (time(NULL));
        int range = samplingIntervalMax - samplingIntervalMin + 1;
        for (auto it = timingIntervals.begin(); it != timingIntervals.end(); ++it) {
            *it = samplingIntervalMin + rand() % range;
        }
    }

    struct sigaction SetAction(void (*sigaction)(int, siginfo_t *, void *));

    bool updateSigprofInterval();

    bool updateSigprofInterval(int);

    bool stopSigprof() { return updateSigprofInterval(0); }

    ~SignalHandler() {}

private:
    int intervalIndex;
    int currentInterval;
    std::array<int, NUMBER_OF_INTERVALS> timingIntervals;

    DISALLOW_COPY_AND_ASSIGN(SignalHandler);
};


#endif // SIGNAL_HANDLER_H
