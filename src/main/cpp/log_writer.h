#include <jvmti.h>
#include <unordered_set>
#include <iostream>
#include <string.h>

#include "circular_queue.h"
#include "stacktraces.h"

#ifndef LOG_WRITER_H
#define LOG_WRITER_H

using std::ostream;
using std::unordered_set;

typedef unsigned char byte;
typedef int64_t method_id;

class MethodListener {
public:
  virtual void recordNewMethod(method_id methodId, const char *file_name,
                               const char *class_name,
                               const char *method_name) = 0;
  virtual ~MethodListener() {}
};

typedef bool (*GetFrameInformation)(const JVMPI_CallFrame &frame,
                                    jvmtiEnv *jvmti, MethodListener &logWriter);

const size_t FIFO_SIZE = 10;
const byte TRACE_START = 0;
const byte FRAME = 1;
const byte NEW_METHOD = 2;

// LogWriter should be independently testable without spinning up a JVM
class LogWriter final : public QueueListener, public MethodListener {

public:
  explicit LogWriter(ostream &output, GetFrameInformation frameLookup,
                     jvmtiEnv *jvmti)
      : output_(output), frameLookup_(frameLookup), jvmti_(jvmti) {}

  virtual void record(const JVMPI_CallTrace &trace);

  void recordTraceStart(const jint num_frames, const int64_t threadId);

  // method are unique pointers, use a long to standardise
  // between 32 and 64 bits
  void recordFrame(const jint lineNumber, method_id methodId);

  virtual void recordNewMethod(method_id methodId, const char *file_name,
                               const char *class_name, const char *method_name);

private:
  ostream &output_;

  GetFrameInformation frameLookup_;

  jvmtiEnv *jvmti_;

  unordered_set<method_id> knownMethods;

  template <typename T> void writeValue(const T &value);

  void writeWithSize(const char *value);

  void inspectMethod(const method_id methodId, const JVMPI_CallFrame &frame);

  DISALLOW_COPY_AND_ASSIGN(LogWriter);
};

#endif // LOG_WRITER_H
