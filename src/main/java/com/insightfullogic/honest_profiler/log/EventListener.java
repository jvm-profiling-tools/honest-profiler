package com.insightfullogic.honest_profiler.log;

public interface EventListener {

    public void handle(TraceStart traceStart);

    public void handle(StackFrame stackFrame);

    public void handle(Method newMethod);

    public void endOfLog();

}
