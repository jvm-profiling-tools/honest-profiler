#ifndef THREAD_MAP_H
#define THREAD_MAP_H

#include <jvmti.h>
#include <jni.h>

#include "concurrent_map.h"

// debug
#define CONCURRENT_MAP_TBB
#include <iostream>

#ifdef CONCURRENT_MAP_TBB
	#include <tbb/concurrent_hash_map.h>
#endif

/* TODO: Test */
// http://source.winehq.org/git/wine.git/?a=blob;f=dlls/ntdll/server.c#l943
static int gettid() {
	int ret = -1;
#if defined(__linux__)
	ret = syscall(SYS_gettid);
#elif defined(__APPLE__)
	//ret = pthread_getthreadid_np();
	ret = mach_thread_self();
	mach_port_deallocate(mach_task_self(), ret);
#elif defined(__NetBSD__)
	ret = _lwp_self();
#elif defined(__FreeBSD__)
	long lwpid;
	thr_self(&lwpid);
	ret = lwpid;
#elif defined(__DragonFly__)
	ret = lwp_gettid();
#else
	ret = pthread_self();
#endif
	return ret;
}

struct ThreadBucket {
    int tid;
    jthread thread; 
    jvmtiEnv *tiEnv;
};

#ifdef CONCURRENT_MAP_TBB

const int kTraceTbbMapTotal = 3;

const int kTraceTbbMapPut = 0;
const int kTraceTbbMapGet = 1;
const int kTraceTbbMapRemove = 2;

TRACE_DECLARE(TbbMap, kTraceTbbMapTotal);

class TbbMapProvider : public AbstractMapProvider {
private:
	typedef tbb::concurrent_hash_map<void*, void*> HashMap;
	HashMap map;

public:
	TbbMapProvider(size_t initialSize) : map(initialSize) {}

	virtual ~TbbMapProvider() {}

	void put(void *key, void *value) {
		TRACE(TbbMap, kTraceTbbMapPut);
		HashMap::accessor acc;
		map.insert(acc, key);
		acc->second = value;
	}

	void *get(void *key) {
		TRACE(TbbMap, kTraceTbbMapGet);
		HashMap::accessor acc;
		return map.find(acc, key) ? acc->second : NULL;
	}

	void *remove(void *key) {
		TRACE(TbbMap, kTraceTbbMapRemove);
		void *old = get(key);
		map.erase(key);
		return old;
	}
};

#endif

template <typename PType>
struct PointerHasher {
	/* pointer hash bijection (collision-free) */
	static int64_t hash(void *p) {
		// remove LSB zeros in pointers
		return ((int64_t)p) / sizeof(PType);
	}
};

#define INITIAL_CONCURRENT_MAP_SIZE 256

template <typename MapProvider>
class ThreadMapBase {
private:
	MapProvider map;

public:

	ThreadMapBase(int capacity=INITIAL_CONCURRENT_MAP_SIZE) : map(capacity) {}

	void put(JNIEnv *jni_env, jvmtiEnv *jvmti_env, jthread thread) {
		ThreadBucket *info = new ThreadBucket;
        info->tid = gettid();
        info->tiEnv = jvmti_env;
        info->thread = jni_env->NewGlobalRef(thread);
        map.put(jni_env, info);
	}

	ThreadBucket *get(JNIEnv *jni_env) {
		return reinterpret_cast<ThreadBucket*>(map.get(jni_env));
	}

	void remove(JNIEnv *jni_env) {
    	ThreadBucket *info = (ThreadBucket*)map.remove(jni_env);
    	if (info) {
    		jni_env->DeleteGlobalRef(info->thread);
    		delete info;
    	}
	}
};

#ifdef CONCURRENT_MAP_TBB
typedef ThreadMapBase<TbbMapProvider> ThreadMap;
typedef ThreadMap TbbThreadMap;
#else
typedef ThreadMapBase<LockFreeMapProvider<PointerHasher<JNIEnv>, true> > ThreadMap;
#endif

typedef ThreadMapBase<LockFreeMapProvider<PointerHasher<JNIEnv>, true> > LockFreeThreadMap;

#endif
