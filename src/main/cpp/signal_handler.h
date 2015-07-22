#ifndef SIGNAL_HANDLER_H
#define SIGNAL_HANDLER_H

#include <signal.h>
#include <stdlib.h>
#include <sys/time.h>

#include <fstream>
#include <unistd.h>
#include <chrono>

#include "globals.h"

const int NUMBER_OF_INTERVALS = 1024;

class SignalHandler {
public:
    SignalHandler(const int samplingIntervalMin, const int samplingIntervalMax) {
        intervalIndex = 0;
        timingIntervals = new int[NUMBER_OF_INTERVALS];
        srand (time(NULL));
        int range = samplingIntervalMax - samplingIntervalMin + 1;
        for (int i = 0; i < NUMBER_OF_INTERVALS; i++) {
            timingIntervals[i] = samplingIntervalMin + rand() % range;
        }
        currentInterval = -1;
    }

    struct sigaction SetAction(void (*sigaction)(int, siginfo_t *, void *));

    bool updateSigprofInterval();

    bool updateSigprofInterval(int);

    bool stopSigprof() { return updateSigprofInterval(0); }

private:
    int intervalIndex;
    int *timingIntervals;
    int currentInterval;

    DISALLOW_COPY_AND_ASSIGN(SignalHandler);
};


#endif // SIGNAL_HANDLER_H
