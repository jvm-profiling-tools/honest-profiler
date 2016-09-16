#include "test.h"
#include "../../main/cpp/thread_map.h"

#include <sstream>
#include <algorithm>
#include <thread>
#include <vector>
#include <chrono>

using namespace map;

struct TestHasher {
	static int64_t hash(void *p) {
  		return (int64_t)p / sizeof(int);
	}
};

typedef ConcurrentMapProvider<TestHasher, false> TestLockFreeMap;
typedef void (*MapFunction)(AbstractMapProvider &, void **, void **, std::atomic<void*> *, size_t, bool);

void **allocateTestBuffer(size_t size) {
	int **buffer = new int*[size];
	for (int i = 0; i < size; ++i) {
		buffer[i] = new int(i);
	}
	// prevent sequential pattern of hashes
	std::random_shuffle(buffer, buffer + size); 
	return (void**)buffer;
}

void deallocateTestBuffer(void **b, size_t size) {
	int **buffer = (int**)b;
	for (int i = 0; i < size; ++i) {
		delete buffer[i];
	}
	delete[] buffer;
}

#define IDX(rev, i, sz) ((rev) ? (sz - 1 - i) : (i))

void mapWriter(AbstractMapProvider &map, void **keys, void **values, std::atomic<void*> *result, size_t size, bool reverse=false) {
	map::GC::EpochType id = GCHelper::attach();
	for (int i = 0; i < size; i++) {
		map.put(keys[IDX(reverse, i, size)], values[IDX(reverse, i, size)]);
	}
	GCHelper::safepoint(id);
	GCHelper::detach(id);
}

void mapReader(AbstractMapProvider &map, void **keys, void **values, std::atomic<void*> *result, size_t size, bool reverse=false) {
	map::GC::EpochType id = GCHelper::attach();
	for (int i = 0; i < size; i++) {
		void *res = map.get(keys[IDX(reverse, i, size)]);
		result[IDX(reverse, i, size)].store(res, std::memory_order_relaxed);
	}
	GCHelper::safepoint(id);
	GCHelper::detach(id);
}

void mapRemover(AbstractMapProvider &map, void **keys, void **values, std::atomic<void*> *result, size_t size, bool reverse=false) {
	map::GC::EpochType id = GCHelper::attach();
	for (int i = 0; i < size; i++) {
		void *res = map.remove(keys[IDX(reverse, i, size)]);
		result[IDX(reverse, i, size)].store(res, std::memory_order_relaxed);
	}
	GCHelper::safepoint(id);
	GCHelper::detach(id);
}

template <bool overlap=false, bool altDirection=false>
void doParallel(int threads, MapFunction func, AbstractMapProvider &map, void **keys, void **values, std::atomic<void*> *result, size_t size) {
	int delta = overlap ? size : size / threads; 
	int index = 0;
	std::vector<std::thread> tvec(threads);
	int reverse = false;
	for (int i = 0; i < threads; ++i, index += (overlap ? 0 : delta)) {
		tvec[i] = std::thread(func, std::ref(map), keys + index, values + index, result + index, delta, reverse);
		reverse = altDirection ? !reverse : reverse;
	}
	for (int i = 0; i < threads; ++i) {
		tvec[i].join();
	}
}

#define CONCURRENT_PROLOGUE_RESIZE(T, power, bsz)      \
	T map(1 << (power));                               \
	int bSize = 1 << (bsz);                            \
	void **keys = allocateTestBuffer(bSize);           \
	void **values = allocateTestBuffer(bSize);         \
	void **nullarr = new void*[bSize];			       \
	for (int i = 0; i < bSize; i++) nullarr[i] = NULL; \
	std::atomic<void*> *results = new std::atomic<void*>[bSize]

#define CONCURRENT_PROLOGUE(T, power)                  \
	CONCURRENT_PROLOGUE_RESIZE(T, power, power-1)

#define CONCURRENT_EPILOGUE()                      \
	delete[] results;                              \
	delete[] nullarr;                              \
	deallocateTestBuffer(keys, bSize);             \
	deallocateTestBuffer(values, bSize);           \

#define REPEAT_NEW_MAP(times)                      \
	for (int testnr = 0; testnr < times; ++testnr)

#define REPEAT_OLD_MAP(times)                      \
	for (int loopnr = 0; loopnr < times; ++loopnr)

