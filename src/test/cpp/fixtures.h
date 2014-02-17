#include "../../main/cpp/circular_queue.h"

#ifndef FIXTURES_H
#define FIXTURES_H

class ItemHolder : public QueueListener {
public:
  explicit ItemHolder() {}

  virtual void record(const JVMPI_CallTrace &item) { lastSeenTrace = &item; }

  const JVMPI_CallTrace *lastSeenTrace;
};

// Queue too big to stack allocate,
// So we use a fixture
struct GivenQueue {
  GivenQueue() {
    holder = new ItemHolder();
    queue = new CircularQueue(*holder);
  }

  ~GivenQueue() {
    delete holder;
    delete queue;
  }

  ItemHolder *holder;

  CircularQueue *queue;

  // wrap an easy to test api around the queue
  bool pop(JVMPI_CallTrace &item) {
    bool result = queue->pop();
    item = *(holder->lastSeenTrace);
    return result;
  }
};

#endif /* FIXTURES_H */
