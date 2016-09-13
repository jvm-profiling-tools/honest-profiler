#include <thread>
#include <vector>
#include <iostream>
#include "fixtures.h"
#include "test.h"
#include "../../main/cpp/agent.cpp"

TEST(ParseSetsDefaultOptions) {
    ConfigurationOptions options;
    parseArguments((char*) NULL, options);
    CHECK_EQUAL(options.samplingIntervalMin, DEFAULT_SAMPLING_INTERVAL);
    CHECK_EQUAL(options.samplingIntervalMax, DEFAULT_SAMPLING_INTERVAL);
    CHECK(!options.logFilePath);
}

TEST(ParsesSamplingInterval) {
    ConfigurationOptions options;
    parseArguments((char *) "interval=10", options);
    CHECK_EQUAL(10, options.samplingIntervalMin);
    CHECK_EQUAL(10, options.samplingIntervalMax);

    parseArguments((char *) "intervalMin=12,intervalMax=17", options);
    CHECK_EQUAL(12, options.samplingIntervalMin);
    CHECK_EQUAL(17, options.samplingIntervalMax);
}

TEST(ParsesLogPath) {
    ConfigurationOptions options;
    char* string = (char *) "logPath=/home/richard/log.hpl";
    parseArguments(string, options);
    CHECK(options.logFilePath > (string + strlen(string)));
    CHECK_EQUAL("/home/richard/log.hpl", options.logFilePath);
}

TEST(ParsesMultipleArguments) {
    ConfigurationOptions options;
    char* string = (char *) "interval=10,logPath=/home/richard/log.hpl";
    parseArguments(string, options);
    CHECK_EQUAL(10, options.samplingIntervalMin);
    CHECK_EQUAL(10, options.samplingIntervalMax);
    CHECK_EQUAL("/home/richard/log.hpl", options.logFilePath);
    safe_free_string(options.logFilePath);

    string = (char *) "logPath=/home/richard/log.hpl,interval=10";
    parseArguments(string, options);
    CHECK_EQUAL(10, options.samplingIntervalMin);
    CHECK_EQUAL(10, options.samplingIntervalMax);
    CHECK_EQUAL("/home/richard/log.hpl", options.logFilePath);
}

TEST(SafelyTerminatesStrings) {
    char* string = (char *) "/home/richard/log.hpl";
    char* result = safe_copy_string(string, NULL);

    CHECK_EQUAL(std::string("/home/richard/log.hpl"), result);
    CHECK_EQUAL('\0', result[21]);

    free(result);

    string = (char *) "/home/richard/log.hpl,interval=10";
    char* next = string + 21;
    result = safe_copy_string(string, next);

    CHECK_EQUAL(std::string("/home/richard/log.hpl"), result);
    CHECK_EQUAL('\0', result[21]);

    free(result);
}
