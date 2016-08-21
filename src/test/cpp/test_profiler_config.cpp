#include <thread>
#include <vector>
#include <iostream>
#include "test.h"
#include "../../main/cpp/profiler.h"

class ProfilerControl {
public:
  Profiler *profiler;
  ProfilerTestHelper *helper;
  ConfigurationOptions *liveConfig;
  ConfigurationOptions *currentConfig;

public:
  ProfilerControl() {
    liveConfig = new ConfigurationOptions();
    profiler = new Profiler(NULL, NULL, liveConfig);
    helper = new ProfilerTestHelper(profiler);
    currentConfig = helper->getHiddenConfig();
  }

  ~ProfilerControl() {
    delete helper;
    delete profiler;
    delete liveConfig;
  }
};

TEST_FIXTURE(ProfilerControl, ProfilerInitialization) {
	CHECK(profiler);
	CHECK(!profiler->isRunning());
	CHECK(helper);
	CHECK(liveConfig);
	CHECK(currentConfig);

	// check that content in configs is the same
	CHECK_EQUAL(liveConfig->samplingIntervalMin, currentConfig->samplingIntervalMin);
	CHECK_EQUAL(liveConfig->samplingIntervalMax, currentConfig->samplingIntervalMax);
	CHECK_EQUAL(liveConfig->logFilePath, currentConfig->logFilePath);
	CHECK_EQUAL(liveConfig->maxFramesToCapture, currentConfig->maxFramesToCapture);
}

TEST_FIXTURE(ProfilerControl, ProfilerChangeSettings) {
	int newSamplingIntervalMin = 13;
	int newSamplingIntervalMax = 17;
	int newMaxFramesToCapture = 225;
	char *newFilePath1 = (char*)"/dev/null";
	char *newFilePath2 = (char*)"/dev/sda";

	// modify settings
	CHECK(!profiler->isRunning());
	profiler->setSamplingInterval(newSamplingIntervalMin, newSamplingIntervalMax);
	profiler->setMaxFramesToCapture(newMaxFramesToCapture);
	profiler->setFilePath(newFilePath1);

	// check that getters return correct new values
	CHECK_EQUAL(newSamplingIntervalMin, profiler->getSamplingIntervalMin());
	CHECK_EQUAL(newSamplingIntervalMax, profiler->getSamplingIntervalMax());
	CHECK_EQUAL(newMaxFramesToCapture, profiler->getMaxFramesToCapture());
	CHECK_EQUAL(std::string(newFilePath1), profiler->getFilePath());

	// check that content in configs is defferent
	CHECK(currentConfig->samplingIntervalMin != profiler->getSamplingIntervalMin());
	CHECK(currentConfig->samplingIntervalMax != profiler->getSamplingIntervalMax());
	CHECK(currentConfig->maxFramesToCapture != profiler->getMaxFramesToCapture());
	CHECK(std::string(currentConfig->logFilePath) != profiler->getFilePath());

	int prev = Trace_ProcessorMock[kTraceProcMockStart].count.load();
	// start profiler
	CHECK(profiler->start());
	CHECK(profiler->isRunning());
	CHECK_EQUAL(prev + 1, Trace_ProcessorMock[kTraceProcMockStart].count.load());

	// start it once again
	CHECK(profiler->start());
	CHECK_EQUAL(prev + 1, Trace_ProcessorMock[kTraceProcMockStart].count.load());

	// check that content was syncronized in configs
	CHECK_EQUAL(liveConfig->samplingIntervalMin, currentConfig->samplingIntervalMin);
	CHECK_EQUAL(liveConfig->samplingIntervalMax, currentConfig->samplingIntervalMax);
	CHECK_EQUAL(liveConfig->logFilePath, currentConfig->logFilePath);
	CHECK_EQUAL(liveConfig->maxFramesToCapture, currentConfig->maxFramesToCapture);

	// check that content is correct
	CHECK_EQUAL(newSamplingIntervalMin, currentConfig->samplingIntervalMin);
	CHECK_EQUAL(newSamplingIntervalMax, currentConfig->samplingIntervalMax);
	CHECK_EQUAL(newMaxFramesToCapture, currentConfig->maxFramesToCapture);
	CHECK_EQUAL(newFilePath1, currentConfig->logFilePath);

	// no changes allowed when profiler is running
	profiler->setFilePath(newFilePath2);
	CHECK_EQUAL(std::string(newFilePath1), profiler->getFilePath());

	// check that memory is freed in live config
	profiler->stop();
	profiler->setFilePath(newFilePath2);
	profiler->setFilePath(NULL);

	// set bad input 
	profiler->setMaxFramesToCapture(-100);

	// check that memory is freed in hidden config
	profiler->start();
	CHECK((char*)NULL != currentConfig->logFilePath);
	CHECK(std::string() != profiler->getFilePath());
	CHECK(profiler->getMaxFramesToCapture() > 0);

	profiler->stop();
}

TEST_FIXTURE(ProfilerControl, ProfilerConcurrentStartStop) {
	bool (Profiler::*foo)(void) = &Profiler::start;
	const int tsize = 4;
	std::vector<std::thread> threads(tsize);

	for (int it = 0; it < 100; it++) {
		int prev = Trace_ProcessorMock[kTraceProcMockStart].count.load();
		for (int i = 0; i < tsize; i++)
			threads[i] = std::thread(foo, std::ref(profiler));
		for (int i = 0; i < tsize; i++)
			threads[i].join();

		// only 1 will succeed
		CHECK_EQUAL(prev + 1, Trace_ProcessorMock[kTraceProcMockStart].count.load());
		CHECK(profiler->isRunning());

		prev = Trace_ProcessorMock[kTraceProcMockStop].count.load();
		for (int i = 0; i < tsize; i++)
			threads[i] = std::thread(&Profiler::stop, std::ref(profiler));
		for (int i = 0; i < tsize; i++)
			threads[i].join();

		CHECK_EQUAL(prev + 1, Trace_ProcessorMock[kTraceProcMockStop].count.load());
		CHECK(!profiler->isRunning());
	}
}

TEST_FIXTURE(ProfilerControl, ProfilerConcurrentModification) {
	bool (Profiler::*startFoo)(void) = &Profiler::start;
	void (Profiler::*setFoo)(int) = &Profiler::setMaxFramesToCapture;
	const int tsize = 4;
	int totals[tsize + 1] = {0};
	std::vector<std::thread> threads(tsize);
	TraceGroup_Profiler.reset();

	for (int it = 0; it < 100; it++) {
		profiler->setMaxFramesToCapture(tsize + 1);
		for (int i = 0; i < tsize; i++) {
			threads[i] = std::thread(setFoo, std::ref(profiler), i + 1);
		}
		std::thread starter(startFoo, std::ref(profiler));
		for (int i = 0; i < tsize; i++)
			threads[i].join();
		starter.join();
		
		int val = profiler->getMaxFramesToCapture();
		CHECK(0 < val && val < tsize + 2);
		totals[val - 1]++;

		profiler->stop();
	}

	int p50 = (tsize + 1) >> 1; // at least 50% should be != 0
	int check = 0;
	for (int i = 0; i < tsize + 1; i++) {
		check += (totals[i] != 0);
	}
	CHECK(check >= p50);

	//std::cout << "#### Concurrent modification tracing results:\n";
	//TraceGroup_Profiler.dumpIfUsed();
}
