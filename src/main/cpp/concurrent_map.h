#ifndef CONCURRENT_MAP_H
#define CONCURRENT_MAP_H

/**
 * Concurrent hash map implementation with lock-free readers that can be used
 * in signal handlers. Writers use locks only for memory allocation bounded
 * number of times during migration (resize) only. Map is ported from @preshing's
 * ConcurrentMap_Linear: https://github.com/preshing/junction/blob/master/junction/ConcurrentMap_Linear.h.
 */

#if __GNUC__ == 4 && __GNUC_MINOR__ < 6 && !defined(__APPLE__) && !defined(__FreeBSD__)
#	include <cstdatomic>
#else
#	include <atomic>
#endif

#include <vector>
#include <condition_variable>

#include "trace.h"

namespace map {

const int kTraceLFMapTotal = 27;
TRACE_DECLARE(LFMap, kTraceLFMapTotal);

// Configurable parameters
const int kNeighbourhood = 16;
const int kMaxJumpsAllowed = 8;
const int kMigrationChunkSize = 32;
const int kSizeMin = 32; // min size of hash map
const int kMaxSampleSize = 256;

typedef void* KeyType;
typedef int64_t HashType;
typedef void* ValueType;
typedef unsigned int DeltaType;

typedef HashType (*HashFunction)(KeyType);

const HashType MapHashEmpty = -1;
const ValueType MapValEmpty = nullptr;
const ValueType MapValMove = (ValueType)-1;
const DeltaType MapDeltaEmpty = 0;
const DeltaType MapDeltaExtend = -1;

static int nearestPow2(int x) {
	x--;
	x |= x >> 1;
	x |= x >> 2;
	x |= x >> 4;
	x |= x >> 8;
	x |= x >> 16;
	return x + 1;
}

class JobCoordinator {
public:
	struct Job {
		virtual void run() = 0;
		virtual ~Job() {}
	};

private:
	std::atomic<Job*> job;

public:
	JobCoordinator() : job(nullptr) {}

	~JobCoordinator() {
		end();
	}

	void set(Job *newJob) {
		job.store(newJob, std::memory_order_release);
	}

	Job* get() {
		return job.load(std::memory_order_consume);
	}

	void participate() {
		Job *prevJob = nullptr;
		while (true) {
			Job *cjob = job.load(std::memory_order_consume);
			if (cjob == prevJob) { // this job is done, wait for a new job
				while (true) {
					cjob = job.load(std::memory_order_consume);
					if (cjob != prevJob)
						break;
					sched_yield();
				}
			}
			if (cjob == (Job*)-1) return;
			cjob->run();
			prevJob = cjob;
		}
	}

	void end() {
		set((Job*)-1);
	}
};

struct HashTable {
	struct LockFreeMapEntry {
		std::atomic<HashType> hash;
		std::atomic<ValueType> value;
		std::atomic<DeltaType> deltaNext;

		LockFreeMapEntry() : hash(MapHashEmpty), value(MapValEmpty), deltaNext(MapDeltaEmpty) {}
		LockFreeMapEntry(HashType h, ValueType v) : hash(h), value(v), deltaNext(MapDeltaEmpty) {}
	};

	int sizeMask;
	LockFreeMapEntry *array;
	std::atomic_int freeBuckets;
	std::mutex mutex; // for allocation guard
	JobCoordinator coordinator; // migration coordinator
	std::atomic<JobCoordinator::Job*> victim; // comleted migration job

	HashTable(size_t initialSize) : victim(nullptr) {
		int allocatedSize = std::max(nearestPow2(initialSize), kSizeMin);
		freeBuckets.store((int)(0.75 * allocatedSize)); // resize when 75% of map is full
		sizeMask = allocatedSize - 1;
		array = new LockFreeMapEntry[allocatedSize];
		for (int i = 0; i < allocatedSize; i++) {
			array[i].hash = MapHashEmpty;
			array[i].value = MapValEmpty;
		}
	}

