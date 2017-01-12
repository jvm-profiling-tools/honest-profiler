package com.insightfullogic.honest_profiler.core.collector.lean;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.profiles.lean.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.MethodInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;
import com.insightfullogic.honest_profiler.core.profiles.lean.ThreadInfo;

public class LeanLogCollector implements LogEventListener
{
    private static final long SECONDSTONANOS = 1000 * 1000 * 1000;

    private final LeanProfileListener listener;

    private final Map<Long, MethodInfo> methodMap;
    private final Map<Long, ThreadInfo> threadMap;
    private final Map<Long, LeanNode> threadData;

    private Deque<StackFrame> previousStack;
    private Deque<StackFrame> currentStack;

    private long prevSeconds;
    private long prevNanos;
    private long nanosSpent;

    private LeanNode currentNode;

    private boolean emitOnChange;

    public LeanLogCollector(final LeanProfileListener listener, final boolean emitOnChange)
    {
        this.listener = listener;
        this.emitOnChange = emitOnChange;

        methodMap = new HashMap<>();
        threadMap = new HashMap<>();
        threadData = new HashMap<>();

        previousStack = new ArrayDeque<>();
        currentStack = new ArrayDeque<>();

        currentNode = null;
    }

    @Override
    public void handle(TraceStart traceStart)
    {
        currentNode = threadData.computeIfAbsent(traceStart.getThreadId(), v -> new LeanNode(0));
        updateTime(traceStart.getTraceEpoch(), traceStart.getTraceEpochNano());

        collectThreadDump();
        emitProfileIfNeeded();
        currentStack.clear();
    }

    @Override
    public void handle(StackFrame stackFrame)
    {
        currentStack.push(stackFrame);
    }

    private void collectThreadDump()
    {
        // Switch around the stacks, so the currentStack is empty again, and
        // what was collected can be processed.
        Deque<StackFrame> temp = currentStack;
        currentStack = previousStack;
        previousStack = temp;

        while (!previousStack.isEmpty())
        {
            collectStackFrame(previousStack.size() == 1, previousStack.pop());
        }
    }

    @Override
    public void handle(Method newMethod)
    {
        methodMap.putIfAbsent(newMethod.getMethodId(), new MethodInfo(newMethod));
        emitProfileIfNeeded();
    }

    @Override
    public void handle(ThreadMeta newThreadMeta)
    {
        threadMap.compute(
            newThreadMeta.getThreadId(),
            (k, v) -> v == null ? new ThreadInfo(newThreadMeta)
                : v.checkAndSetName(newThreadMeta.getThreadName()));
    }

    @Override
    /**
     * If no ThreadStart occurred after the last stack frames were added, we
     * obviously don't have an accurate "nanosSpent", but reusing the last one
     * should do fine as an approximation.
     */
    public void endOfLog()
    {
        collectThreadDump();
        emitProfile();
    }

    private void collectStackFrame(boolean self, StackFrame stackFrame)
    {
        currentNode = currentNode.update(nanosSpent, new FrameInfo(stackFrame));
    }

    private void updateTime(long newSeconds, long newNanos)
    {
        long secondsDiff = newSeconds - prevSeconds;
        long nanosDiff = newNanos - prevNanos;

        nanosSpent = (secondsDiff * SECONDSTONANOS) + nanosDiff;

        prevSeconds = newSeconds;
        prevNanos = newNanos;
    }

    private void emitProfileIfNeeded()
    {
        if (nanosSpent > 0 && emitOnChange)
        {
            emitProfile();
        }
    }

    private void emitProfile()
    {
        listener.accept(new LeanProfile(methodMap, threadMap, threadData));
    }
}
