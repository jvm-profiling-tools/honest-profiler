#include "test.h"

#ifndef DISABLE_CPP11

#include "../../main/cpp/thread_map.h"

#include <thread>
#include <vector>
#include <memory>

#define PTR std::unique_ptr<JNIEnv>((JNIEnv*)new int(0))

TEST(ThreadMapPutGetDeleteTest) {
    ThreadMap map;
    auto p1 = PTR;
    auto pid = 999;
    auto name = "name321";

    // map is empty
    CHECK(!map.get(p1.get()).defined());

    map.put(p1.get(), name, pid);

    // try to create temporary references to the bucket
    for (int i = 0; i < 4; i++) {
        ThreadBucketPtr r1 = map.get(p1.get());
        CHECK(r1.defined());
        CHECK_EQUAL(r1->tid, pid);
        CHECK_EQUAL(r1->name, name);
    }

    map.remove(p1.get());

    // map is empty again
    CHECK(!map.get(p1.get()).defined());
}

TEST(ThreadMapPutDeleteGetTest) {
    ThreadMap map;
    auto p1 = PTR;
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

        CHECK_EQUAL(r2->tid, pid);
        CHECK_EQUAL(r2->name, name);
    }

    CHECK(!map.get(p1.get()).defined());
}

TEST(ThreadMapAssignResetTest) {
    ThreadMap map;
    auto p1 = PTR, p2 = PTR;
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
        CHECK_EQUAL(r1->tid, pid1);
        CHECK_EQUAL(r1->name, name1);

        map.remove(p1.get());
        CHECK(!map.get(p1.get()).defined());

        r1 = map.get(p2.get());
        CHECK(r1.defined());
        CHECK_EQUAL(r1->tid, pid2);
        CHECK_EQUAL(r1->name, name2);

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

#endif // DISABLE_CPP11