	~HashTable() {
		coordinator.end();
		JobCoordinator::Job *v = victim.load(std::memory_order_relaxed);
		if (v != nullptr) delete v;
		delete[] array;
	}

	int getMigrationSize() {
		return (sizeMask + 1) / kMigrationChunkSize; // both are powers of 2
	}

};

class GC {
private:
	std::mutex mutex;
	int totalThreads;
	std::atomic_int globalEpoch;
	std::atomic_int remaining;
	std::vector<HashTable*> recentGarbage;
	std::vector<HashTable*> oldGarbage;
#ifdef DEBUG_MAP_GC
public:
	int statsScheduled;
	int statsRemoved;
#endif

	void newEpoch() { // within mutex
#ifdef DEBUG_MAP_GC
		statsRemoved += oldGarbage.size();
#endif
		while (!oldGarbage.empty()) {
			delete oldGarbage.back();
			oldGarbage.pop_back();
		}
		globalEpoch.fetch_add(1, std::memory_order_relaxed);
		remaining.store(totalThreads, std::memory_order_relaxed);
		oldGarbage = std::move(recentGarbage);
		recentGarbage.clear();
	}

public:
	typedef unsigned int EpochType;
	static const EpochType kEpochInitial;

	GC() : totalThreads(0), globalEpoch(1), remaining(0) {}

	EpochType attachThread() {
		std::lock_guard<std::mutex> guard(mutex);
		totalThreads++;
		remaining.fetch_add(1, std::memory_order_relaxed);
		return globalEpoch.load(std::memory_order_relaxed) - 1; // force thread to move to global epoch at least once
	}

	void detachThread(EpochType &localEpoch) {
		std::lock_guard<std::mutex> guard(mutex);
		totalThreads--;
		int rem;
		if (localEpoch < globalEpoch) {
			rem = remaining.fetch_sub(1, std::memory_order_relaxed) - 1;
		} else {
			rem = remaining.load(std::memory_order_relaxed);
		}
		if (rem == 0) 
			newEpoch();
		localEpoch = kEpochInitial;
	}

	void safepoint(EpochType &localEpoch) {
		std::lock_guard<std::mutex> guard(mutex);
		int rem;

		if (localEpoch < globalEpoch) {
			localEpoch = globalEpoch;
			rem = remaining.fetch_sub(1, std::memory_order_relaxed) - 1;
		} else {
			rem = remaining.load(std::memory_order_relaxed);
		}
		if (rem == 0)
			newEpoch();
	}

	/* for reads within signal handler */
	void ss_safepoint(EpochType &localEpoch) {
		EpochType global = globalEpoch.load(std::memory_order_relaxed);
		if (localEpoch < global) { // no thread can move global epoch since remaining > 0
			remaining.fetch_sub(1, std::memory_order_relaxed);
			localEpoch = global;
		}
	}

	void scheduleDelete(HashTable *pointer) {
		std::lock_guard<std::mutex> guard(mutex);
#ifdef DEBUG_MAP_GC
		statsScheduled++;
#endif
		recentGarbage.push_back(pointer);
	}

	~GC() {
		std::move(recentGarbage.begin(), recentGarbage.end(), std::back_inserter(oldGarbage));
		recentGarbage.clear();
		newEpoch();
	}
};

extern GC DefaultGC;

enum InsertOutcome { INSERT_OK, INSERT_OVERFLOW, INSERT_HELP_MIGRATION };

class LockFreeMapPrimitives {
public:
	static HashTable::LockFreeMapEntry *find(HashTable *root, KeyType key, HashFunction hasher) {
		HashType tHash = hasher(key);
		int i = tHash & root->sizeMask;
		while (true) {
			HashTable::LockFreeMapEntry* entr = root->array + i;
			HashType bucketHash = entr->hash.load(std::memory_order_relaxed);

			if (bucketHash == MapHashEmpty) { // not found
				TRACE(LFMap, 0);
				return nullptr;
			} else if (bucketHash == tHash) {
				TRACE(LFMap, 1);
				return entr;
			}

			DeltaType delta = entr->deltaNext.load(std::memory_order_relaxed);

			if (delta == MapDeltaEmpty || delta == MapDeltaExtend) {
				// there's no next bucket or it's not ready yet, giving up
				return nullptr;
			}

			i = (i + delta) & root->sizeMask;
		}
	}

