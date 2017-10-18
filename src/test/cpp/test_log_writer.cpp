#include <iostream>
#include <fstream>
#include <limits>
#include <memory>

#include "fixtures.h"
#include "ostreambuf.h"
#include "test.h"
#include "../../main/cpp/log_writer.h"

using std::ostream;
using std::ofstream;

#define copyString(from, to)                                                   \
  to = (char *)malloc(sizeof(from));                                           \
  strcpy(to, from)

// leaks memory during tests
bool stubFrameInformation(const JVMPI_CallFrame &frame, MethodListener &listener) {
  listener.recordNewMethod((method_id)frame.method_id, "c", "b", "a");
  return true;
}

// Queue is too large to be stack allocated
#define givenLogWriter()                                                       \
  char buffer[100] = {};                                                       \
  ostreambuf<char> outputBuffer(buffer, sizeof(buffer));                       \
  ostream output(&outputBuffer);                                               \
  LogWriter logWriter(output, &stubFrameInformation, NULL);                    \
  CircularQueue *queue = new CircularQueue(logWriter, DEFAULT_MAX_FRAMES_TO_CAPTURE);  

#define done() delete queue;

TEST(RecordsStartOfStackTrace) {
  givenLogWriter();
  auto threadInfo = std::unique_ptr<ThreadBucket>(new ThreadBucket(22, "Thr-222"));
  timespec tspec = {44, 55};
  ThreadBucketPtr tptr(threadInfo.get(), false);

  logWriter.recordTraceStart(2, 3, tspec, tptr);
  int cnt = 0; 

  CHECK_EQUAL(THREAD_META, buffer[cnt]);
  CHECK_EQUAL(22, buffer[cnt+=8]);
  CHECK_EQUAL(threadInfo->name.size(), buffer[cnt+=4]);
  cnt++;
  CHECK_EQUAL(threadInfo->name, std::string(buffer + cnt, threadInfo->name.size()));
  cnt += threadInfo->name.size();

  CHECK_EQUAL(TRACE_WITH_TIME, buffer[cnt]);
  CHECK_EQUAL(2, buffer[cnt+=4]);
  CHECK_EQUAL(22, buffer[cnt+=8]);
  CHECK_EQUAL(44, buffer[cnt+=8]);
  CHECK_EQUAL(55, buffer[cnt+=8]);

  GCHelper::detach(threadInfo->localEpoch);
  done();
}

TEST(SupportsHighThreadId) {
  givenLogWriter();
  timespec tspec = {44, 55};

  // LONG_MAX
  long bigNumber = std::numeric_limits<long>::max();
  ThreadBucketPtr tBuck(nullptr);
  logWriter.recordTraceStart(2, (map::HashType)bigNumber, tspec, tBuck);

  CHECK_EQUAL(THREAD_META, buffer[0]);
  CHECK_EQUAL(0, buffer[12]);

  CHECK_EQUAL(1, buffer[25]);
  CHECK_EQUAL(1 << 7, buffer[18]);

  done();
}

TEST(RecordsStackFrames) {
  givenLogWriter();

  logWriter.recordFrame(5, 6);
  CHECK_EQUAL(FRAME_BCI_ONLY, buffer[0]);
  CHECK_EQUAL(5, buffer[4]);
  CHECK_EQUAL(0, buffer[3]);
  CHECK_EQUAL(6, buffer[12]);
  CHECK_EQUAL(0, buffer[11]);

  done();
}

#define intThen ((index += 4) - 1)
#define longThen ((index += 8) - 1)

TEST(RecordsMethodNames) {
  givenLogWriter();

  logWriter.recordNewMethod(12, "a", "b", "cde");

  int index = 0;
  CHECK_EQUAL(NEW_METHOD, buffer[index++]);
  CHECK_EQUAL(12, buffer[longThen]);
  CHECK_EQUAL(1, buffer[intThen]);
  CHECK_EQUAL('a', buffer[index++]);
  CHECK_EQUAL(1, buffer[intThen]);
  CHECK_EQUAL('b', buffer[index++]);
  CHECK_EQUAL(3, buffer[intThen]);
  CHECK_EQUAL('c', buffer[index++]);
  CHECK_EQUAL('d', buffer[index++]);
  CHECK_EQUAL('e', buffer[index++]);

  done();
}

void thenACompleteLogIsOutput(char buffer[]) {
  int index = 0;

  CHECK_EQUAL(THREAD_META, buffer[index++]);
  CHECK_EQUAL(-5 & 0x000000ff, buffer[longThen]);
  CHECK_EQUAL(0, buffer[intThen]);

  CHECK_EQUAL(TRACE_WITH_TIME, buffer[index++]);
  CHECK_EQUAL(2, buffer[intThen]);
  CHECK_EQUAL(-5 & 0x000000ff, buffer[longThen]);
  CHECK_EQUAL(44, buffer[longThen]);
  CHECK_EQUAL(55, buffer[longThen]);

  CHECK_EQUAL(FRAME_BCI_ONLY, buffer[index++]);
  CHECK_EQUAL(0, buffer[intThen]);
  CHECK_EQUAL(1, buffer[longThen]);

  CHECK_EQUAL(NEW_METHOD, buffer[index++]);
  CHECK_EQUAL(1, buffer[longThen]);
  CHECK_EQUAL(1, buffer[intThen]);
  CHECK_EQUAL('c', buffer[index++]);
  CHECK_EQUAL(1, buffer[intThen]);
  CHECK_EQUAL('b', buffer[index++]);
  CHECK_EQUAL(1, buffer[intThen]);
  CHECK_EQUAL('a', buffer[index++]);

  CHECK_EQUAL(FRAME_BCI_ONLY, buffer[index++]);
  CHECK_EQUAL(0, buffer[intThen]);
  CHECK_EQUAL(2, buffer[longThen]);
}

#define givenStackTrace()                                                      \
  JVMPI_CallFrame frame0 = {};                                                 \
  frame0.lineno = 0;                                                           \
  frame0.method_id = (jmethodID)1;                                             \
                                                                               \
  JVMPI_CallFrame frame1 = {};                                                 \
  frame1.lineno = 0;                                                           \
  frame1.method_id = (jmethodID)2;                                             \
                                                                               \
  JVMPI_CallFrame frames[] = { frame0, frame1 };                               \
                                                                               \
  JVMPI_CallTrace trace = {};                                                  \
  trace.env_id = (JNIEnv *)5;                                                  \
  trace.num_frames = 2;                                                        \
  trace.frames = frames;

TEST(ExtractsStackTraceInformation) {
  givenLogWriter();
  givenStackTrace();
  timespec tspec = {44, 55};

  logWriter.record(tspec, trace);

  thenACompleteLogIsOutput(buffer);

  done();
}

bool dumpStubFrameInformation(const JVMPI_CallFrame &frame, MethodListener &listener) {
  method_id id = (method_id)frame.method_id;
  if (frame.method_id == (jmethodID)1) {
    listener.recordNewMethod(id, "PrintStream.java", "Ljava/io/PrintStream;",
                             "printf");
  } else {
    listener.recordNewMethod(id, "PrintStream.java", "Ljava/io/PrintStream;",
                             "append");
  }
  return true;
}

TEST(DumpTestFile) {
  givenStackTrace();

  ofstream output("dump.hpl", ofstream::out | ofstream::binary);
  LogWriter logWriter(output, &dumpStubFrameInformation, NULL);

  logWriter.record(trace);
  logWriter.record(trace);
}
