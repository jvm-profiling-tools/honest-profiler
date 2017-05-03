package com.insightfullogic.honest_profiler.framework.generator;

import static java.util.Arrays.asList;
import static java.util.Collections.reverse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo;
import com.insightfullogic.honest_profiler.framework.LeanLogCollectorDriver;

public class LeanProfileGenerator extends LeanLogCollectorDriver
{
    // Instance Properties

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

    public LeanNode getNode(long threadId, StackFrame... stack)
    {
        LeanNode current = currentProfile.getThreads().get(threadId);
        List<StackFrame> frames = asList(stack);
        reverse(frames);
        for (StackFrame frame : frames)
        {
            Optional<LeanNode> child = current.getChildren().stream()
                .filter(node -> node.getFrame().getMethodId() == frame.getMethodId()).findFirst();
            current = child.get();
        }
        return current;
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
            Optional<LeanNode> child = current.getChildren().stream().filter(
                node -> node.getFrame().getMethodId() == frame.getMethodId()
                    && node.getFrame().getLineNr() == frame.getLineNumber()
                    && node.getFrame().getBci() == frame.getBci())
                .findFirst();
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

    public void assertSelfCountEquals(int selfCount, long threadId, StackFrame... stack)
    {
        NumericInfo info = getNode(threadId, stack).getData();
        assertEquals("Wrong Self Count", selfCount, info.getSelfCnt());
    }

    public void assertTotalCountEquals(int totalCount, long threadId, StackFrame... stack)
    {
        NumericInfo info = getNode(threadId, stack).getData();
        assertEquals("Wrong Total Count", totalCount, info.getTotalCnt());
    }

    public void assertCountsEqual(int selfCount, int totalCount, long threadId, StackFrame... stack)
    {
        NumericInfo info = getNode(threadId, stack).getData();
        assertEquals("Wrong Self Count", selfCount, info.getSelfCnt());
        assertEquals("Wrong Total Count", totalCount, info.getTotalCnt());
    }

    public void assertSelfTimeEquals(long selfTime, long threadId, StackFrame... stack)
    {
        NumericInfo info = getNode(threadId, stack).getData();
        assertEquals("Wrong Self Time", BigInteger.valueOf(selfTime), info.getSelfTime());
    }

    public void assertTotalTimeEquals(long totalTime, long threadId, StackFrame... stack)
    {
        NumericInfo info = getNode(threadId, stack).getData();
        assertEquals("Wrong Total Time", BigInteger.valueOf(totalTime), info.getTotalTime());
    }

    public void assertTimesEqual(long selfTime, long totalTime, long threadId, StackFrame... stack)
    {
        NumericInfo info = getNode(threadId, stack).getData();
        assertEquals("Wrong Self Time", BigInteger.valueOf(selfTime), info.getSelfTime());
        assertEquals("Wrong Total Time", BigInteger.valueOf(totalTime), info.getTotalTime());
    }
}