	static InsertOutcome insertOrUpdate(HashTable *root, KeyType key, ValueType value, HashFunction hasher, ValueType& oldValue) {
		HashType hash = hasher(key);
		return insertOrUpdate(root, hash, value, oldValue);
	}

	static InsertOutcome insertOrUpdate(HashTable *root, HashType tHash, ValueType value, ValueType& oldValue) {
		int i = tHash & root->sizeMask;
		int jumps = 0;
		DeltaType delta = 0;
		HashTable::LockFreeMapEntry *prev, *entr;

		while (true) {
			do {
				if (jumps++ > kMaxJumpsAllowed) {
					// too many jumps and hash collisions => resize
					TRACE(LFMap, 2);
					return INSERT_OVERFLOW;
				}
				i = (i + delta) & root->sizeMask;
				prev = root->array + i;
				HashType bucketHash = prev->hash.load(std::memory_order_relaxed);

				// all items in chain are already allocated
				if (bucketHash == tHash) { // allocated bucket found
					oldValue = prev->value.load(std::memory_order_relaxed);
					if (oldValue == MapValMove) {
						return INSERT_HELP_MIGRATION; // indicate overflow
					} else if (prev->value.compare_exchange_strong(oldValue, value, std::memory_order_acq_rel)) {
						TRACE(LFMap, 3);
					} else {
						TRACE(LFMap, 4);
					}

					// if there's a concurrent write or erase (i.e. CAS failed), giving up and pretending that value was overwritten
					return INSERT_OK;
				}

				delta = prev->deltaNext.load(std::memory_order_relaxed);
			} while (delta != MapDeltaEmpty && delta != MapDeltaExtend);

			delta = MapDeltaEmpty;
			if (prev->deltaNext.compare_exchange_strong(delta, MapDeltaExtend, std::memory_order_acq_rel))
				break;

			while (prev->deltaNext.load(std::memory_order_relaxed) == MapDeltaExtend) sched_yield(); // wait until parent is extended
			delta = 0;
		}

		// linear search
		for (int d = 0; d < kNeighbourhood; d++) {
			entr = root->array + ((i + d) & root->sizeMask);
			HashType bucketHash = entr->hash.load(std::memory_order_relaxed);

			if (bucketHash == MapHashEmpty) { // unallocated bucket
				int cellsBeforeInsert = root->freeBuckets.fetch_sub(1, std::memory_order_relaxed);

				if (cellsBeforeInsert <= 0) {
					TRACE(LFMap, 5);
					root->freeBuckets.fetch_add(1, std::memory_order_relaxed);

					// reset prev state
					prev->deltaNext.store(MapDeltaEmpty, std::memory_order_release);
					return INSERT_OVERFLOW; // indicate overflow
				}

				if (entr->hash.compare_exchange_strong(bucketHash, tHash, std::memory_order_relaxed)) {
					TRACE(LFMap, 6);
					bucketHash = tHash;
					prev->deltaNext.store(d > 0 ? d : MapDeltaEmpty, std::memory_order_release);
					bucketHash = tHash;
				} else {
					TRACE(LFMap, 7);
					root->freeBuckets.fetch_add(1, std::memory_order_relaxed);
				}
			}

			if (bucketHash == tHash) {
				// by chance bucket was allocated but unnoticed or allocated by code above
				oldValue = entr->value.load(std::memory_order_relaxed); // we are only interested in address
				if (oldValue == MapValMove) {
					return INSERT_HELP_MIGRATION; // indicate overflow
				} else if (entr->value.compare_exchange_strong(oldValue, value, std::memory_order_acq_rel)) {
					TRACE(LFMap, 8);
				} else {
					TRACE(LFMap, 9);
					if (oldValue == MapValMove)
						return INSERT_HELP_MIGRATION;
				}

				// if there's a concurrent write or erase (i.e. CAS failed), giving up and pretending that value was overwritten
				return INSERT_OK;
			}
		}

		TRACE(LFMap, 10);
		prev->deltaNext.store(MapDeltaEmpty, std::memory_order_release);
		return INSERT_OVERFLOW; // no free space in neighbourhood
	}
};

template <typename Map>
struct Migration : public JobCoordinator::Job {
	struct Source {
		HashTable *table;
		std::atomic_int index;
	};

