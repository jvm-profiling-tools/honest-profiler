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
    SignalHandler(const int samplingInterval) {
        intervalIndex = 0;
        timingIntervals = new int[NUMBER_OF_INTERVALS];
        srand (time(NULL));
        for (int i = 0; i < NUMBER_OF_INTERVALS; i++) {
            timingIntervals[i] = rand() % samplingInterval + (samplingInterval / 2);
        }
    }

    struct sigaction SetAction(void (*sigaction)(int, siginfo_t *, void *));

    bool updateSigprofInterval();

    bool updateSigprofInterval(int);

private:
    int intervalIndex;

    int *timingIntervals;

    DISALLOW_COPY_AND_ASSIGN(SignalHandler);
};


#endif // SIGNAL_HANDLER_H
