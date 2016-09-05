#ifndef CONCURRENT_MAP_H
#define CONCURRENT_MAP_H

/**
 * Concurrent hash map implementation with lock-free readers that can be used
 * in signal handlers. Writers use locks only for memory allocation bounded 
 * number of times during migration (resize) only. Map is ported from @preshing's 
 * ConcurrentMap_Linear: https://github.com/preshing/junction/blob/master/junction/ConcurrentMap_Linear.h.
 */

#if __GNUC__ == 4 && __GNUC_MINOR__ < 6 && !defined(__APPLE__) && !defined(__FreeBSD__) 
	#include <cstdatomic>
#else
	#include <atomic>
#endif

#include <vector>
#include <condition_variable>

#include "trace.h"

#define MAP_HASH_NULL (int64_t)-1
#define MAP_VALUE_MIGRATION (void*)-1
#define PENDING_QUEUE_LENGTH 4
#define MAP_DELTA_UNDEF -1
#define MAP_DELTA_EXTEND -2
#define MAP_NEIGHBOURHOOD 16
#define MAP_MAX_JUMPS 4

const int kTraceLFMapTotal = 26;

TRACE_DECLARE(LFMap, kTraceLFMapTotal);

#define LFMAP_MIGRATION_CHUNK_SIZE 32
#define LFMAP_HASHTABLE_SIZE_MIN 32
#define LFMAP_MAX_SAMPLE_SIZE 256

typedef int64_t (*HashFunction)(void*);

