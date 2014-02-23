package com.insightfullogic.honest_profiler.log;


import java.util.Objects;

public final class TraceStart implements LogEvent {

    private final int numberOfFrames;
    private final long threadId;

    public TraceStart(int numberOfFrames, long threadId) {
        this.numberOfFrames = numberOfFrames;
        this.threadId = threadId;
    }

    public int getNumberOfFrames() {
        return numberOfFrames;
    }

    public long getThreadId() {
        return threadId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraceStart that = (TraceStart) o;
        return Objects.equals(numberOfFrames, that.numberOfFrames)
            && Objects.equals(threadId, that.threadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfFrames, threadId);
    }

    @Override
    public void accept(IEventListener listener) {
        listener.handle(this);
    }

    @Override
    public String toString() {
        return "TraceStart{" +
                "numberOfFrames=" + numberOfFrames +
                ", threadId=" + threadId +
                '}';
    }
}
