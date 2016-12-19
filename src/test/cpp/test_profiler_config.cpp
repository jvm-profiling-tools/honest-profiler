#include <thread>
#include <vector>
#include <iostream>
#include "test.h"
#include "../../main/cpp/profiler.h"

#ifndef __APPLE__

static JavaVM *jvm = NULL;
static JNIEnv *env = NULL;
static jvmtiEnv *jvmti = NULL;
static ThreadMap threadMap; // empty map

static void init() {
	if (jvm) return;

	int res;
	JavaVMInitArgs vm_args;
    vm_args.version = JNI_VERSION_1_6;
    vm_args.nOptions = 0;
    vm_args.options = NULL;
    vm_args.ignoreUnrecognized = false;
    
    res = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
    if (res < 0 || !env) {
    	std::cerr << "Can't create a VM instance (err: " << res << ")\n";
    	return;
    }

    res = jvm->GetEnv((void**)&jvmti, JVMTI_VERSION);
    if (res != JNI_OK) {
    	std::cerr << "Can't get a jvmti (err: " << res << ")\n";
    	return;
    }
}

class ProfilerControl {
public:
	Profiler *profiler;
	ConfigurationOptions *liveConfig;

public:
	ProfilerControl() {
		init();
		liveConfig = new ConfigurationOptions();
		profiler = new Profiler(jvm, jvmti, liveConfig, threadMap);

		// otherwise Profiler::handle called from bootstrapHandle in agent.cpp will fail
		setProfiler(profiler);
		Asgct::SetAsgct(Accessors::GetJvmFunction<ASGCTType>("AsyncGetCallTrace"));
	}

	~ProfilerControl() {
		delete profiler;
		delete liveConfig;
	}
};

static void threadStartFunction(Profiler *p) {
	JNIEnv *currEnv;

	int res = jvm->AttachCurrentThread((void**)&currEnv, NULL);
	if (res < 0 || currEnv == NULL) {
		std::cerr << "Can't create JNI instance (err: " << res << ")\n";
    	return;
	}

	p->start(currEnv);
	jvm->DetachCurrentThread();	
}


static void threadStopFunction(Profiler *p) {
	// no need to attach to VM
	p->stop();
}

TEST_FIXTURE(ProfilerControl, ProfilerInitialization) {
	CHECK(profiler);
	CHECK(!profiler->isRunning());
	CHECK(liveConfig);

	CHECK_EQUAL(liveConfig->samplingIntervalMin, profiler->getSamplingIntervalMin());
	CHECK_EQUAL(liveConfig->samplingIntervalMax, profiler->getSamplingIntervalMax());
	CHECK_EQUAL(liveConfig->maxFramesToCapture, profiler->getMaxFramesToCapture());
	CHECK_EQUAL(std::string(liveConfig->logFilePath), profiler->getFilePath());
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

#ifdef ENABLE_TRACING
	int prev = Trace_Processor[kTraceProcessorStart].count.load();
#endif

	// start profiler
	CHECK(profiler->start(env));
	CHECK(profiler->isRunning());

#ifdef ENABLE_TRACING
	CHECK_EQUAL(prev + 1, Trace_Processor[kTraceProcessorStart].count.load());
#endif

	// start it once again
	CHECK(profiler->start(env));

#ifdef ENABLE_TRACING
	CHECK_EQUAL(prev + 1, Trace_Processor[kTraceProcessorStart].count.load());
#endif

	// check that getters return correct new values
	CHECK_EQUAL(newSamplingIntervalMin, profiler->getSamplingIntervalMin());
	CHECK_EQUAL(newSamplingIntervalMax, profiler->getSamplingIntervalMax());
	CHECK_EQUAL(newMaxFramesToCapture, profiler->getMaxFramesToCapture());
	CHECK_EQUAL(std::string(newFilePath1), profiler->getFilePath());

	// no changes allowed when profiler is running
	profiler->setFilePath(newFilePath2);
	CHECK_EQUAL(std::string(newFilePath1), profiler->getFilePath());

	// set 2 values in a row (valgrind: check that memory is reclaimed)
	profiler->stop();
	profiler->setFilePath(newFilePath2);
	profiler->setFilePath(NULL);

	// set bad input 
	profiler->setMaxFramesToCapture(-100);

	// check that memory is freed in hidden config
	profiler->start(env);
	CHECK(std::string() != profiler->getFilePath());
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
	int totals[tsize + 1] = {0};
	std::vector<std::thread> threads(tsize);
	TraceGroup_Profiler.reset();

	for (int it = 0; it < 100; it++) {
		profiler->setMaxFramesToCapture(tsize + 1);
		for (int i = 0; i < tsize; i++) {
			threads[i] = std::thread(setFoo, std::ref(profiler), i + 1);
		}
		std::thread starter(&threadStartFunction, std::ref(profiler));
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

#ifdef ENABLE_TRACING
	//std::cout << "#### Concurrent modification tracing results:\n";
	//TraceGroup_Profiler.dumpIfUsed();
#endif
}

#endif