TEST(LockFreeHashMapSequentialSpec) {
#ifdef DEBUG_MAP_GC
	int prevScheduled = DefaultGC.statsScheduled;
#endif
	CONCURRENT_PROLOGUE_RESIZE(TestLockFreeMap, 1, 15);
	
	mapReader(map, keys, values, results, 1);
	CHECK_ARRAY_EQUAL(nullarr, results, 1);
	mapRemover(map, keys, values, results, 1);
	CHECK_ARRAY_EQUAL(nullarr, results, 1);
	CHECK_EQUAL(0, map.unsafeUsed());
	CHECK_EQUAL(0, map.unsafeDirty());

	mapWriter(map, keys, values, results, bSize);
	CHECK_EQUAL(1 << 16, map.capacity());
	CHECK_EQUAL(bSize, map.unsafeUsed());
	CHECK_EQUAL(bSize, map.unsafeDirty());
#ifdef DEBUG_MAP_GC
	CHECK(DefaultGC.statsScheduled > prevScheduled);
	prevScheduled = DefaultGC.statsScheduled;
	CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif
	mapReader(map, keys, values, results, bSize);
	CHECK_ARRAY_EQUAL(values, results, bSize);

	mapRemover(map, keys, values, results, bSize);
	CHECK_ARRAY_EQUAL(values, results, bSize);
	CHECK_EQUAL(0, map.unsafeUsed());
	CHECK_EQUAL(bSize, map.unsafeDirty());
#ifdef DEBUG_MAP_GC
	CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif

	mapReader(map, keys, values, results, bSize);
	CHECK_ARRAY_EQUAL(nullarr, results, bSize);

	// minimize the space
	int halfSize = (1 << 14);
	void **keys1 = allocateTestBuffer(halfSize);
	void **values1 = allocateTestBuffer(halfSize);

	mapWriter(map, keys1, values1, results, halfSize);
	CHECK_EQUAL(halfSize, map.unsafeUsed());
	CHECK_EQUAL(bSize + halfSize, map.unsafeDirty());
#ifdef DEBUG_MAP_GC
	CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif

	mapRemover(map, keys1, values1, results, halfSize); // 75% is allocated
	CHECK_EQUAL(0, map.unsafeUsed());
	CHECK_EQUAL(bSize + halfSize, map.unsafeDirty());

	int *key2 = new int(1);
	int *val2 = new int(1);
	mapWriter(map, (void**)&key2, (void**)&val2, results, 1);
	CHECK_EQUAL(kSizeMin, map.capacity());
	CHECK_EQUAL(1, map.unsafeUsed());
	CHECK_EQUAL(1, map.unsafeDirty());
#ifdef DEBUG_MAP_GC
	CHECK(DefaultGC.statsScheduled > prevScheduled);
	prevScheduled = DefaultGC.statsScheduled;
	CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif

	mapReader(map, (void**)&key2, (void**)&val2, results, 1);
	CHECK_ARRAY_EQUAL((void**)&val2, results, 1);

	mapReader(map, keys, values, results, bSize);
	CHECK_ARRAY_EQUAL(nullarr, results, bSize);
	
	delete val2;
	delete key2;
	deallocateTestBuffer(keys1, halfSize);
	deallocateTestBuffer(values1, halfSize);
	CONCURRENT_EPILOGUE();
}

TEST(LockFreeHashMapBasicConcurrentChecks) {
	CONCURRENT_PROLOGUE(TestLockFreeMap, 4);

	void *res;

	map::GC::EpochType id = GCHelper::attach();

	mapReader(map, keys, values, results, 1);
	CHECK_ARRAY_EQUAL(nullarr, results, 1);

	std::thread writer(mapWriter, std::ref(map), keys, values, results, 1, false);
	while ( !(res = map.get(keys[0])) ) sched_yield();
	CHECK_EQUAL(values[0], res);
	writer.join();

	std::thread remover(mapRemover, std::ref(map), keys, values, results, 1, false);
	while ( (res = map.get(keys[0])) ) sched_yield();
	CHECK_EQUAL((void*)NULL, res);
	remover.join();

	GCHelper::detach(id);

	CHECK_EQUAL(0, map.unsafeUsed());
#ifdef DEBUG_MAP_GC
	CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif

	CONCURRENT_EPILOGUE();
}

TEST(LockFreeHashMapConcurrentIsolatedModifications) {
	REPEAT_NEW_MAP(5) {
		CONCURRENT_PROLOGUE_RESIZE(TestLockFreeMap, 4, 15);

		REPEAT_OLD_MAP(3) {
			// populate map in parallel
			doParallel(4, mapWriter, map, keys, values, results, bSize);
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_EQUAL(bSize, map.unsafeUsed());
			CHECK_ARRAY_EQUAL(values, results, bSize);
			
			// clean map in parallel
			doParallel(4, mapRemover, map, keys, values, results, bSize);
			CHECK_EQUAL(0, map.unsafeUsed());

#ifdef DEBUG_MAP_GC
			CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif
		}
	
		CONCURRENT_EPILOGUE();
	}
}

