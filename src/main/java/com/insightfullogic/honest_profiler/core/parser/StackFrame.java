package com.insightfullogic.honest_profiler.core.parser;


import java.util.Objects;

public final class StackFrame implements LogEvent {

    private final int lineNumber;
    private final long methodId;

    public StackFrame(int lineNumber, long methodId) {
        this.lineNumber = lineNumber;
        this.methodId = methodId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public long getMethodId() {
        return methodId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StackFrame that = (StackFrame) o;
        return Objects.equals(lineNumber, that.lineNumber)
            && Objects.equals(methodId, that.methodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, methodId);
    }

    @Override
    public void accept(EventListener listener) {
        listener.handle(this);
    }

    @Override
    public String toString() {
        return "StackFrame{" +
                "lineNumber=" + lineNumber +
                ", methodId=" + methodId +
                '}';
    }

}
