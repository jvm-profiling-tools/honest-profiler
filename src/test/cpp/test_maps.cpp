#include "test.h"
#include "../../main/cpp/thread_map.h"

#include <sstream>
#include <algorithm>
#include <thread>
#include <vector>

typedef LockFreeMapProvider<PointerHasher<int> > TestLockFreeMap;

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

template <typename T, bool reverse=false>
void mapWriter(T &map, void **keys, void **values, size_t size) {
	for (int i = 0; i < size; i++) {
		map.put(keys[IDX(reverse, i, size)], values[IDX(reverse, i, size)]);
	}
}

template <typename T, bool reverse=false>
void mapReader(T &map, void **keys, void **result, size_t size) {
	for (int i = 0; i < size; i++) {
		result[IDX(reverse, i, size)] = map.get(keys[IDX(reverse, i, size)]);
	}
}

template <typename T, bool reverse=false>
void mapRemover(T &map, void **keys, void **result, size_t size) {
	for (int i = 0; i < size; i++) {
		result[IDX(reverse, i, size)] = map.remove(keys[IDX(reverse, i, size)]);
	}
}

template <typename T>
void singleThreadedTest() {
	T map(16);

	int bSize = 10; // < map size
	void **keys = allocateTestBuffer(bSize);
	void **values = allocateTestBuffer(bSize);
	void **results = new void*[bSize];
	
	CHECK_EQUAL(0, map.size());
	
	mapReader(map, keys, results, 1);
	CHECK_EQUAL((void*)NULL, results[0]);
	
	mapRemover(map, keys, results, 1);
	CHECK_EQUAL((void*)NULL, results[0]);

	mapWriter(map, keys, values, bSize);
	CHECK_EQUAL(bSize, map.size());

	mapReader(map, keys, results, bSize);
	CHECK_ARRAY_EQUAL(values, results, bSize);

	mapRemover(map, keys, results, bSize);
	CHECK_ARRAY_EQUAL(values, results, bSize);

	mapReader(map, keys, results, 1);
	CHECK_EQUAL((void*)NULL, results[0]);

	delete[] results;
	deallocateTestBuffer(keys, bSize);
	deallocateTestBuffer(values, bSize);
}


#define CONCURRENT_PROLOGUE(power)                 \
	T map(1 << (power));                           \
	int bSize = 1 << (power - 1);                  \
	void **keys = allocateTestBuffer(bSize);       \
	void **values = allocateTestBuffer(bSize);     \
	void **results = new void*[bSize]

#define CONCURRENT_EPILOGUE()                      \
	delete[] results;                              \
	deallocateTestBuffer(keys, bSize);             \
	deallocateTestBuffer(values, bSize);           \


template <typename T = TestLockFreeMap>
void concReadSeesInsert() {
	CONCURRENT_PROLOGUE(1);

	std::thread simpleWriter(mapWriter<T>, std::ref(map), keys, values, 1);

	void *res;
	while ( !(res = map.get(keys[0])) ); // spin until get() succeeds
	CHECK_EQUAL(values[0], res);
	
	simpleWriter.join();
	CONCURRENT_EPILOGUE();
}

template <typename T = TestLockFreeMap>
void concReadSeesDelete() {
	CONCURRENT_PROLOGUE(1);

	mapWriter(map, keys, values, 1);
	CHECK_EQUAL(values[0], map.get(keys[0]));

	std::thread simpleRemover(mapRemover<T>, std::ref(map), keys, results, 1);

	void *res;
	while ( (res = map.get(keys[0])) ); // spin until get() returns NULL
	CHECK_EQUAL((void*)NULL, res);

	simpleRemover.join();
	CONCURRENT_EPILOGUE();
}

template <typename T = TestLockFreeMap>
void concInsertsSeparate() {
	CONCURRENT_PROLOGUE(15);

	const int threadsSize = 4;
	int delta = bSize / threadsSize; 
	int index = 0;

	for (int iteration = 0; iteration < 10; ++iteration, index = 0) {
		std::vector<std::thread> threads(threadsSize);

		for (int i = 0; i < threadsSize; ++i, index += delta) {
			threads[i] = std::thread(mapWriter<T>, std::ref(map), keys + index, values + index, delta);
		}

		for (int i = 0; i < threadsSize; ++i) {
			threads[i].join();
		}

		mapRemover(map, keys, results, bSize);
		CHECK_ARRAY_EQUAL(values, results, index);
		for (int i = index; i < bSize; ++i) {
			CHECK_EQUAL((void*)NULL, results[i]);
		}
	}
	
	CONCURRENT_EPILOGUE();
}

template <typename T = TestLockFreeMap>
void concInsertsOverlapping() {
	CONCURRENT_PROLOGUE(15);

	const int threadsSize = 2;

	for (int iteration = 0; iteration < 10; ++iteration) {
		std::vector<std::thread> threads(threadsSize);

		for (int i = 0; i < threadsSize; ++i) {
			threads[i] = std::thread(mapWriter<T>, std::ref(map), keys, values, bSize);
		}

		for (int i = 0; i < threadsSize; ++i) {
			threads[i].join();
		}

		mapRemover(map, keys, results, bSize);
		CHECK_ARRAY_EQUAL(values, results, bSize);
	}
	
	CONCURRENT_EPILOGUE();
}

template <typename T = TestLockFreeMap>
void concInsertsSameKeys() {
	CONCURRENT_PROLOGUE(15);
	void **upd_values = allocateTestBuffer(bSize);

	for (int iteration = 0; iteration < 10; ++iteration) {
		std::thread writer1(mapWriter<T>, std::ref(map), keys, values, bSize);
		std::thread writer2(mapWriter<T, true>, std::ref(map), keys, upd_values, bSize);

		writer1.join();
		writer2.join();

		mapRemover(map, keys, results, bSize);
		for (int i = 0; i < bSize; ++i) {
			CHECK(results[i] == values[i] || results[i] == upd_values[i]);
		}
	}
	
	deallocateTestBuffer(upd_values, bSize);
	CONCURRENT_EPILOGUE();
}

#ifdef CONCURRENT_MAP_TBB

TEST(TbbHashMapSequentialSpec) {
	/* make sure TBB map works as expected in single threaded case */
	singleThreadedTest<TbbMapProvider>();
}

TEST(TbbConcurrentReadSeesInsert) {
	concReadSeesInsert<TbbMapProvider>();
}

TEST(TbbConcurrentReadSeesRemove) {
	concReadSeesDelete<TbbMapProvider>();
}

TEST(TbbConcurrentInserts) {
	concInsertsSeparate<TbbMapProvider>();
}

TEST(TbbConcurrentOverlappingInserts) {
	concInsertsOverlapping<TbbMapProvider>();
}

TEST(TbbConcurrentInsertSameKeys) {
	concInsertsSameKeys<TbbMapProvider>();
}

#endif

TEST(LockFreeHashMapSequentialSpec) {
	singleThreadedTest<TestLockFreeMap>();
}

TEST(LockFreeHashMapConcurrentReadSeesInsert) {
	concReadSeesInsert<TestLockFreeMap>();
}

TEST(LockFreeHashMapConcurrentReadSeesRemove) {
	concReadSeesDelete<TestLockFreeMap>();
}

TEST(LockFreeHashMapConcurrentInserts) {
	concInsertsSeparate<TestLockFreeMap>();
}

TEST(LockFreeHashMapConcurrentOverlappingInserts) {
	concInsertsOverlapping<TestLockFreeMap>();
}

TEST(LockFreeHashMapConcurrentInsertSameKeys) {
	concInsertsSameKeys<TestLockFreeMap>();
}