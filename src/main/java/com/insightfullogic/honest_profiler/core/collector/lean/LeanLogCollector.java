package com.insightfullogic.honest_profiler.core.collector.lean;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.profiles.lean.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;
import com.insightfullogic.honest_profiler.core.profiles.lean.MethodInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.ThreadInfo;

/**
 * Collector which emits {@link LeanProfile}s.
 */
public class LeanLogCollector implements LogEventListener, ProfileSource
{
    // Class Properties

    private static final long SECONDSTONANOS = 1000 * 1000 * 1000;

    // Instance Properties

    private final LeanProfileListener listener;

    // Maps method ids to MethodInfo objects.
    private final Map<Long, MethodInfo> methodMap;
    // Maps thread ids to ThreadInfo objects.
    private final Map<Long, ThreadInfo> threadMap;
    // Maps thread ids to the profile trees for the threads. The root contains
    // the Thread-level data, anything below are stackframe-level data.
    private final Map<Long, LeanNode> threadData;

    private Deque<StackFrame> stackTrace;

    // Seconds and nanos as reported by the last TraceStart received.
    private long prevSeconds;
    private long prevNanos;

    // Difference in ns between the previous and the current TraceStart.
    private long nanosSpent;

    // Properties related to profile emission.
    private AtomicBoolean profileRequested;

    // Property for internal use. When a TraceStart is received, this is set to
    // the LeanNode corresponding to the reported thread id. When stackframes
    // are processed, it is replaced by the node representing the processed
    // stackframe.
    private LeanNode currentNode;

    private boolean dirty;
    private LeanProfile cachedProfile;

    // Instance Constructors

    public LeanLogCollector(final LeanProfileListener listener)
    {
        this.listener = listener;

        methodMap = new HashMap<>();
        threadMap = new HashMap<>();
        threadData = new HashMap<>();

        stackTrace = new ArrayDeque<>();

        currentNode = null;

        profileRequested = new AtomicBoolean(false);
    }

    // ProfileSource Implementation

    /**
     * Set a flag from any thread, which will cause an updated profile to be emitted as soon as possible.
     */
    @Override
    public void requestProfile()
    {
        profileRequested.set(true);
    }

    // LogEventListener Implementation

    /**
     * On the very first {@link TraceStart}, dirty should be false, so nothing will be emitted.
     */
    @Override
    public void handle(TraceStart traceStart)
    {
        updateTime(traceStart.getTraceEpoch(), traceStart.getTraceEpochNano());
        collectThreadDump();

        currentNode = threadData
            .computeIfAbsent(traceStart.getThreadId(), v -> new LeanNode(null, null));

        emitProfileIfNeeded();
        stackTrace.clear();
    }

    @Override
    public void handle(StackFrame stackFrame)
    {
        dirty = true;
        stackTrace.push(stackFrame);
    }

    @Override
    public void handle(Method newMethod)
    {
        dirty = true;
        methodMap.putIfAbsent(newMethod.getMethodId(), new MethodInfo(newMethod));
        emitProfileIfNeeded();
    }

    @Override
    public void handle(ThreadMeta newThreadMeta)
    {
        dirty = true;
        threadMap.compute(
            newThreadMeta.getThreadId(),
            (k, v) -> v == null ? new ThreadInfo(newThreadMeta)
                : v.checkAndSetName(newThreadMeta.getThreadName()));
        emitProfileIfNeeded();
    }

    /**
     * If no ThreadStart occurred after the last stack frames were added, we obviously don't have an accurate
     * "nanosSpent", but reusing the last one should do fine as an approximation.
     */
    @Override
    public void endOfLog()
    {
        dirty = true;
        collectThreadDump();
        emitProfile();
    }

    // Helper Methods

    private void collectThreadDump()
    {
        while (!stackTrace.isEmpty())
        {
            collectStackFrame(stackTrace.pop());
        }
    }

    private void collectStackFrame(StackFrame stackFrame)
    {
        currentNode = currentNode
            .update(nanosSpent, new FrameInfo(stackFrame), stackTrace.isEmpty());
    }

    /**
     * Calculates the number of ns spent between the previous {@link TraceStart} and the current one. After the first
     * {@link TraceStart} nanosSpent will still be 0.
     *
     * @param newSeconds seconds reported in the current TraceStart
     * @param newNanos nanoSeconds reported in the current TraceStart
     */
    private void updateTime(long newSeconds, long newNanos)
    {
        // The timestamp is absolute, so the very first time these calculations
        // are meaningless. And if the log doesn't contain timestamps,
        // prevSeconds will always be zero, so we avoid the calculations.
        if (prevSeconds > 0)
        {
            long secondsDiff = newSeconds - prevSeconds;
            long nanosDiff = newNanos - prevNanos;

            nanosSpent = (secondsDiff * SECONDSTONANOS) + nanosDiff;
        }

        prevSeconds = newSeconds;
        prevNanos = newNanos;
    }

    private void emitProfileIfNeeded()
    {
        if (profileRequested.getAndSet(false))
        {
            emitProfile();
        }
    }

    private void emitProfile()
    {
        if (dirty)
        {
            cachedProfile = new LeanProfile(methodMap, threadMap, threadData);
            dirty = false;
        }

        listener.accept(cachedProfile);
    }
}