namespace map {

static int nearestPow2(unsigned int x) {
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
	JobCoordinator() : job(NULL) {}

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
		Job *prevJob = NULL;
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

struct LockFreeMapEntry {
	std::atomic<int64_t> hash;
	std::atomic<void*> value;
	std::atomic<int> deltaNext;

	LockFreeMapEntry() : hash(MAP_HASH_NULL), value(NULL), deltaNext(MAP_DELTA_UNDEF) {}
	LockFreeMapEntry(int64_t h, void *v) : hash(h), value(v), deltaNext(MAP_DELTA_UNDEF) {}
};

struct HashTable {
	LockFreeMapEntry *array;
	std::atomic_int freeBuckets;
	int sizeMask;
	std::mutex mutex;
	JobCoordinator coordinator;
	std::atomic<JobCoordinator::Job*> victim;

	HashTable(size_t initialSize) : victim(NULL) {
		int allocatedSize = std::max(nearestPow2(initialSize), LFMAP_HASHTABLE_SIZE_MIN);
		freeBuckets.store((int)(0.75 * allocatedSize)); // resize when 75% of map is full
		sizeMask = allocatedSize - 1;
		array = new LockFreeMapEntry[allocatedSize];
		for (int i = 0; i < allocatedSize; i++) {
			array[i].hash = MAP_HASH_NULL;
			array[i].value = NULL;
		}
	}

	~HashTable() {
		coordinator.end();
		JobCoordinator::Job *v = victim.load(std::memory_order_relaxed);
		if (v != NULL)
			delete v;
		delete[] array;
	}

	int getAllocBuckets() {
		return sizeMask + 1 - freeBuckets.load(std::memory_order_relaxed);
	}

	int getMigrationSize() {
		return (sizeMask + 1) / LFMAP_MIGRATION_CHUNK_SIZE; // both are power of 2
	}

};

/**
 * Multi-reader, single writer fixed size array.
 */
class TableGuard {
private:
	struct Entry {
		std::atomic<HashTable*> table;
		std::atomic_int references;
	};

	Entry pending[PENDING_QUEUE_LENGTH];
	std::atomic_int current;

public:
	TableGuard(HashTable *initial) : current(0) {
		for (int i = 0; i < PENDING_QUEUE_LENGTH; i++) {
			pending[i].references.store(0, std::memory_order_relaxed);
			pending[i].table.store(NULL, std::memory_order_relaxed);
		}
		pending[0].references.store(1, std::memory_order_relaxed);
		pending[0].table.store(initial, std::memory_order_release);
	}

	HashTable *acquire() {
		TRACE(LFMap, 24);
		int index = current.load(std::memory_order_consume);
		pending[index].references.fetch_add(1, std::memory_order_relaxed);
		return pending[index].table.load(std::memory_order_acquire);
	}

	void release(HashTable *p) {
		TRACE(LFMap, 25);
		int startIndex = current.load(std::memory_order_consume);
		int endIndex = startIndex - PENDING_QUEUE_LENGTH;
		for (int index = startIndex; index > endIndex; index--) {
			index &= (PENDING_QUEUE_LENGTH - 1);
			HashTable *root = pending[index].table.load(std::memory_order_acquire);
			if (p == root) {
				pending[index].references.fetch_sub(1, std::memory_order_relaxed);
				return;
			}
		}
		TRACE(LFMap, 23);
	}

	void publish(HashTable *p) {
		int oldRoot = current.load(std::memory_order_relaxed);
		int index = oldRoot;
		while (true) {
			index = (index + 1) & (PENDING_QUEUE_LENGTH - 1);
			int refs = pending[index].references.load(std::memory_order_consume);
			if (refs < 1) {
				HashTable *root = pending[index].table.load(std::memory_order_consume);
				if (refs < 0) {
					std::cout << "#### [publish] references: " << refs << std::endl;
					TraceGroup_LFMap.dump();
				}
				if (pending[index].table.compare_exchange_strong(root, p, std::memory_order_acq_rel)) {
					if (root != NULL) delete root;
					pending[index].references.store(1, std::memory_order_relaxed); // global ref
					pending[oldRoot].references.fetch_sub(1, std::memory_order_relaxed); // remove global ref
					current.store(index, std::memory_order_release);
					return;
				}
			}
		}
	}

	~TableGuard() {
		for (int i = 0; i < PENDING_QUEUE_LENGTH; i++) {
			HashTable *t = pending[i].table.load(std::memory_order_consume);
			if (t != NULL) delete t;
		}
	}
};

struct ScopedGuard {
	HashTable *root;
	TableGuard &tg;

	ScopedGuard(TableGuard &guard) : tg(guard) {
		root = tg.acquire();
	}

	~ScopedGuard() {
		tg.release(root);
	}
};

enum InsertOutcome { INSERT_OK, INSERT_OVERFLOW, INSERT_HELP_MIGRATION };

class LockFreeMapPrimitives {
public:
	static LockFreeMapEntry *find(HashTable *root, void *key, HashFunction hasher) {
		int64_t tHash = hasher(key);
		int i = tHash;
		while (true) {
			i &= root->sizeMask;

			LockFreeMapEntry* entr = root->array + i;
			int64_t bucketHash = entr->hash.load(std::memory_order_relaxed);
	
			if (bucketHash == MAP_HASH_NULL) { // not found
				TRACE(LFMap, 0);
				return NULL;
			} else if (bucketHash == tHash) {
				TRACE(LFMap, 1);
				return entr;
			}

			int delta = entr->deltaNext.load(std::memory_order_relaxed);
			
			if (delta == MAP_DELTA_UNDEF || delta == MAP_DELTA_EXTEND) {
				// there's no next bucket or it's not ready yet, giving up
				return NULL;
			}

			i += delta;
		}
	}

	static InsertOutcome insertOrUpdate(HashTable *root, void *key, void *value, HashFunction hasher) {
		return insertOrUpdate(root, hasher(key), value);
	}

	static InsertOutcome insertOrUpdate(HashTable *root, int64_t tHash, void *value) {
		int i = tHash;
		int delta = 0, jumps = 0;
		LockFreeMapEntry *prev, *entr;

		while (true) {
			do {
				if (jumps > MAP_MAX_JUMPS) return INSERT_OVERFLOW;
				i = (i + delta) & root->sizeMask;
				prev = root->array + i;
				int64_t bucketHash = prev->hash.load(std::memory_order_relaxed);

				// all items in chain are already allocated
				if (bucketHash == tHash) { // allocated bucket found
					void *oldValue = prev->value.load(std::memory_order_relaxed);
					if (oldValue == MAP_VALUE_MIGRATION) {
						return INSERT_HELP_MIGRATION; // indicate overflow
					} else if (prev->value.compare_exchange_strong(oldValue, value, std::memory_order_acq_rel)) {
						TRACE(LFMap, 5);
					} else {
						TRACE(LFMap, 6);
					}
				
					// if there's a concurrent write or erase (i.e. CAS failed), giving up and pretending that value was overwritten
					return INSERT_OK;
				}

				delta = prev->deltaNext.load(std::memory_order_relaxed);
				jumps++;
			} while (delta != MAP_DELTA_UNDEF && delta != MAP_DELTA_EXTEND);

			delta = MAP_DELTA_UNDEF;
			if (prev->deltaNext.compare_exchange_strong(delta, MAP_DELTA_EXTEND, std::memory_order_acq_rel))
				break;

			while (prev->deltaNext.load(std::memory_order_relaxed) == MAP_DELTA_EXTEND) sched_yield(); // wait until parent is extended
			delta = 0;
		}

		// linear search
		for (int d = 0; d < MAP_NEIGHBOURHOOD; d++) {
			entr = root->array + ((i + d) & root->sizeMask);
			int64_t bucketHash = entr->hash.load(std::memory_order_relaxed);
		
			if (bucketHash == MAP_HASH_NULL) { // unallocated bucket
				int cellsBeforeInsert = root->freeBuckets.fetch_sub(1, std::memory_order_relaxed);

				if (cellsBeforeInsert <= 0) {
					TRACE(LFMap, 2);
					root->freeBuckets.fetch_add(1, std::memory_order_relaxed);

					// reset prev state
					prev->deltaNext.store(MAP_DELTA_UNDEF, std::memory_order_release);
					return INSERT_OVERFLOW; // indicate overflow
				}

				if (entr->hash.compare_exchange_strong(bucketHash, tHash, std::memory_order_relaxed)) {
					TRACE(LFMap, 3);
					bucketHash = tHash;
					prev->deltaNext.store(d > 0 ? d : MAP_DELTA_UNDEF, std::memory_order_release);
					bucketHash = tHash;
				} else {
		 	 		TRACE(LFMap, 4);
		 	 		root->freeBuckets.fetch_add(1, std::memory_order_relaxed);
				}
			}

			if (bucketHash == tHash) {
				// by chance bucket was allocated but unnoticed or allocated by code above
				void *oldValue = entr->value.load(std::memory_order_relaxed); // we are only interested in address
				if (oldValue == MAP_VALUE_MIGRATION) {
					return INSERT_HELP_MIGRATION; // indicate overflow
				} else if (entr->value.compare_exchange_strong(oldValue, value, std::memory_order_acq_rel)) {
					TRACE(LFMap, 5);
				} else {
					TRACE(LFMap, 6);
					if (oldValue == MAP_VALUE_MIGRATION) 
						return INSERT_HELP_MIGRATION;
				}

				// if there's a concurrent write or erase (i.e. CAS failed), giving up and pretending that value was overwritten
				return INSERT_OK;
			}
		}

		prev->deltaNext.store(MAP_DELTA_UNDEF, std::memory_order_release);
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
		overflowed(false), state(0), unitsRemaining(0), prev(NULL) {}

	virtual ~Migration() {
		if (prev != NULL) delete prev;
		TablesIterator it = sources.begin();
		for (it++; it != sources.end(); it++) {
			if (it->table) delete it->table;
		} // skip 1st
	}

	virtual void run() {
		if (state.load(std::memory_order_relaxed) & 1) {
			TRACE(LFMap, 12);
			return; // work is done, new table not published yet
		}
		state.fetch_add(2, std::memory_order_relaxed);

		for (TablesIterator it = sources.begin(); it != sources.end(); it++) {
			HashTable *table = it->table;

			while (true) {
				if (state.load(std::memory_order_relaxed) & 1) {
					TRACE(LFMap, 13);
					goto end_migration;
				}

				int index = it->index.fetch_add(LFMAP_MIGRATION_CHUNK_SIZE, std::memory_order_relaxed);
				if (index > table->sizeMask) break; // migrate next source 

				bool rangeOverflow = migrateRange(table, index);
				if (rangeOverflow) {
					TRACE(LFMap, 14);
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
		TRACE(LFMap, 15);

end_migration:

		int stateProbe = state.fetch_sub(2, std::memory_order_acq_rel); // see all changes

		if (stateProbe > 3) {
			TRACE(LFMap, 16);
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
					it->table = NULL;
				}

				m->sources[sources.size()].table = dest;
				m->sources[sources.size()].index.store(0, std::memory_order_relaxed);
				m->unitsRemaining = unitsRemaining + dest->getMigrationSize();
				m->prev = this;

				origTable->coordinator.set(m);
        	} else {
        		TRACE(LFMap, 17);
        	}
		}
	}

	bool migrateRange(HashTable *from, int startIndex) {
		int last = std::min(startIndex + LFMAP_MIGRATION_CHUNK_SIZE, from->sizeMask + 1);

		for (int index = startIndex; index < last; index++) {
			LockFreeMapEntry *entry = &from->array[index];
			while (true) {
				int64_t srcHash = entry->hash.load(std::memory_order_relaxed);
				void *srcValue;

				if (srcHash == MAP_HASH_NULL) { // unused cell
					srcValue = NULL; // expect NULL for unused cell value
					if (entry->value.compare_exchange_strong(srcValue, MAP_VALUE_MIGRATION, std::memory_order_relaxed)) {
						break; // nothing to move to new table
					} else if (srcValue == MAP_VALUE_MIGRATION) {
						TRACE(LFMap, 18);
						break; // found previous unfinished migration
					}
					TRACE(LFMap, 19);
					// someone placed value to the cell, reread
				} else { // used cell: deleted or not
					srcValue = entry->value.load(std::memory_order_relaxed); // we only need a pointer
					if (srcValue == NULL) { // deleted cell
						if (entry->value.compare_exchange_strong(srcValue, MAP_VALUE_MIGRATION, std::memory_order_relaxed)) {
							break; // nothing to move to new table
						}
						TRACE(LFMap, 20);
						// someone placed value to the cell, evacuation required
					} else if (srcValue == MAP_VALUE_MIGRATION) {
						TRACE(LFMap, 21);
						break; // found previous unfinished migration
					}

					while (!entry->value.compare_exchange_strong(srcValue, MAP_VALUE_MIGRATION, std::memory_order_relaxed)) sched_yield();

					// only 1 thread can migrate bucket at time, so srcValue != MAP_VALUE_MIGRATION and srcHash is not in dest
					if (srcValue != NULL) {
						InsertOutcome res = LockFreeMapPrimitives::insertOrUpdate(dest, srcHash, srcValue);
						if (res == INSERT_OVERFLOW) {
							entry->value.store(srcValue, std::memory_order_release); // return replaced value
							return true; // overflow
						}
					} else {
						TRACE(LFMap, 22);
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
	virtual void put(void *key, void *value) = 0;
	virtual void *get(void *key) = 0;
	virtual void *remove(void *key) = 0;
	virtual ~AbstractMapProvider() {}
};

/* Hasher assumed to be collision free. */
template <typename Hasher, bool signalSafeReaders>
class ConcurrentMapProvider : public AbstractMapProvider {
private:
	TableGuard current;
	HashFunction hasher;

	void migrationStart(HashTable *table, bool doubleSize) {
		int newSize;
		if (doubleSize) {
			newSize = ((table->sizeMask + 1) << 1);
		} else {
			int estimatedSize = 0;
			int sampleMax = std::min(LFMAP_MAX_SAMPLE_SIZE, table->sizeMask + 1);
			for (int i = 0; i < sampleMax; i++) {
				// we just need a pointer, not content it is pointing to
				void *value = table->array[i].value.load(std::memory_order_relaxed);

				if (value == MAP_VALUE_MIGRATION) {
					// someone has already strated the migration, join it
					TRACE(LFMap, 10);
					return;
				} else if (value != NULL) {
					estimatedSize++;
				}
			}
			int sizeApprox = (table->sizeMask + 1) * float(estimatedSize) / sampleMax;
			newSize = std::max(LFMAP_HASHTABLE_SIZE_MIN, nearestPow2(2 * sizeApprox));
		}
		migrationStart(table, newSize);
	}

	void migrationStart(HashTable *table, int targetSize) {
		std::lock_guard<std::mutex> guard(table->mutex);

		JobCoordinator::Job *migration = table->coordinator.get();
		if (migration) {
			// no need to create second migration job
			TRACE(LFMap, 11);
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
	ConcurrentMapProvider(size_t initialSize) : current(new HashTable(initialSize)), hasher(&Hasher::hash) {
	}

	virtual ~ConcurrentMapProvider() {
	}

	void put(void *key, void *value) {
		bool doubleSize = false;
		while (true) {
			ScopedGuard guard(current);
			HashTable *root = guard.root;
			InsertOutcome res = LockFreeMapPrimitives::insertOrUpdate(root, key, value, hasher);

			if (res == INSERT_OK) {
				return;
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
	void *get(void *key) {
		while (true) {
			ScopedGuard guard(current);
			HashTable *root = guard.root;

			LockFreeMapEntry *el = LockFreeMapPrimitives::find(root, key, hasher);
			void *res = el ? el->value.load(std::memory_order_consume) : NULL;

			if (res != MAP_VALUE_MIGRATION)
				return res;

			if (!signalSafeReaders) 
				root->coordinator.participate();
		}
	}

	void *remove(void *key) {
		while (true) {
			ScopedGuard guard(current);
			HashTable *root = guard.root;

			LockFreeMapEntry *entr = LockFreeMapPrimitives::find(root, key, hasher);
			if (entr == NULL) { // not found
				TRACE(LFMap, 7);
				return NULL;
			}

			void *oldValue = entr->value.load(std::memory_order_relaxed);
			if (oldValue == MAP_VALUE_MIGRATION) {
				root->coordinator.participate();
			} else if (entr->value.compare_exchange_strong(oldValue, NULL, std::memory_order_consume)) {
				TRACE(LFMap, 8);
				return oldValue;
			} else { // CAS failed
				// there's a concurrent write or erase, giving up and pretending that value was overwritten
				TRACE(LFMap, 9);
				return NULL;
			}
		}
	}

	// Migration callback, called once per successful migration
	void finishMigration(HashTable *newRoot) {
		current.publish(newRoot);
	}

	int capacity() {
		ScopedGuard guard(current);
		return guard.root->sizeMask + 1;
	}

	int unsafeUsed() {
		int size = 0;
		ScopedGuard guard(current);
		HashTable *root = guard.root;
		for (int i = 0; i < root->sizeMask + 1; i++) {
			void *value = root->array[i].value.load(std::memory_order_relaxed);
			if (value != NULL && value != MAP_VALUE_MIGRATION)
				size++;
		}
		return size;
	}

	int unsafeDirty() {
		int size = 0;
		ScopedGuard guard(current);
		HashTable *root = guard.root;
		for (int i = 0; i < root->sizeMask + 1; i++) {
			if (root->array[i].hash.load(std::memory_order_relaxed) != MAP_HASH_NULL)
				size++;
		}
		return size;
	}
};

} // namespace map end

#endif