	typedef typename std::vector<Source> TablesVec;
	typedef typename TablesVec::iterator TablesIterator;

	Map &map;
	HashTable *dest;
	TablesVec sources;
	std::atomic<bool> overflowed;
	std::atomic<int> state; // odd means migration completed
	std::atomic<int> unitsRemaining;
	Migration<Map> *prev;

	Migration(Map &self, int numSources) : map(self), sources(numSources),
		overflowed(false), state(0), unitsRemaining(0), prev(nullptr) {}

	virtual ~Migration() {
		if (prev != nullptr) delete prev;
		TablesIterator it = sources.begin();
		for (it++; it != sources.end(); it++) {
			if (it->table) delete it->table;
		} // skip 1st
	}

	virtual void run() {
		if (state.load(std::memory_order_relaxed) & 1) {
			TRACE(LFMap, 16);
			return; // work is done, new table not published yet
		}
		state.fetch_add(2, std::memory_order_relaxed);

		for (TablesIterator it = sources.begin(); it != sources.end(); it++) {
			HashTable *table = it->table;

			while (true) {
				if (state.load(std::memory_order_relaxed) & 1) {
					TRACE(LFMap, 17);
					goto end_migration;
				}

				int index = it->index.fetch_add(kMigrationChunkSize, std::memory_order_relaxed);
				if (index > table->sizeMask) break; // migrate next source

				bool rangeOverflow = migrateRange(table, index);
				if (rangeOverflow) {
					TRACE(LFMap, 18);
					overflowed.store(true, std::memory_order_relaxed);
					state.fetch_or(1, std::memory_order_relaxed);
					goto end_migration;
				}

				int sizeToMigrate = unitsRemaining.fetch_sub(1, std::memory_order_relaxed);
				if (sizeToMigrate == 1) { // successful data migration
					state.fetch_or(1, std::memory_order_relaxed);
					goto end_migration;
				}
			}
		}
		TRACE(LFMap, 19);

end_migration:

		int stateProbe = state.fetch_sub(2, std::memory_order_acq_rel); // see all changes

		if (stateProbe > 3) {
			TRACE(LFMap, 20);
			return; // not the last one
		}

		bool overflow = overflowed.load(std::memory_order_relaxed);

		if (!overflow) {
			sources[0].table->victim.store(this, std::memory_order_release);
			sources[0].table->coordinator.end();
			map.finishMigration(dest);
		} else {
			HashTable *origTable = sources[0].table;
			std::lock_guard<std::mutex> guard(origTable->mutex);

			JobCoordinator::Job *startedMigration = origTable->coordinator.get();

			if (startedMigration == this) { // make sure no new migrations started
				Migration *m = new Migration(map, sources.size() + 1);
				m->dest = new HashTable((dest->sizeMask + 1) << 1);
				int unitsRemaining = 0, i = 0;
				for (TablesIterator it = sources.begin(); it != sources.end(); it++, i++) {
					unitsRemaining += it->table->getMigrationSize();
					m->sources[i].table = it->table;
					m->sources[i].index.store(0, std::memory_order_relaxed);
					it->table = nullptr;
				}

				m->sources[sources.size()].table = dest;
				m->sources[sources.size()].index.store(0, std::memory_order_relaxed);
				m->unitsRemaining = unitsRemaining + dest->getMigrationSize();
				m->prev = this;

				origTable->coordinator.set(m);
			} else {
				TRACE(LFMap, 21);
			}
		}
	}