TEST(LockFreeHashMapConcurrentOverlappingModifications) {
	REPEAT_NEW_MAP(5) {
		CONCURRENT_PROLOGUE_RESIZE(TestLockFreeMap, 4, 15);

		REPEAT_OLD_MAP(3) {
			// 2 threads writing the same keys and same values
			// write direction of threads is opposite t1 -> <- t2
			doParallel<true, true>(2, mapWriter, map, keys, values, results, bSize);
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(values, results, bSize);
			CHECK_EQUAL(bSize, map.unsafeUsed());

			// 2 threads removing the same keys
			doParallel<true>(2, mapRemover, map, keys, values, results, bSize);
			for (int i = 0; i < bSize; ++i) {
				// each item was removed twice but results can be loaded in any order
				void* res = results[i].load(std::memory_order_relaxed);
				CHECK(res == NULL || res == values[i]);
			}

			CHECK_EQUAL(0, map.unsafeUsed());

#ifdef DEBUG_MAP_GC
			CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif
		}
	
		CONCURRENT_EPILOGUE();
	}
}

TEST(LockFreeHashMapConcurrentOverlappingUpdates) {
	REPEAT_NEW_MAP(5) {
		CONCURRENT_PROLOGUE_RESIZE(TestLockFreeMap, 4, 15);

		void **upd_values = allocateTestBuffer(bSize);

		REPEAT_OLD_MAP(3) {
			// 2 threads writing the same keys but different values
			// write direction of threads is opposite t1 -> <- t2
			std::thread writer1(mapWriter, std::ref(map), keys, values, results, bSize, false);
			std::thread writer2(mapWriter, std::ref(map), keys, upd_values, results, bSize, true);

			writer1.join();
			writer2.join();

			doParallel(4, mapReader, map, keys, values, results, bSize);
			for (int i = 0; i < bSize; i++) {
				void *check = results[i].load(std::memory_order_relaxed);
				CHECK(check == upd_values[i] || check == values[i]);
			}

			// clean map in parallel
			doParallel(2, mapRemover, map, keys, values, results, bSize);

			CHECK_EQUAL(0, map.unsafeUsed());

#ifdef DEBUG_MAP_GC
			CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif
		}
		
		deallocateTestBuffer(upd_values, bSize);
		CONCURRENT_EPILOGUE();
	}
}

TEST(LockFreeHashMapConcurrentMixedLoad) {
	REPEAT_NEW_MAP(5) {
		CONCURRENT_PROLOGUE_RESIZE(TestLockFreeMap, 1, 15);

		int jobSize = bSize >> 1;

		REPEAT_OLD_MAP(3) {
			void **writerKeys = keys + jobSize;
			void **writerValues = values + jobSize;
			void **removerKeys = keys;
			void **removerValues = values;

			doParallel(4, mapWriter, map, removerKeys, removerValues, results, jobSize); // populate 1 half of the map
			CHECK_EQUAL(jobSize, map.unsafeUsed());

			std::thread writer(mapWriter, std::ref(map), writerKeys, writerValues, results, jobSize, false);
			std::thread remover(mapRemover, std::ref(map), removerKeys, removerValues, results, jobSize, false);

			bool *conditions = new bool[bSize]();
			int conditionsMet = 0;

			map::GC::EpochType id = GCHelper::attach();
			
			while (conditionsMet < bSize) {
				for (int i = 0; i < jobSize; ++i) {
					if (!conditions[i]) {
						conditions[i] = (map.get(writerKeys[i]) == writerValues[i]);
						if (conditions[i]) conditionsMet++;
					}
				}
				for (int i = 0; i < jobSize; ++i) {
					if (!conditions[i + jobSize]) {
						conditions[i + jobSize] = (map.get(removerKeys[i]) == NULL);
						if (conditions[i + jobSize]) conditionsMet++;
					}
				}
				GCHelper::safepoint(id);
			}
			delete[] conditions;

			GCHelper::detach(id);

			writer.join();
			remover.join();

			// clean map in parallel
			doParallel(2, mapRemover, map, keys, values, results, bSize);
			CHECK_EQUAL(0, map.unsafeUsed());

			// check that map is indeed empty
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_EQUAL(0, map.unsafeUsed());

#ifdef DEBUG_MAP_GC
			CHECK_EQUAL(DefaultGC.statsScheduled, DefaultGC.statsRemoved);
#endif
		}
		CONCURRENT_EPILOGUE();
	}
}