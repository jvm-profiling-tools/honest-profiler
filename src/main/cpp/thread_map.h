#ifndef THREAD_MAP_H
#define THREAD_MAP_H

#include <jvmti.h>
#include <jni.h>
#include "concurrent_map.h"


int gettid();

struct ThreadBucket {
	int tid;
	jthread thread;
};

template <typename PType>
struct PointerHasher {
	/* pointer hash bijection (collision-free) */
	static int64_t hash(void *p) {
		// remove LSB zeros in pointers
		return ((int64_t)p) / sizeof(PType);
	}
};

const int kInitialMapSize = 256;
const int KSafepointTrigger = 32; // each 32nd read results in safepoint

thread_local static map::GC::EpochType localEpoch = map::GC::kEpochInitial;
thread_local static int mapUsageCounter = 0;

class GCHelper {
public:
	static void safepointOnDemand() {
		if (!(mapUsageCounter++ & (KSafepointTrigger - 1))) // mapUsageCounter = 0 (mod KSafepointTrigger)
			map::DefaultGC.safepoint(localEpoch);
	}

	static void attach() {
		if (localEpoch == map::GC::kEpochInitial)
			localEpoch = map::DefaultGC.attachThread();
	}

	static void detach() {
		if (localEpoch != map::GC::kEpochInitial)
			map::DefaultGC.detachThread(localEpoch);
	}

	static void safepoint() {
		if (localEpoch == map::GC::kEpochInitial) attach();
		map::DefaultGC.safepoint(localEpoch);
	}
};

template <typename MapProvider>
class ThreadMapBase {
private:
	MapProvider map;

public:

	ThreadMapBase(int capacity = kInitialMapSize) : map(capacity) {}

	void put(JNIEnv *jni_env, jthread thread) {
		put(jni_env, thread, gettid());
	}

	void put(JNIEnv *jni_env, jthread thread, int tid, bool globalRef = true) {
		GCHelper::attach();
		ThreadBucket *info = new ThreadBucket;
		info->tid = tid;
		info->thread = globalRef ? thread : jni_env->NewGlobalRef(thread);
		map.put((map::KeyType)jni_env, (map::ValueType)info);
		GCHelper::safepoint(); // each thread inserts once
	}

	ThreadBucket *get(JNIEnv *jni_env) {
		GCHelper::attach();
		GCHelper::safepointOnDemand();
		return reinterpret_cast<ThreadBucket*>(map.get((map::KeyType)jni_env));
	}

	void remove(JNIEnv *jni_env) {
		GCHelper::attach();
		ThreadBucket *info = (ThreadBucket*)map.remove((map::KeyType)jni_env);
		if (info) {
			jni_env->DeleteGlobalRef(info->thread);
			delete info;
		}
		GCHelper::safepoint(); // each thread deletes once
	}

	void attach() {
		GCHelper::attach();
	}

	void detach() {
		GCHelper::detach();
	}
};

typedef ThreadMapBase<map::ConcurrentMapProvider<PointerHasher<JNIEnv>, false> > ThreadMap;

#endif
