#include "circular_queue.h"
#include <iostream>
#include <unistd.h>

bool CircularQueue::push(const JVMPI_CallTrace &item, ThreadBucket *info) {
    size_t currentInput;
    size_t nextInput;
    do {
        currentInput = input.load(std::memory_order_relaxed);
        nextInput = advance(currentInput);
        if (output.load(std::memory_order_relaxed) == nextInput) {
            return false;
        }
        // TODO: have someone review the memory ordering constraints
    } while (!input.compare_exchange_strong(currentInput, nextInput, std::memory_order_relaxed));
    write(item, currentInput);
    buffer[currentInput].info = info;
    buffer[currentInput].is_committed.store(COMMITTED, std::memory_order_release);

    return true;
}

// Unable to use memcpy inside the push method because its not async-safe
void CircularQueue::write(const JVMPI_CallTrace &trace, const size_t slot) {
    JVMPI_CallFrame *fb = frame_buffer_[slot];
    for (int frame_num = 0; frame_num < trace.num_frames; ++frame_num) {
        // Padding already set to 0 by the consumer.

        fb[frame_num].lineno = trace.frames[frame_num].lineno;
        fb[frame_num].method_id = trace.frames[frame_num].method_id;
    }

    buffer[slot].trace.frames = fb;
    buffer[slot].trace.num_frames = trace.num_frames;
    buffer[slot].trace.env_id = trace.env_id;
}

bool CircularQueue::pop() {
    const auto current_output = output.load(std::memory_order_relaxed);

    // queue is empty
    if (current_output == input.load(std::memory_order_relaxed)) {
        return false;
    }

    // wait until we've finished writing to the buffer
    while (buffer[current_output].is_committed.load(std::memory_order_acquire) != COMMITTED) {
        usleep(1);
    }

    listener_.record(buffer[current_output].trace, buffer[current_output].info);
    
    // 0 out all frames so the next write is clean
    JVMPI_CallFrame *fb = frame_buffer_[current_output];
    auto num_frames = buffer[current_output].trace.num_frames;
    for (int frame_num = 0; frame_num < num_frames; ++frame_num) {
        memset(&(fb[frame_num]), 0, sizeof(JVMPI_CallFrame));
    }
    buffer[current_output].info = nullptr;

    // ensure that the record is ready to be written to
    buffer[current_output].is_committed.store(UNCOMMITTED, std::memory_order_release);
    // Signal that you've finished reading the record
    output.store(advance(current_output), std::memory_order_relaxed);

    return true;
}

size_t CircularQueue::advance(size_t index) const {
    return (index + 1) % Capacity;
}
