#include "test.h"
#include "../../main/cpp/thread_map.h"

#include <sstream>
#include <algorithm>
#include <thread>
#include <vector>

typedef LockFreeMapProvider<PointerHasher<int> > TestLockFreeMap;
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
	for (int i = 0; i < size; i++) {
		map.put(keys[IDX(reverse, i, size)], values[IDX(reverse, i, size)]);
	}
}

void mapReader(AbstractMapProvider &map, void **keys, void **values, std::atomic<void*> *result, size_t size, bool reverse=false) {
	for (int i = 0; i < size; i++) {
		void *res = map.get(keys[IDX(reverse, i, size)]);
		result[IDX(reverse, i, size)].store(res, std::memory_order_relaxed);
	}
}

void mapRemover(AbstractMapProvider &map, void **keys, void **values, std::atomic<void*> *result, size_t size, bool reverse=false) {
	for (int i = 0; i < size; i++) {
		void *res = map.remove(keys[IDX(reverse, i, size)]);
		result[IDX(reverse, i, size)].store(res, std::memory_order_relaxed);
	}
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

#define CONCURRENT_PROLOGUE(T, power)                  \
	T map(1 << (power));                               \
	int bSize = 1 << (power - 1);                      \
	void **keys = allocateTestBuffer(bSize);           \
	void **values = allocateTestBuffer(bSize);         \
	void **nullarr = new void*[bSize];			       \
	for (int i = 0; i < bSize; i++) nullarr[i] = NULL; \
	std::atomic<void*> *results = new std::atomic<void*>[bSize]

#define CONCURRENT_EPILOGUE()                      \
	delete[] results;                              \
	delete[] nullarr;                              \
	deallocateTestBuffer(keys, bSize);             \
	deallocateTestBuffer(values, bSize);           \

#define REPEAT_NEW_MAP(times)                      \
	for (int testnr = 0; testnr < times; ++testnr)

#define REPEAT_OLD_MAP(times)                      \
	for (int loopnr = 0; loopnr < times; ++loopnr)

template <typename T>
void singleThreadedTest() {
	CONCURRENT_PROLOGUE(T, 4);
	
	CHECK_EQUAL(0, map.size());
	
	mapReader(map, keys, values, results, 1);
	CHECK_EQUAL((void*)NULL, results[0]);
	
	mapRemover(map, keys, values, results, 1);
	CHECK_EQUAL((void*)NULL, results[0]);

	mapWriter(map, keys, values, results, bSize);
	CHECK_EQUAL(bSize, map.size());

	mapReader(map, keys, values, results, bSize);
	CHECK_ARRAY_EQUAL(values, results, bSize);

	mapRemover(map, keys, values, results, bSize);
	CHECK_ARRAY_EQUAL(values, results, bSize);

	mapReader(map, keys, values, results, 1);
	CHECK_EQUAL((void*)NULL, results[0]);

	CONCURRENT_EPILOGUE();
}

// TODO: make this run
void concMixedTestCase() {
	REPEAT_NEW_MAP(5) {
		CONCURRENT_PROLOGUE(TestLockFreeMap, 15);
		int jobSize = bSize >> 1;

		REPEAT_OLD_MAP(3) {
			void **writerKeys = keys + jobSize;
			void **writerValues = values + jobSize;
			void **removerKeys = keys;
			void **removerValues = values;

			doParallel(4, mapWriter, map, keys, values, results, jobSize);
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(values, results, jobSize);
			CHECK_ARRAY_EQUAL(nullarr, results + jobSize, jobSize);

			std::thread writer(mapWriter, std::ref(map), writerKeys, writerValues, results, jobSize, false);
			std::thread remover(mapRemover, std::ref(map), removerKeys, removerValues, results, jobSize, false);

			bool *conditions = new bool[bSize];
			int conditionsMet = 0;
			
			while (conditionsMet != bSize) {
				for (int i = 0; i < jobSize; ++i) {
					if (!conditions[i]) {
						conditions[i] = (map.get(writerKeys[i]) == writerValues[i]);
						conditionsMet += (conditions[i] ? 1 : 0);
					}
				}
				for (int i = 0; i < jobSize; ++i) {
					if (!conditions[i + jobSize]) {
						conditions[i + jobSize] = (map.get(removerKeys[i]) == NULL);
						conditionsMet += (conditions[i + jobSize] ? 1 : 0);
					}
				}
			}
			delete[] conditions;

			writer.join();
			remover.join();

			// clean map in parallel
			doParallel(2, mapRemover, map, keys, values, results, bSize);

			// check that map is indeed empty
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(nullarr, results, bSize);
		}
		CONCURRENT_EPILOGUE();
	}
}

#ifdef CONCURRENT_MAP_TBB

TEST(TbbHashMapSequentialSpec) {
	singleThreadedTest<TbbMapProvider>();
}

#endif

TEST(LockFreeHashMapSequentialSpec) {
	singleThreadedTest<TestLockFreeMap>();
}

TEST(LockFreeHashMapBasicConcurrentChecks) {
	CONCURRENT_PROLOGUE(TestLockFreeMap, 4);

	void *res;

	mapReader(map, keys, values, results, 1);
	CHECK_EQUAL((void*)NULL, results[0]);

	std::thread writer(mapWriter, std::ref(map), keys, values, results, 1, false);
	while ( !(res = map.get(keys[0])) );
	CHECK_EQUAL(values[0], res);

	std::thread remover(mapRemover, std::ref(map), keys, values, results, 1, false);
	while ( (res = map.get(keys[0])) );
	CHECK_EQUAL(values[0], results[0]);

	writer.join();
	remover.join();

	CONCURRENT_EPILOGUE();
}

TEST(LockFreeHashMapConcurrentIsolatedModifications) {
	REPEAT_NEW_MAP(5) {
		CONCURRENT_PROLOGUE(TestLockFreeMap, 15);

		REPEAT_OLD_MAP(3) {
			// populate map in parallel
			doParallel(1, mapWriter, map, keys, values, results, bSize);
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(values, results, bSize);

			// clean map in parallel
			doParallel(4, mapRemover, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(values, results, bSize);
			
			// check that map is indeed empty
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(nullarr, results, bSize);
		}
	
		CONCURRENT_EPILOGUE();
	}
}

TEST(LockFreeHashMapConcurrentOverlappingModifications) {
	REPEAT_NEW_MAP(5) {
		CONCURRENT_PROLOGUE(TestLockFreeMap, 15);

		REPEAT_OLD_MAP(3) {
			// 2 threads writing the same keys and same values
			// write direction of threads is opposite t1 -> <- t2
			doParallel<true, true>(2, mapWriter, map, keys, values, results, bSize);
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(values, results, bSize);

			// 2 threads removing the same keys
			doParallel<true>(2, mapRemover, map, keys, values, results, bSize);
			for (int i = 0; i < bSize; ++i) {
				// each item was removed twice but results can be loaded in any order
				void* res = results[i].load(std::memory_order_relaxed);
				CHECK(res == NULL || res == values[i]);
			}

			// check that map is indeed empty
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(nullarr, results, bSize);
		}
	
		CONCURRENT_EPILOGUE();
	}
}

TEST(LockFreeHashMapConcurrentOverlappingUpdates) {
	REPEAT_NEW_MAP(5) {
		CONCURRENT_PROLOGUE(TestLockFreeMap, 15);
		void **upd_values = allocateTestBuffer(bSize);

		REPEAT_OLD_MAP(3) {
			// 2 threads writing the same keys but different values
			// write direction of threads is opposite t1 -> <- t2
			std::thread writer1(mapWriter, std::ref(map), keys, values, results, bSize, false);
			std::thread writer2(mapWriter, std::ref(map), keys, upd_values, results, bSize, true);

			writer1.join();
			writer2.join();

			doParallel(4, mapReader, map, keys, values, results, bSize);
			for (int i = 0; i < bSize; i++)
				CHECK(results[i] == upd_values[i] || results[i] == values[i]);

			// clean map in parallel
			doParallel(2, mapRemover, map, keys, values, results, bSize);

			// check that map is indeed empty
			doParallel(4, mapReader, map, keys, values, results, bSize);
			CHECK_ARRAY_EQUAL(nullarr, results, bSize);
		}
		
		deallocateTestBuffer(upd_values, bSize);
		CONCURRENT_EPILOGUE();
	}
}

TEST(LockFreeHashMapConcurrentMixedLoad) {
	concMixedTestCase();
}
