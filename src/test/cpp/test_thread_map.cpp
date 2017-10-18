#include "test.h"

#ifndef DISABLE_CPP11

#include "../../main/cpp/thread_map.h"

#include <memory>
#include <thread>
#include <vector>

#define ptr() std::unique_ptr<JNIEnv>((JNIEnv *)new int(0))

TEST(ThreadMapPutGetDeleteTest) {
  ThreadMap map;
  auto p1 = ptr();
  auto pid = 999;
  auto name = "name321";

  // map is empty
  CHECK(!map.get(p1.get()).defined());

  map.put(p1.get(), name, pid);

  // try to create temporary references to the bucket
  for (int i = 0; i < 4; i++) {
    ThreadBucketPtr r1 = map.get(p1.get());
    CHECK(r1.defined());
    CHECK_EQUAL(pid, r1->tid);
    CHECK_EQUAL(name, r1->name);
  }

  map.remove(p1.get());

  // map is empty again
  CHECK(!map.get(p1.get()).defined());
}

TEST(ThreadMapUpdateTest) {
  ThreadMap map;
  auto p1 = ptr();
  auto pid1 = 999, pid2 = 111;
  auto name1 = "name321", name2 = "name123";

  // map is empty
  CHECK(!map.get(p1.get()).defined());

  map.put(p1.get(), name1, pid1);

  {
    ThreadBucketPtr r1 = map.get(p1.get());
    CHECK(r1.defined());
    CHECK_EQUAL(pid1, r1->tid);
    CHECK_EQUAL(name1, r1->name);
  }

  // update
  map.put(p1.get(), name2, pid2);

  {
    ThreadBucketPtr r1 = map.get(p1.get());
    CHECK(r1.defined());
    CHECK_EQUAL(pid2, r1->tid);
    CHECK_EQUAL(name2, r1->name);
  }

  map.remove(p1.get());

  // map is empty again
  CHECK(!map.get(p1.get()).defined());
}

TEST(ThreadMapPutDeleteGetTest) {
  ThreadMap map;
  auto p1 = ptr();
  auto pid = 999;
  auto name = "name321";

  // map is empty
  CHECK(!map.get(p1.get()).defined());

  map.put(p1.get(), name, pid);

  {
    ThreadBucketPtr r1 = map.get(p1.get()), r2 = map.get(p1.get()), r3 = map.get(p1.get());
    map.remove(p1.get());
    CHECK(!map.get(p1.get()).defined());

    CHECK(r1.defined());
    CHECK(r2.defined());
    CHECK(r3.defined());

    CHECK_EQUAL(pid, r2->tid);
    CHECK_EQUAL(name, r2->name);
  }

  CHECK(!map.get(p1.get()).defined());
}

TEST(ThreadMapAssignResetTest) {
  ThreadMap map;
  auto p1 = ptr(), p2 = ptr();
  auto pid1 = 999, pid2 = 111;
  auto name1 = "name321", name2 = "name123";

  // map is empty
  CHECK(!map.get(p1.get()).defined());
  CHECK(!map.get(p2.get()).defined());

  map.put(p1.get(), name1, pid1);
  map.put(p2.get(), name2, pid2);

  {
    ThreadBucketPtr r1 = map.get(p1.get());
    CHECK(r1.defined());
    CHECK_EQUAL(pid1, r1->tid);
    CHECK_EQUAL(name1, r1->name);

    map.remove(p1.get());
    CHECK(!map.get(p1.get()).defined());

    r1 = map.get(p2.get());
    CHECK(r1.defined());
    CHECK_EQUAL(pid2, r1->tid);
    CHECK_EQUAL(name2, r1->name);

    map.remove(p2.get());
    CHECK(!map.get(p2.get()).defined());

    r1.reset();
    CHECK(!r1.defined());

    r1.reset();
  }

  // map is empty
  CHECK(!map.get(p1.get()).defined());
  CHECK(!map.get(p2.get()).defined());
}

void reader(ThreadMap &map, const int id, JNIEnv *const p, std::atomic_int &read) {
  ThreadBucketPtr ptr = map.get(p);
  std::string name("thread-" + std::to_string(id));

  while (!ptr.defined()) {
    sched_yield();
    ptr = map.get(p);
  }

  read.fetch_add(1, std::memory_order_seq_cst);
  do {
    CHECK_EQUAL(name, ptr->name);
    CHECK_EQUAL(id, ptr->tid);
  } while ((ptr = map.get(p)).defined());
}

TEST(ThreadMapConcurrentTest) {
  const int sz = 4;
  ThreadMap map;

  std::atomic_int read(0);
  std::vector<std::unique_ptr<JNIEnv>> ps(sz);
  std::vector<std::thread> tvec(sz);

  for (auto it = ps.begin(); it != ps.end(); ++it) {
    *it = ptr();
    int id = it - ps.begin();
    tvec[id] = std::thread(reader, std::ref(map), id, it->get(), std::ref(read));
  }

  for (auto it = ps.begin(); it != ps.end(); ++it) {
    int id = it - ps.begin();
    std::string name("thread-" + std::to_string(id));
    map.put(it->get(), name.c_str(), id);
  }

  while (read.load(std::memory_order_acquire) != sz)
    sched_yield();

  for (auto it = ps.begin(); it != ps.end(); ++it) {
    map.remove(it->get());
  }

  for (auto it = tvec.begin(); it != tvec.end(); ++it) {
    it->join();
    CHECK(!map.get(ps[it - tvec.begin()].get()).defined());
  }
}

#endif // DISABLE_CPP11