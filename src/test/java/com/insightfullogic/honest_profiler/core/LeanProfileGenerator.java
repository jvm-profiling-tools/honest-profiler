package com.insightfullogic.honest_profiler.core;

import static java.util.Arrays.asList;
import static java.util.Collections.reverse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.collector.lean.LeanLogCollector;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo;
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
            MethodInfo info = currentProfile.getMethodInfoMap().get(method.getMethodId());
            assertEquals("Method Id mismatch", method.getMethodId(), info.getMethodId());
            assertEquals("Method Name mismatch", method.getMethodName(), info.getMethodName());
            assertEquals("Method Class Name mismatch", method.getClassName(), info.getClassName());
            assertEquals("Method File Name mismatch", method.getFileName(), info.getFileName());
        });
    }

    public void assertContains(ThreadMeta... threads)
    {
        asList(threads).forEach(thread ->
        {
            ThreadInfo info = currentProfile.getThreadInfoMap().get(thread.getThreadId());
            assertEquals("Thread Id mismatch", thread.getThreadId(), info.getId());
            assertEquals("Thread Name mismatch", thread.getThreadName(), info.getName());
        });
    }

    public void assertMethodMapSizeEquals(int size)
    {
        assertEquals("MethodInfoMap size wrong", size, currentProfile.getMethodInfoMap().size());
    }

    public void assertThreadMapSizeEquals(int size)
    {
        assertEquals("ThreadInfoMap size wrong", size, currentProfile.getThreadInfoMap().size());
    }

    public void assertProfileThreadCountEquals(int size)
    {
        assertEquals("Wrong Thread count in Profile", size, currentProfile.getThreads().size());
    }

    public void assertProfileContainsThread(long id)
    {
        assertNotNull("Thread not found in Profile", currentProfile.getThreads().get(id));
    }

    public void assertProfileContainsStack(long threadId, StackFrame... stack)
    {
        LeanNode current = currentProfile.getThreads().get(threadId);
        assertNotNull("Thread not found in Profile", current);

        int level = 0;

        List<StackFrame> frames = asList(stack);
        reverse(frames);

        for (StackFrame frame : frames)
        {
            level++;
            Optional<LeanNode> child = current.getChildren().stream()
                .filter(node -> node.getFrame().getMethodId() == frame.getMethodId()).findFirst();
            assertTrue("Descendant at level " + level + " not found", child.isPresent());
            current = child.get();
            assertNodeRepresentsFrame(current, frame);
        }
    }

    public void assertNodeRepresentsFrame(LeanNode node, StackFrame stackFrame)
    {
        FrameInfo info = node.getFrame();
        assertEquals(stackFrame.getMethodId(), info.getMethodId());
        assertEquals(stackFrame.getLineNumber(), info.getLineNr());
        assertEquals(stackFrame.getBci(), info.getBci());
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
