#include "test.h"

#ifndef DISABLE_CPP11

#include <thread>
#include <vector>
#include <iostream>
#include "../../main/cpp/profiler.h"

#ifndef TEST_SKIP_PROFILER

static ThreadMap threadMap; // empty map

class ProfilerControl {
public:
	Profiler *profiler;
	ConfigurationOptions liveConfig;

public:
	ProfilerControl() {
		profiler = new Profiler(NULL, NULL, liveConfig, threadMap);

		setProfiler(profiler);
	}

	~ProfilerControl() {
		delete profiler;
	}
};

static void threadStartFunction(Profiler *p) {
	p->start(NULL);
}


static void threadStopFunction(Profiler *p) {
	// no need to attach to VM
	p->stop();
}

TEST_FIXTURE(ProfilerControl, ProfilerInitialization) {
	CHECK(profiler);
	CHECK(!profiler->isRunning());

	CHECK_EQUAL(liveConfig.samplingIntervalMin, profiler->getSamplingIntervalMin());
	CHECK_EQUAL(liveConfig.samplingIntervalMax, profiler->getSamplingIntervalMax());
	CHECK_EQUAL(liveConfig.maxFramesToCapture, profiler->getMaxFramesToCapture());

	std::string path = profiler->getFilePath();
	std::string expectedPrefix = "log-";
	std::string expectedSuffix = ".hpl";
	CHECK(path.compare(0, expectedPrefix.size(), expectedPrefix) == 0 && 
		path.compare(path.size() - expectedSuffix.size(), expectedSuffix.size(), expectedSuffix) == 0);
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
	CHECK_EQUAL(newFilePath1, profiler->getFilePath());

#ifdef ENABLE_TRACING
	int prev = Trace_Processor[kTraceProcessorStart].count.load();
#endif

	// start profiler
	CHECK(profiler->start(NULL));
	CHECK(profiler->isRunning());

#ifdef ENABLE_TRACING
	CHECK_EQUAL(prev + 1, Trace_Processor[kTraceProcessorStart].count.load());
#endif

	// start it once again
	CHECK(profiler->start(NULL));

#ifdef ENABLE_TRACING
	CHECK_EQUAL(prev + 1, Trace_Processor[kTraceProcessorStart].count.load());
#endif

	// check that getters return correct new values
	CHECK_EQUAL(newSamplingIntervalMin, profiler->getSamplingIntervalMin());
	CHECK_EQUAL(newSamplingIntervalMax, profiler->getSamplingIntervalMax());
	CHECK_EQUAL(newMaxFramesToCapture, profiler->getMaxFramesToCapture());
	CHECK_EQUAL(newFilePath1, profiler->getFilePath());

	// no changes allowed when profiler is running
	profiler->setFilePath(newFilePath2);
	CHECK_EQUAL(std::string(newFilePath1), profiler->getFilePath());

	// set 2 values in a row (valgrind: check that memory is reclaimed)
	profiler->stop();
	profiler->setFilePath(newFilePath2);
	profiler->setFilePath((char*)"");

	// set bad input 
	profiler->setMaxFramesToCapture(-100);

	// check that memory is freed in hidden config
	profiler->start(NULL);
	CHECK("" != profiler->getFilePath());
	CHECK(profiler->getMaxFramesToCapture() > 0);

	profiler->stop();
}

TEST_FIXTURE(ProfilerControl, ProfilerConcurrentStartStop) {
	const int tsize = 1;
	std::vector<std::thread> threads(tsize);

	for (int it = 0; it < 100; it++) {
#ifdef ENABLE_TRACING
		int prev = Trace_Processor[kTraceProcessorStart].count.load();
#endif
		for (int i = 0; i < tsize; i++)
			threads[i] = std::thread(&threadStartFunction, std::ref(profiler));
		for (int i = 0; i < tsize; i++)
			threads[i].join();

#ifdef ENABLE_TRACING
		// only 1 will succeed
		CHECK_EQUAL(prev + 1, Trace_Processor[kTraceProcessorStart].count.load());
#endif
		CHECK(profiler->isRunning());

#ifdef ENABLE_TRACING
		prev = Trace_Processor[kTraceProcessorStop].count.load();
#endif
		for (int i = 0; i < tsize; i++)
			threads[i] = std::thread(&threadStopFunction, std::ref(profiler));
		for (int i = 0; i < tsize; i++)
			threads[i].join();

#ifdef ENABLE_TRACING
		CHECK_EQUAL(prev + 1, Trace_Processor[kTraceProcessorStop].count.load());
#endif
		CHECK(!profiler->isRunning());
	}
}

TEST_FIXTURE(ProfilerControl, ProfilerConcurrentModification) {
	void (Profiler::*setFoo)(int) = &Profiler::setMaxFramesToCapture;
	const int tsize = 4;
	std::vector<std::thread> threads(tsize);
	TraceGroup_Profiler.reset();

	for (int it = 0; it < 100; it++) {
		profiler->setMaxFramesToCapture(tsize + 1);
		for (int i = 0; i < tsize; i++) {
			threads[i] = std::thread(setFoo, profiler, i + 1);
		}
		std::thread starter(&threadStartFunction, std::ref(profiler));
		for (int i = 0; i < tsize; i++)
			threads[i].join();
		starter.join();
		
		int val = profiler->getMaxFramesToCapture();
		CHECK(0 < val && val < tsize + 2);

		profiler->stop();
	}
}

#endif

#endif // DISABLE_CPP11