	bool migrateRange(HashTable *from, int startIndex) {
		int last = std::min(startIndex + kMigrationChunkSize, from->sizeMask + 1);

		for (int index = startIndex; index < last; index++) {
			HashTable::LockFreeMapEntry *entry = &from->array[index];
			while (true) {
				HashType srcHash = entry->hash.load(std::memory_order_relaxed);
				ValueType srcValue;

				if (srcHash == MapHashEmpty) { // unused cell
					srcValue = MapValEmpty; // expect null for unused cell value
					if (entry->value.compare_exchange_strong(srcValue, MapValMove, std::memory_order_relaxed)) {
						break; // nothing to move to new table
					} else if (srcValue == MapValMove) {
						TRACE(LFMap, 22);
						break; // found previous unfinished migration
					}
					TRACE(LFMap, 23);
					// someone placed value to the cell, reread
				} else { // used cell: deleted or not
					srcValue = entry->value.load(std::memory_order_relaxed); // we only need a pointer
					if (srcValue == MapValEmpty) { // deleted cell
						if (entry->value.compare_exchange_strong(srcValue, MapValMove, std::memory_order_relaxed)) {
							break; // nothing to move to new table
						}
						TRACE(LFMap, 24);
						// someone placed value to the cell, evacuation required
					} else if (srcValue == MapValMove) {
						TRACE(LFMap, 25);
						break; // found previous unfinished migration
					}

					while (!entry->value.compare_exchange_strong(srcValue, MapValMove, std::memory_order_relaxed)) sched_yield();

					// only 1 thread can migrate bucket at time, so srcValue != MapValMove and srcHash is not in dest
					if (srcValue != MapValEmpty) {
						ValueType oldValue;
						InsertOutcome res = LockFreeMapPrimitives::insertOrUpdate(dest, srcHash, srcValue, oldValue);
						if (res == INSERT_OVERFLOW) {
							entry->value.store(srcValue, std::memory_order_release); // return replaced value
							return true; // overflow
						}
					} else {
						TRACE(LFMap, 26);
					}

					break; // next element
				}
			}
		}
		return false;
	}
};

class AbstractMapProvider {
public:
	virtual ValueType put(KeyType key, ValueType value) = 0;
	virtual ValueType get(KeyType key) = 0;
	virtual ValueType remove(KeyType key) = 0;
	virtual ~AbstractMapProvider() {}
};

/* Hasher assumed to be collision free. */
template <typename Hasher, bool signalSafeReaders>
class ConcurrentMapProvider : public AbstractMapProvider {
private:
	std::atomic<HashTable*> current;
	HashFunction hasher;

	void migrationStart(HashTable *table, bool doubleSize) {
		int newSize;
		if (doubleSize) {
			newSize = ((table->sizeMask + 1) << 1);
		} else {
			int estimatedSize = 0;
			int sampleMax = std::min(kMaxSampleSize, table->sizeMask + 1);
			for (int i = 0; i < sampleMax; i++) {
				// we just need a pointer, not content it is pointing to
				ValueType value = table->array[i].value.load(std::memory_order_relaxed);

				if (value == MapValMove) {
					// someone has already strated the migration, join it
					TRACE(LFMap, 14);
					return;
				} else if (value != MapValEmpty) {
					estimatedSize++;
				}
			}
			int sizeApprox = (table->sizeMask + 1) * float(estimatedSize) / sampleMax;
			newSize = std::max(kSizeMin, nearestPow2(2 * sizeApprox));
		}
		migrationStart(table, newSize);
	}

