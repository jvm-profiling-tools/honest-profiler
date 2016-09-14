#ifndef THREAD_MAP_H
#define THREAD_MAP_H

#include <jvmti.h>
#include <jni.h>
#include "concurrent_map.h"


int gettid();

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

class GCHelper {
public:
	static void safepointOnDemand(map::GC::EpochType &localEpoch, unsigned int &count) {
		if (!(count++ & (KSafepointTrigger - 1))) // count = 0 (mod KSafepointTrigger)
			map::DefaultGC.safepoint(localEpoch);
	}

	static map::GC::EpochType attach() {
		return map::DefaultGC.attachThread();
	}

	static void detach(map::GC::EpochType &localEpoch) {
		if (localEpoch != map::GC::kEpochInitial)
			map::DefaultGC.detachThread(localEpoch);
	}

	static void safepoint(map::GC::EpochType &localEpoch) {
		map::DefaultGC.safepoint(localEpoch);
	}
};

struct ThreadBucket {
	const int tid;
	const jthread thread;
	map::GC::EpochType localEpoch;
	std::atomic_int refs;

	ThreadBucket(int id, jthread thr) : tid(id), thread(thr), localEpoch(GCHelper::attach()), refs(1) {
	}
};

// MWSR
template <typename MapProvider>
class ThreadMapBase {
private:
	MapProvider map;
	map::GC::EpochType readerEpoch;
	unsigned int readerCounter;

	static ThreadBucket *acq_bucket(ThreadBucket *tb) {
		if (tb != nullptr) {
			int prev = tb->refs.fetch_add(1, std::memory_order_relaxed);
			if (prev > 0) {
				return tb;
			}
			tb->refs.fetch_sub(1, std::memory_order_relaxed);
		}
		return nullptr;
	}

	static void rel_bucket(ThreadBucket *tb, JNIEnv *jni_env) {
		if (tb != nullptr) {
			int prev = tb->refs.fetch_sub(1, std::memory_order_acquire);
			if (prev == 1) {
				jni_env->DeleteGlobalRef(tb->thread);
				delete tb;
			}
		}
	}

public:

	ThreadMapBase(int capacity = kInitialMapSize) : map(capacity) {
		readerEpoch = map::GC::kEpochInitial;
		readerCounter = 0;
	}

	void put(JNIEnv *jni_env, jthread thread) {
		put(jni_env, thread, gettid());
	}

	void put(JNIEnv *jni_env, jthread thread, int tid, bool globalRef = true) {
		// constructor == call to acquire
		ThreadBucket *info = new ThreadBucket(tid, globalRef ? thread : jni_env->NewGlobalRef(thread));
		ThreadBucket *old = (ThreadBucket*)map.put((map::KeyType)jni_env, (map::ValueType)info);
		rel_bucket(old, jni_env);
		GCHelper::safepoint(info->localEpoch); // each thread inserts once
	}

	ThreadBucket *get(JNIEnv *jni_env) {
		GCHelper::safepointOnDemand(readerEpoch, readerCounter);
		return acq_bucket((ThreadBucket*)map.get((map::KeyType)jni_env));
	}

	void remove(JNIEnv *jni_env) {
		ThreadBucket *info = (ThreadBucket*)map.remove((map::KeyType)jni_env);
		if (info) {
			GCHelper::detach(info->localEpoch);
			rel_bucket(info, jni_env);
		}
	}

	void attachReader() {
		if (readerEpoch == map::GC::kEpochInitial) {
			readerEpoch = GCHelper::attach();
			readerCounter = 0;
		}
	}

	void detachReader() {
		if (readerEpoch != map::GC::kEpochInitial) {
			GCHelper::detach(readerEpoch);
		}
	}
};

typedef ThreadMapBase<map::ConcurrentMapProvider<PointerHasher<JNIEnv>, false> > ThreadMap;

#endif
