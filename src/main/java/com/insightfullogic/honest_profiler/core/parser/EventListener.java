package com.insightfullogic.honest_profiler.core.parser;

public interface EventListener {

    public void startOfLog(boolean continuous);

    public void handle(TraceStart traceStart);

    public void handle(StackFrame stackFrame);

    public void handle(Method newMethod);

    public void endOfLog();

}
