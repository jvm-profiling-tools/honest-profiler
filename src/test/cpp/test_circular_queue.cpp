#include <thread>
#include <vector>
#include <iostream>
#include "fixtures.h"
#include "test.h"

#define givenStackTrace(envId)                                                 \
  JVMPI_CallFrame frame0 = {};                                                 \
  frame0.lineno = 52;                                                          \
  frame0.method_id = (jmethodID)1;                                             \
                                                                               \
  JVMPI_CallFrame frame1 = {};                                                 \
  frame1.lineno = 42;                                                          \
  frame1.method_id = (jmethodID)1;                                             \
                                                                               \
  JVMPI_CallFrame frames[] = { frame0, frame1 };                               \
                                                                               \
  JVMPI_CallTrace trace = {};                                                  \
  trace.env_id = (JNIEnv *)envId;                                              \
  trace.num_frames = 2;                                                        \
  trace.frames = frames;


TEST_FIXTURE(GivenQueue, EmptyQueueWontPop) {
  CHECK(!pop(0));
}

TEST_FIXTURE(GivenQueue, OnlyInOnlyOut) {
  givenStackTrace(5);

  // when you push then pop
  CHECK(queue->push(trace));
  // then you get the same value back
  CHECK(pop(5));
  // and no more
  CHECK(!pop(0));
}

void pushLocalTraceOnto(CircularQueue &queue, const long envId) {
  givenStackTrace(envId);
  CHECK(queue.push(trace));
}

TEST_FIXTURE(GivenQueue, ElementsAreGenuinelyCopied) {
  pushLocalTraceOnto(*queue, 5);

  CHECK(pop(5));

  // then you get the same value back
  CHECK(!pop(0));
}

TEST_FIXTURE(GivenQueue, FirstInFirstOut) {
  pushLocalTraceOnto(*queue, 5);
  pushLocalTraceOnto(*queue, 6);

  CHECK(pop(5));

  CHECK(pop(6));

  CHECK(!pop(0));
}

TEST_FIXTURE(GivenQueue, CantOverWriteUnreadInput) {
  for (int i = 0; i < Size; i++) {
    pushLocalTraceOnto(*queue, 5);
  }

  givenStackTrace(5);
  CHECK(!queue->push(trace));

  for (int i = 0; i < Size; i++) {
    CHECK(pop(5));
  }

  CHECK(!pop(0));
}

/* Prevent floating point exception for GCC < 4.7 */
const int THREAD_COUNT = std::thread::hardware_concurrency() ?
  std::thread::hardware_concurrency() : 1;
const int THREAD_GAP = Size / THREAD_COUNT;

void runnable(long start, CircularQueue* queue) {
  start *= THREAD_GAP;
  givenStackTrace(start);
  int end = start + THREAD_GAP;
  for (long i = start; i < end; i++) {
    trace.env_id = (JNIEnv *) i;
    CHECK(queue->push(trace));
  }
}

/*
TEST_FIXTURE(GivenQueue, MultiThreadedRun) {
  // Given 4 threads pushing in traces
  std::vector<std::thread> workers;
  std::vector<int> counters;

  for (int i = 0; i < THREAD_COUNT; i++) {
    workers.push_back(std::thread(runnable, i, queue));
    counters.push_back(i * THREAD_GAP);
  }
  for (std::thread &thread: workers) {
    thread.join();
  }

  // When you pop all the values out of the queue
  while(pop(5)) {
    long counter = (long) out.env_id;
    long index = counter / THREAD_GAP;

    // Then pushed values are incremental within each thread
    CHECK_EQUAL(counters[index], counter);
    counters[index]++;
  }
  
  // And you get the required number of values
  int total = THREAD_GAP;
  for (int i : counters) {
    CHECK_EQUAL(i, total);
    total += THREAD_GAP;
  }
}
*/