	void migrationStart(HashTable *table, int targetSize) {
		std::lock_guard<std::mutex> guard(table->mutex);

		JobCoordinator::Job *migration = table->coordinator.get();
		if (migration) {
			// no need to create second migration job
			TRACE(LFMap, 15);
			return;
		}

		auto *m = new Migration<ConcurrentMapProvider<Hasher, signalSafeReaders> >(*this, 1);
		m->dest = new HashTable(targetSize);
		m->sources[0].table = table;
		m->sources[0].index.store(0, std::memory_order_relaxed);
		m->unitsRemaining = table->getMigrationSize();

		table->coordinator.set(m);
	}

public:
	ConcurrentMapProvider(size_t initialSize) : hasher(&Hasher::hash) {
		HashTable *initial = new HashTable(initialSize);
		current.store(initial, std::memory_order_relaxed);
	}

	virtual ~ConcurrentMapProvider() {
		delete current.load(std::memory_order_consume);
	}

	ValueType put(KeyType key, ValueType value) {
		bool doubleSize = false;
		while (true) {
			HashTable *root = current.load(std::memory_order_consume);
			ValueType oldValue;
			InsertOutcome res = LockFreeMapPrimitives::insertOrUpdate(root, key, value, hasher, oldValue);

			if (res == INSERT_OK) {
				return oldValue;
			} else if (res == INSERT_OVERFLOW) {
				migrationStart(root, doubleSize);
			}

			// help migration
			root->coordinator.participate();

			// double the size for the next overflow in a row
			doubleSize = true;
		}
	}

	// this is the only function that can be called from signal handler
	ValueType get(KeyType key) {
		while (true) {
			HashTable *root = current.load(std::memory_order_consume);

			HashTable::LockFreeMapEntry *el = LockFreeMapPrimitives::find(root, key, hasher);
			ValueType res = el ? el->value.load(std::memory_order_consume) : MapValEmpty;

			if (res != MapValMove)
				return res;

			if (!signalSafeReaders)
				root->coordinator.participate();
		}
	}

	ValueType remove(KeyType key) {
		while (true) {
			HashTable *root = current.load(std::memory_order_consume);

			HashTable::LockFreeMapEntry *entr = LockFreeMapPrimitives::find(root, key, hasher);
			if (entr == nullptr) { // not found
				TRACE(LFMap, 11);
				return MapValEmpty;
			}

			ValueType oldValue = entr->value.load(std::memory_order_relaxed);
			if (oldValue == MapValMove) {
				root->coordinator.participate();
			} else if (entr->value.compare_exchange_strong(oldValue, MapValEmpty, std::memory_order_consume)) {
				TRACE(LFMap, 12);
				return oldValue;
			} else { // CAS failed
				// there's a concurrent write or erase, giving up and pretending that value was overwritten
				TRACE(LFMap, 13);
				return MapValEmpty;
			}
		}
	}

	// Migration callback, called once per successful migration
	void finishMigration(HashTable *newRoot) {
		HashTable *oldRoot = current.load(std::memory_order_consume);
		current.store(newRoot, std::memory_order_release);
		DefaultGC.scheduleDelete(oldRoot);
	}

	int capacity() {
		HashTable *root = current.load(std::memory_order_consume);
		return root->sizeMask + 1;
	}

	int unsafeUsed() {
		int size = 0;
		HashTable *root = current.load(std::memory_order_consume);
		for (int i = 0; i < root->sizeMask + 1; i++) {
			ValueType value = root->array[i].value.load(std::memory_order_relaxed);
			if (value != MapValEmpty && value != MapValMove)
				size++;
		}
		return size;
	}

	int unsafeDirty() {
		int size = 0;
		HashTable *root = current.load(std::memory_order_consume);
		for (int i = 0; i < root->sizeMask + 1; i++) {
			if (root->array[i].hash.load(std::memory_order_relaxed) != MapHashEmpty)
				size++;
		}
		return size;
	}
};

} // namespace map end

#endif
