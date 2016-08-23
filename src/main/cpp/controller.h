#ifndef HONEST_PROFILER_CONTROLLER_H
#define HONEST_PROFILER_CONTROLLER_H

#include "globals.h"
#include "common.h"
#include "profiler.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <jvmti.h>

#define MAX_DATA_SIZE 100

class Controller {
public:
    explicit Controller(JavaVM *jvm, jvmtiEnv *jvmti, Profiler *profiler, ConfigurationOptions *configuration) :
            jvm_(jvm), jvmti_(jvmti), profiler_(profiler), configuration_(configuration), isRunning_(false) {

    }

    void start();

    void stop();

    void run();

    bool isRunning() const;

private:
    JavaVM *jvm_;
    jvmtiEnv *jvmti_;
    Profiler *profiler_;
    ConfigurationOptions *configuration_;
    std::atomic_bool isRunning_;


    void startSampling();

    void stopSampling();

    void reportStatus(int clientConnection);

    void getProfilerParam(int clientConnection, char *param);

    void setProfilerParam(char *paramDesc);
};

#endif
