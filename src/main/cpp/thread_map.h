#ifndef THREAD_MAP_H
#define THREAD_MAP_H

// debug
#define CONCURRENT_MAP_TBB

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

class TbbMapProvider {
private:
	typedef tbb::concurrent_hash_map<void*, void*> HashMap;
	HashMap map;

public:
	void put(void *key, void *value) {
		HashMap::accessor acc;
		map.insert(acc, key);
		acc->second = value;
	}

	void *get(void *key) {
		HashMap::accessor acc;
		return map.find(acc, key) ? acc->second : NULL;
	}

	void remove(void *key) {
		map.erase(key);
	}
};

#endif


class LockFreeMapProvider {
public:
	void put(void *key, void *value) {}

	void *get(void *key) {
		return NULL;
	}

	void remove(void *key) {}
};


template <typename MapProvider>
class ThreadMapBase {
private:
	MapProvider map;

public:
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
    	ThreadBucket *info = get(jni_env);
    	if (info) {
    		map.remove(jni_env);
    		jni_env->DeleteGlobalRef(info->thread);
    		delete info;
    	}
	}
};

#ifdef CONCURRENT_MAP_TBB
typedef ThreadMapBase<TbbMapProvider> ThreadMap;
#else
typedef ThreadMapBase<LockFreeMapProvider> ThreadMap;
#endif

typedef ThreadMapBase<TbbMapProvider> TbbMap;
typedef ThreadMapBase<LockFreeMapProvider> LockFreeMap;

#endif