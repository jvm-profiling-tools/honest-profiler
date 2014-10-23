
// Somewhat originally dervied from:
// http://www.codeproject.com/Articles/43510/Lock-Free-Single-Producer-Single-Consumer-Circular

// Multiple Producer, Single Consumer Queue

#ifndef CIRCULAR_QUEUE_H
#define CIRCULAR_QUEUE_H

#include "stacktraces.h"
#include <string.h>

#include <atomic>
#include <cstddef>

const size_t Size = 1024;

// Capacity is 1 larger than size to make sure
// we can use input = output as our "can't read" invariant
// and advance(output) = input as our "can't write" invariant
// effective the gap acts as a sentinel
const size_t Capacity = Size + 1;

// We have to set every byte to 0 instead of just initializing the
// individual fields, because the structs might be padded, and we
// use memcmp on it later.  We can't use memset, because it isn't
// async-safe.
void safe_reset(void *start, size_t size);

class QueueListener {
public:
  virtual void record(const JVMPI_CallTrace &item) = 0;
  virtual ~QueueListener() {}
};

class CircularQueue {
public:
  explicit CircularQueue(QueueListener &listener)
      : listener_(listener), input(0), output(0) {
    memset(buffer, 0, sizeof(buffer));
    memset((void *) frame_buffer_, 0, sizeof(frame_buffer_));
  }
  ~CircularQueue() {}

  bool push(const JVMPI_CallTrace &item);

  bool pop();

private:

  QueueListener &listener_;

  std::atomic<size_t> input;
  std::atomic<size_t> output;

  JVMPI_CallTrace buffer[Capacity];
  JVMPI_CallFrame frame_buffer_[Capacity][kMaxFramesToCapture];

  size_t advance(size_t index) const;

  void write(const JVMPI_CallTrace &item, const size_t slot);
};

#endif /* CIRCULAR_QUEUE_H */
