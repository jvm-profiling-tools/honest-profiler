#include "controller.h"

void controllerRunnable(jvmtiEnv *jvmti_env, JNIEnv *jni_env, void *arg) {
    IMPLICITLY_USE(jvmti_env);
    IMPLICITLY_USE(jni_env);
    Controller *control = (Controller *) arg;
    sigset_t mask;

    sigemptyset(&mask);
    sigaddset(&mask, SIGPROF);

    if (pthread_sigmask(SIG_BLOCK, &mask, NULL) < 0) {
        logError("ERROR: unable to set controller thread signal mask\n");
    }

    control->run();
}

void Controller::start() {
    JNIEnv *env = getJNIEnv(jvm_);
    jvmtiError result;

    if (env == NULL) {
        logError("ERROR: Failed to obtain JNIEnv\n");
        return;
    }

    isRunning_.store(true);

    jthread thread = newThread(env, "Honest Profiler Controller Thread");
    jvmtiStartFunction callback = controllerRunnable;
    result = jvmti_->RunAgentThread(thread, callback, this, JVMTI_THREAD_NORM_PRIORITY);

    if (result != JVMTI_ERROR_NONE) {
        logError("ERROR: Running controller thread failed with: %d\n", result);
    }
}

void Controller::stop() {
    isRunning_.store(false);
}

void Controller::run() {
    struct addrinfo hints, *res;
    char buf[MAX_DATA_SIZE];
    struct sockaddr_storage clientAddress;
    socklen_t addressSize = sizeof(clientAddress);
    ssize_t bytesRead;
    int result, listener, clientConnection;
    const int yes = 1;

    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    if ((result = getaddrinfo(configuration_->host, configuration_->port, &hints, &res)) != 0) {
        logError("ERROR: getaddrinfo: %s\n", gai_strerror(result));
        return;
    }

    if ((listener = socket(res->ai_family, res->ai_socktype, res->ai_protocol)) == -1) {
        logError("ERROR: Failed to open socket: %s\n", strerror(errno));
        return;
    }

    setsockopt(listener, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int));

    if (bind(listener, res->ai_addr, res->ai_addrlen) == -1) {
        logError("ERROR: Failed to bind successfully: %s\n", strerror(errno));
        close(listener);
        return;
    }

    if (listen(listener, 3) == -1) {
        logError("ERROR: Failed to listen: %s\n", strerror(errno));
        return;
    }

    while (isRunning_.load()) {
        if ((clientConnection = accept(listener, (struct sockaddr *) &clientAddress, &addressSize)) == -1) {
            logError("ERROR: Failed to accept incoming connection: %s\n", strerror(errno));
            continue;
        }

        if ((bytesRead = recv(clientConnection, buf, MAX_DATA_SIZE - 1, 0)) == -1) {
            if (bytesRead == 0) {
                // client closed the connection
            } else {
                logError("ERROR: Failed to read data from client: %s\n", strerror(errno));
            }
        } else {
            buf[bytesRead] = '\0';

            if (strstr(buf, "start") == buf) {
                startSampling();
            } else if (strstr(buf, "stop") == buf) {
                stopSampling();
            } else if (strstr(buf, "status") == buf) {
                reportStatus(clientConnection);
            } else {
                logError("WARN: Unknown command received, ignoring: %s\n", buf);
            }
        }

        close(clientConnection);
    }

    close(listener);
}

void Controller::startSampling() {
    JNIEnv *env = getJNIEnv(jvm_);

    if (env == NULL) {
        logError("ERROR: Failed to obtain JNI environment, cannot start sampling\n");
        return;
    }

    profiler_->start(env);
}

void Controller::stopSampling() {
    profiler_->stop();
}

void Controller::reportStatus(int clientConnection) {
    bool samplingIsRunning = profiler_->isRunning();
    // ensures there's space for status, log file path, comma, newline, and NUL
    int bufSize = strlen(configuration_->logFilePath) + 10;
    char buf[bufSize];

    snprintf(buf, bufSize, "%s,%s\n", samplingIsRunning ? "started" : "stopped", configuration_->logFilePath);

    int length = strlen(buf);

    if (send(clientConnection, buf, length, 0) <= 0) {
        logError("ERROR: Failed to respond to client: %s\n", strerror(errno));
    }
}
