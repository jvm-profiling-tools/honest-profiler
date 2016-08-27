#ifndef THREAD_MAP_H
#define THREAD_MAP_H

#include <jvmti.h>
#include <jni.h>

#include "concurrent_map.h"
#include <sys/syscall.h>
#include <unistd.h>

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

typedef ThreadMapBase<map::ConcurrentMapProvider<PointerHasher<JNIEnv>, false> > ThreadMap;

#endif
