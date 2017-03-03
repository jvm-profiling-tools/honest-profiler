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
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo;

/**
 * Collector which emits {@link LeanProfile}s, based on a request mechanism.
 * <p>
 * A {@link LeanProfile} will only be emitted when requested, or when the end of the log file is reached. When the final
 * {@link LeanProfile} has been emitted after a log file has been processed, requests no longer have any effect.
 * <p>
 * As long as no stacks have been received, nothing will be emitted.
 */
public class LeanLogCollector implements LogEventListener, ProfileSource
{
    // Class Properties

    private static final long SECONDS_TO_NANOS = 1000 * 1000 * 1000;

    // Instance Properties

    private final LeanProfileListener listener;

    // Maps method ids to MethodInfo objects.
    private final Map<Long, MethodInfo> methodMap;
    // Maps thread ids to ThreadInfo objects.
    private final Map<Long, ThreadInfo> threadMap;
    // Maps thread ids to the profile trees for the threads. The root contains the Thread-level data, anything below are
    // stackframe-level data.
    private final Map<Long, LeanThreadNode> threadData;

    private Deque<StackFrame> stackTrace;

    // Seconds and nanos as reported by the last TraceStart received.
    private long prevSeconds;
    private long prevNanos;

    // Difference in ns between the previous and the current TraceStart.
    private long nanosSpent;

    // Indicates whether a profile was requested and should be emitted.
    private AtomicBoolean profileRequested;

    // Property for internal use. When a TraceStart is received, this is set to the LeanThreadNode corresponding to the
    // reported thread id. When stackframes are processed, it is replaced by the node representing the processed
    // stackframe.
    private LeanNode currentNode;

    // Indicates whether at least one stack has been processed. If not, no profile will be emitted.
    private boolean empty = true;

    // Instance Constructors

    /**
     * Constructor which sets the {@link LeanProfileListener} to which the {@link LeanProfile}s will be emitted.
     * <p>
     * @param listener the {@link LeanProfileListener} which will receive any emitted {@link LeanProfile}s
     */
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
     * Processes a {@link TraceStart}, i.e. the indication that a new stack will be coming in. The time is updated, and
     * the previously collected stack (if any) is processed. Then, the top-level {@link LeanThreadNode} is put in place
     * (possibly after creating it) using the thread id in the {@link TraceStart}.
     * <p>
     * A profile will be emitted if requested.
     */
    @Override
    public void handle(TraceStart traceStart)
    {
        updateTime(traceStart.getTraceEpoch(), traceStart.getTraceEpochNano());
        collectThreadDump();

        currentNode = threadData
            .computeIfAbsent(traceStart.getThreadId(), v -> new LeanThreadNode());

        emitProfileIfNeeded();

        // The stacktrace should be empty anyway, given the collectThreadDump() logic, so got rid of this.
        // stackTrace.clear();
    }

    /**
     * Processes a {@link StackFrame} by pushing it onto the {@link Deque}.
     */
    @Override
    public void handle(StackFrame stackFrame)
    {
        stackTrace.push(stackFrame);
    }

    /**
     * Processes a {@link Method} which maps the method id to method information by putting the information into the
     * method map if it isn't there yet.
     */
    @Override
    public void handle(Method newMethod)
    {
        methodMap.putIfAbsent(newMethod.getMethodId(), new MethodInfo(newMethod));
        emitProfileIfNeeded();
    }

    /**
     * Processes a {@link ThreadMeta} which maps the thread id to thread information by putting the information into the
     * method map if it isn't there yet, or updating the existing information using the thread name.
     * <p>
     * Sometimes several {@link ThreadMeta}s are received for the same thread id, but only the first contains the actual
     * thread name, which is why the update mechanism is in place.
     */
    @Override
    public void handle(ThreadMeta newThreadMeta)
    {
        threadMap.compute(
            newThreadMeta.getThreadId(),
            (k, v) -> v == null ? new ThreadInfo(newThreadMeta) : v.checkAndSetName(newThreadMeta));
        emitProfileIfNeeded();
    }

    /**
     * Processes the "end of log" event, received when the end of a log file is reached. If no ThreadStart occurred
     * after the last stack frames were added, we obviously don't have an accurate "nanosSpent", but reusing the last
     * one should do fine as an approximation.
     */
    @Override
    public void endOfLog()
    {
        collectThreadDump();
        emitProfile();
    }

    // Helper Methods

    /**
     * Processes the {@link StackFrame}s in the current stack.
     */
    private void collectThreadDump()
    {
        // Slightly nicer IMHO than executing the "empty = false" inside the while loop.
        if (stackTrace.isEmpty())
        {
            return;
        }

        while (!stackTrace.isEmpty())
        {
            collectStackFrame(stackTrace.pop());
        }

        empty = false;
    }

    /**
     * Processes a single {@link StackFrame}. The currentNode is the parent {@link LeanNode} which either represents the
     * parent {@link StackFrame} or, if this is the first {@link StackFrame} from the internal {@link Deque}, the parent
     * {@link LeanThreadNode} representing the thread for which the stack was received. The {@link StackFrame}
     * information will be aggregated into the children of the currentNode, and the resulting {@link LeanNode} becomes
     * the currentNode, acting as parent for the next {@link StackFrame} which will be processed, if there are any left
     * in the {@link Deque}.
     * <p>
     * @param stackFrame the {@link StackFrame} to be added as a child to the current {@link LeanNode}
     */
    private void collectStackFrame(StackFrame stackFrame)
    {
        currentNode = currentNode.add(nanosSpent, new FrameInfo(stackFrame), stackTrace.isEmpty());
    }

    /**
     * Calculates the number of ns spent between the previous {@link TraceStart} and the current one. After the first
     * {@link TraceStart} nanosSpent will still be 0.
     * <p>
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

            nanosSpent = (secondsDiff * SECONDS_TO_NANOS) + nanosDiff;
        }

        prevSeconds = newSeconds;
        prevNanos = newNanos;
    }

    /**
     * Emit a {@link LeanProfile} if a request is outstanding.
     */
    private void emitProfileIfNeeded()
    {
        if (profileRequested.getAndSet(false))
        {
            emitProfile();
        }
    }

    /**
     * Emit a {@link LeanProfile} if at least one full stack was processed.
     */
    private void emitProfile()
    {
        if (!empty)
        {
            listener.accept(new LeanProfile(methodMap, threadMap, threadData));
        }
    }
}
