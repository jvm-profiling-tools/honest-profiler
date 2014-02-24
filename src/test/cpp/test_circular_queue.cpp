#include <UnitTest++.h>

#include "fixtures.h"
#include "../../main/cpp/stacktraces.h"

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

void checkStackTrace(const JVMPI_CallTrace &trace, const int envId) {
  CHECK_EQUAL(2, trace.num_frames);
  CHECK_EQUAL((JNIEnv *)envId, trace.env_id);

  JVMPI_CallFrame frame0 = trace.frames[0];
  CHECK_EQUAL(52, frame0.lineno);
  CHECK_EQUAL((jmethodID)1, frame0.method_id);
}

TEST_FIXTURE(GivenQueue, EmptyQueueWontPop) {
  JVMPI_CallTrace item;
  CHECK(!pop(item));
}

TEST_FIXTURE(GivenQueue, OnlyInOnlyOut) {
  givenStackTrace(5);

  // when you push then pop
  CHECK(queue->push(trace));
  JVMPI_CallTrace item;
  CHECK(pop(item));

  // then you get the same value back
  checkStackTrace(item, 5);
  CHECK(!pop(item));
}

void pushLocalTraceOnto(CircularQueue &queue, const int envId) {
  givenStackTrace(envId);
  CHECK(queue.push(trace));
}

TEST_FIXTURE(GivenQueue, ElementsAreGenuinelyCopied) {
  pushLocalTraceOnto(*queue, 5);

  JVMPI_CallTrace item;
  CHECK(pop(item));

  // then you get the same value back
  checkStackTrace(item, 5);
  CHECK(!pop(item));
}

TEST_FIXTURE(GivenQueue, FirstInFirstOut) {
  pushLocalTraceOnto(*queue, 5);
  pushLocalTraceOnto(*queue, 6);

  JVMPI_CallTrace first;
  CHECK(pop(first));
  checkStackTrace(first, 5);

  JVMPI_CallTrace second;
  CHECK(pop(second));
  checkStackTrace(second, 6);

  CHECK(!pop(second));
}

TEST_FIXTURE(GivenQueue, CantOverWriteUnreadInput) {
  for (int i = 0; i < Size; i++) {
    pushLocalTraceOnto(*queue, 5);
  }

  givenStackTrace(5);
  CHECK(!queue->push(trace));

  JVMPI_CallTrace out;
  for (int i = 0; i < Size; i++) {
    CHECK(pop(out));
    checkStackTrace(out, 5);
  }

  CHECK(!pop(out));
}
