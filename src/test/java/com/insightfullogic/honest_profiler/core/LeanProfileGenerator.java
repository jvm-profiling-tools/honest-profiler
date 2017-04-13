package com.insightfullogic.honest_profiler.core;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.insightfullogic.honest_profiler.core.collector.lean.LeanLogCollector;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo;

public class LeanProfileGenerator implements LeanProfileListener
{
    // Instance Properties

    private LeanLogCollector collector;

    private LeanProfile previousProfile;
    private LeanProfile currentProfile;
    private int nrChanges;

    // Instance Constructors

    public LeanProfileGenerator()
    {
        reset();
    }

    public LeanProfileGenerator(boolean issueInitialRequest)
    {
        if (issueInitialRequest)
        {
            resetAndRequest();
        }
        else
        {
            reset();
        }
    }

    // Instance Accessors

    public LeanProfile getProfile()
    {
        return currentProfile;
    }

    // LeanProfileListener Implementation

    @Override
    public void accept(LeanProfile newProfile)
    {
        previousProfile = currentProfile;
        currentProfile = newProfile;
        nrChanges++;
    }

    // Assertions

    public void assertNothingEmitted()
    {
        assertNull("No profile should have been emitted.", currentProfile);
        assertNull("No profile should have been emitted.", previousProfile);
        assertEquals("No profile should have been emitted.", 0, nrChanges);
    }

    public void assertSingleEmission()
    {
        assertNotNull("A profile should have been emitted.", currentProfile);
        assertNull("Only one profile should have been emitted.", previousProfile);
        assertEquals("Exactly one profile should have been emitted.", 1, nrChanges);
    }

    public void assertContains(Method... methods)
    {
        asList(methods).forEach(method ->
        {
            MethodInfo actual = currentProfile.getMethodInfoMap().get(method.getMethodId());
            assertEquals("Method Id mismatch", method.getMethodId(), actual.getMethodId());
            assertEquals("Method Name mismatch", method.getMethodName(), actual.getMethodName());
            assertEquals(
                "Method Class Name mismatch",
                method.getClassName(),
                actual.getClassName());
            assertEquals("Method File Name mismatch", method.getFileName(), actual.getFileName());
        });
    }

    public void assertContains(ThreadMeta... threads)
    {
        asList(threads).forEach(thread ->
        {
            ThreadInfo actual = currentProfile.getThreadInfoMap().get(thread.getThreadId());
            assertEquals("Thread Id mismatch", thread.getThreadId(), actual.getId());
            assertEquals("Thread Name mismatch", thread.getThreadName(), actual.getName());
        });
    }

    public void assertMethodMapSizeEquals(int size)
    {
        assertEquals(
            "MethodInfoMap contains wrong number of items",
            size,
            currentProfile.getMethodInfoMap().size());
    }

    public void assertThreadMapSizeEquals(int size)
    {
        assertEquals(
            "ThreadInfoMap contains wrong number of items",
            size,
            currentProfile.getThreadInfoMap().size());
    }

    // Initialization

    public void resetAndRequest()
    {
        reset();
        collector.requestProfile();
    }

    public void reset()
    {
        previousProfile = null;
        currentProfile = null;
        nrChanges = 0;

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

    public void handle(Object... events)
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
