#ifndef THREAD_MAP_H
#define THREAD_MAP_H

#include "concurrent_map.h"
#include <jni.h>
#include <jvmti.h>
#include <string.h>

int gettid();

template <typename PType> struct PointerHasher {
  /* Numerical Recipes, 3rd Edition */
  static int64_t hash(void *p) {
    int64_t v = (int64_t)p / sizeof(PType);
    v = v * 3935559000370003845 + 2691343689449507681;

    v ^= v >> 21;
    v ^= v << 37;
    v ^= v >> 4;

    v *= 4768777513237032717;

    v ^= v << 20;
    v ^= v >> 41;
    v ^= v << 5;

    return v;
  }
};

const int kInitialMapSize = 256;

class GCHelper {
public:
  static map::GC::EpochType attach() { return map::DefaultGC.attachThread(); }

  static void detach(map::GC::EpochType &localEpoch) {
    if (localEpoch != map::GC::kEpochInitial)
      map::DefaultGC.detachThread(localEpoch);
  }

  static void safepoint(map::GC::EpochType &localEpoch) { map::DefaultGC.safepoint(localEpoch); }

  static void signalSafepoint(map::GC::EpochType &localEpoch) { map::DefaultGC.ss_safepoint(localEpoch); }
};

struct ThreadBucket {
  const int tid;
  std::string name;
  std::atomic_int refs;
  map::GC::EpochType localEpoch;

  explicit ThreadBucket(int id, const char *n) : tid(id), name(n), refs(1), localEpoch(GCHelper::attach()) {}

  int release() { return refs.fetch_sub(1, std::memory_order_acquire); }

  ~ThreadBucket() {}
};

/* ThreadBucket* wrapper that does atomic reference counting and only supports
   move semantic. Here weak means that ref counter is not incremented when
   wrapper is created, but it will be decremented once object is destroyed.
*/
class ThreadBucketPtr {
public:
  explicit ThreadBucketPtr(ThreadBucket *b, bool weak = true) : bucket(b) {
    if (bucket && !weak) {
      int prev = bucket->refs.fetch_add(1, std::memory_order_relaxed);
      assert(prev >= 0);
      if (prev == 0) {
        // return to released state
        bucket->refs.fetch_sub(1, std::memory_order_relaxed);
      }
    }
  }

  ThreadBucketPtr(ThreadBucketPtr &&tb) : bucket(tb.bucket) { tb.bucket = nullptr; }

  ThreadBucketPtr(const ThreadBucketPtr &tb) = delete;

  ThreadBucketPtr &operator=(ThreadBucketPtr &&tb) {
    if (bucket && bucket->release() == 1) {
      delete bucket;
    }
    bucket = tb.bucket;
    tb.bucket = nullptr;
    return *this;
  }

  ThreadBucketPtr &operator=(const ThreadBucketPtr &tb) = delete;

  ThreadBucket *operator->() { return bucket; }

  bool defined() const { return bucket != nullptr; }

  void reset() {
    if (bucket && bucket->release() == 1) {
      delete bucket;
    }
    bucket = nullptr;
  }

  ~ThreadBucketPtr() {
    if (bucket && bucket->release() == 1) {
      delete bucket;
    }
  }

private:
  ThreadBucket *bucket;
};

template <typename MapProvider> class ThreadMapBase {
private:
  MapProvider map;

public:
  explicit ThreadMapBase(int capacity = kInitialMapSize) : map(capacity) {}

  void put(JNIEnv *jni_env, const char *name) { put(jni_env, name, gettid()); }

  void put(JNIEnv *jni_env, const char *name, int tid) {
    ThreadBucket *info = new ThreadBucket(tid, name);
    ThreadBucketPtr oldRef((ThreadBucket *)map.put((map::KeyType)jni_env, (map::ValueType)info)); // weak ref to object
    GCHelper::safepoint(info->localEpoch); // each thread inserts once
  }

  ThreadBucketPtr get(JNIEnv *jni_env) {
    ThreadBucketPtr info((ThreadBucket *)map.get((map::KeyType)jni_env), false); // non-weak ref
    if (info.defined())
      GCHelper::signalSafepoint(info->localEpoch);
    return info; // move
  }

  void remove(JNIEnv *jni_env) {
    ThreadBucketPtr info((ThreadBucket *)map.remove((map::KeyType)jni_env)); // weak ref to object
    if (info.defined())
      GCHelper::detach(info->localEpoch);
  }
};

typedef ThreadMapBase<map::ConcurrentMapProvider<PointerHasher<JNIEnv>, true>> ThreadMap;

#endif
