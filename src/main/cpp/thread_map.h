#ifndef THREAD_MAP_H
#define THREAD_MAP_H

#include <jvmti.h>
#include <jni.h>

#if __GNUC__ == 4 && __GNUC_MINOR__ < 6 && !defined(__APPLE__) && !defined(__FreeBSD__) 
  #include <cstdatomic>
#else
  #include <atomic>
#endif

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

class AbstractMapProvider {
public:
	virtual void put(void *key, void *value) = 0;
	virtual void *get(void *key) = 0;
	virtual void *remove(void *key) = 0;
	virtual ~AbstractMapProvider() {}
};

#ifdef CONCURRENT_MAP_TBB

class TbbMapProvider : public AbstractMapProvider {
private:
	typedef tbb::concurrent_hash_map<void*, void*> HashMap;
	HashMap map;

public:

	TbbMapProvider(size_t initialSize) : map(initialSize) {}

	virtual ~TbbMapProvider() {}

	void put(void *key, void *value) {
		HashMap::accessor acc;
		map.insert(acc, key);
		acc->second = value;
	}

	void *get(void *key) {
		HashMap::accessor acc;
		return map.find(acc, key) ? acc->second : NULL;
	}

	void *remove(void *key) {
		void *old = get(key);
		map.erase(key);
		return old;
	}

	/* unsafe */
	size_t size() {
		return map.size();
	}
};

#endif

#define MAP_HASH_NULL (int64_t)-1

struct LockFreeMapEntry {
	std::atomic<int64_t> hash;
	std::atomic<void*> value;

	LockFreeMapEntry() : hash(MAP_HASH_NULL), value(NULL) {}
	LockFreeMapEntry(int64_t h, void *v) : hash(h), value(v) {}
};

template <typename PType>
struct PointerHasher {
	/* pointer hash bijection (collision-free) */
	static int64_t hash(void *p) {
		// remove LSB zeros in pointers
		return ((int64_t)p) / sizeof(PType);
	}
};

/* Hasher assumed to be collision free. */
template <typename Hasher>
class LockFreeMapProvider : public AbstractMapProvider {
private:
	LockFreeMapEntry *array;
	int allocatedSize;
	std::atomic_int freeBuckets;

	LockFreeMapEntry* lookup(void *key) {
		int64_t tHash = Hasher::hash(key);
		int sizeMask = allocatedSize - 1;
		int i = tHash & sizeMask;
		for (;; ++i) {
			i &= sizeMask;

			LockFreeMapEntry* entr = array + i;
			int64_t bucketHash = entr->hash.load(std::memory_order_relaxed);
		
			if (bucketHash == MAP_HASH_NULL) { // not found
				return NULL;
			} else if (bucketHash == tHash) {
				return entr;
			}
		}
	}

public:
	LockFreeMapProvider(size_t initialSize) {
		allocatedSize = nearestPow2(initialSize);
		freeBuckets = allocatedSize;
		array = new LockFreeMapEntry[allocatedSize];
	}

	virtual ~LockFreeMapProvider() {
		delete[] array;
	}

	void put(void *key, void *value) {
		int64_t tHash = Hasher::hash(key);
		int sizeMask = allocatedSize - 1;
		int i = tHash & sizeMask;
		for (;; ++i) {
			i &= sizeMask;

			LockFreeMapEntry* entr = array + i;
			int64_t bucketHash = entr->hash.load(std::memory_order_relaxed);
		
			if (bucketHash == MAP_HASH_NULL) { // unallocated bucket
				int cellsAfterInsert = freeBuckets.fetch_sub(1, std::memory_order_relaxed);

				if (cellsAfterInsert <= 0) {
					std::cerr << "Error: Attempt to insert to full map\n";
					freeBuckets.fetch_add(1, std::memory_order_relaxed);
					return;
				}

				if (entr->hash.compare_exchange_strong(bucketHash, tHash, std::memory_order_relaxed)) {
					bucketHash = tHash;
				} else {
					/*  Potentially we can continue on failed CAS, since hash collisions are not allowed and
			 	 	*  failed CAS only means that bucket was allocated for different hash, but allow double 
			 	 	*  check for a future modifications.
			 	 	*/
			 	 	freeBuckets.fetch_add(1, std::memory_order_relaxed);
				}
			}

			if (bucketHash == tHash) { // CAS succeeded or allocated bucket found
				void *oldValue = entr->value.load(std::memory_order_relaxed); // we are only interested in adress
				if (oldValue == value ||
					entr->value.compare_exchange_strong(oldValue, value, std::memory_order_acq_rel)) {
					break;
				}
			}
		}
	}

	void *get(void *key) {
		LockFreeMapEntry *el = lookup(key);
		return el ? el->value.load(std::memory_order_consume) : NULL;
	}

	void *remove(void *key) {
		LockFreeMapEntry *entr = lookup(key);
		if (!entr)
			return NULL;

		void *oldValue = entr->value.load(std::memory_order_relaxed);
		while (true) {
			if (entr->value.compare_exchange_strong(oldValue, NULL, std::memory_order_consume))
				return oldValue;
		}
	}

	/* reports number of occupied buckets */
	size_t size() {
		return allocatedSize - freeBuckets.load(std::memory_order_relaxed);
	}

	/* nearest (larger) power of 2 */
	static unsigned int nearestPow2(unsigned int x) {
		x--;
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		return x + 1;
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
typedef ThreadMapBase<LockFreeMapProvider<PointerHasher<JNIEnv> > > ThreadMap;
#endif

typedef ThreadMapBase<LockFreeMapProvider<PointerHasher<JNIEnv> > > LockFreeThreadMap;

#endif
