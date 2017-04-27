package com.insightfullogic.honest_profiler.framework;

import static java.util.Arrays.asList;

import com.insightfullogic.honest_profiler.core.collector.lean.LeanLogCollector;
import com.insightfullogic.honest_profiler.core.parser.LogEvent;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;

public abstract class LeanLogCollectorDriver implements LeanProfileListener
{
    // Instance Properties

    private LeanLogCollector collector;

    // LeanProfileListener Implementation

    @Override
    public abstract void accept(LeanProfile newProfile);

    // Initialization

    public void resetAndRequest()
    {
        reset();
        collector.requestProfile();
    }

    public void reset()
    {
        collector = new LeanLogCollector(this);
    }

    // Delegation for LogCollector

    public void requestProfile()
    {
        collector.requestProfile();
    }

    public void handle(TraceStart... traceStarts)
    {
        asList(traceStarts).forEach(collector::handle);
    }

    public void handle(StackFrame... stackFrames)
    {
        asList(stackFrames).forEach(collector::handle);
    }

    public void handle(Method... methods)
    {
        asList(methods).forEach(collector::handle);
    }

    public void handle(ThreadMeta... threadMetas)
    {
        asList(threadMetas).forEach(collector::handle);
    }

    public void handle(LogEvent... events)
    {
        asList(events).forEach(event ->
        {
            if (event instanceof TraceStart)
            {
                handle((TraceStart)event);
            }
            else if (event instanceof StackFrame)
            {
                handle((StackFrame)event);
            }
            else if (event instanceof Method)
            {
                handle((Method)event);
            }
            else if (event instanceof ThreadMeta)
            {
                handle((ThreadMeta)event);
            }
        });
    }

    public void endOfLog()
    {
        collector.endOfLog();
    }
}
