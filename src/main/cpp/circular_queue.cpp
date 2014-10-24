#include "circular_queue.h"
#include <iostream>

void safe_reset(void *start, size_t size) {
    char *base = reinterpret_cast<char *>(start);
    char *end = base + size;
    for (char *p = base; p < end; p++) {
        *p = 0;
    }
}

bool CircularQueue::push(const JVMPI_CallTrace &item) {
    size_t currentInput;
    size_t nextInput;
    do {
        currentInput = input.load();
        nextInput = advance(currentInput);
        if (output.load() == nextInput) {
            return false;
        }
        // TODO: have someone review the memory ordering constraints
    } while (!input.compare_exchange_strong(currentInput, nextInput,
            std::memory_order_release,
            std::memory_order_relaxed));
    write(item, currentInput);

    return true;
}

// Unable to use memcpy inside the push method because its not async-safe
void CircularQueue::write(const JVMPI_CallTrace &trace, const size_t slot) {
    JVMPI_CallFrame *fb = frame_buffer_[slot];
    for (int frame_num = 0; frame_num < trace.num_frames; ++frame_num) {
        // Make sure the padding is all set to 0.
        safe_reset(&(fb[frame_num]), sizeof(JVMPI_CallFrame));

        fb[frame_num].lineno = trace.frames[frame_num].lineno;
        fb[frame_num].method_id = trace.frames[frame_num].method_id;
    }

    buffer[slot].frames = fb;
    buffer[slot].num_frames = trace.num_frames;
    buffer[slot].env_id = trace.env_id;
}

bool CircularQueue::pop() {
    const auto current_output = output.load();

    // queue is empty
    if (current_output == input.load()) {
        return false;
    }

    listener_.record(buffer[current_output]);
    output.store(advance(current_output));
    return true;
}

size_t CircularQueue::advance(size_t index) const {
    return (index + 1) % Capacity;
}
