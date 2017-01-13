package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.parser.TraceStart;

/**
 * Tree node which records stacktrace information. The tree root contains the
 * numerical data at thread-level, and has as unique child the root method of
 * that thread. Any other node contains numerical data about its associated
 * stack frame, and has as children the nodes representing stack frames called
 * at some time from that location.
 */
public class LeanNode
{
    private final NumericInfo data;
    private final Map<FrameInfo, LeanNode> children;

    public LeanNode()
    {
        data = new NumericInfo();
        children = new HashMap<>();
    }

    public LeanNode(long nanos)
    {
        data = new NumericInfo(nanos);
        children = new HashMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param source the source SlimNode which is being copied
     */
    private LeanNode(LeanNode source)
    {
        this.data = source.data.copy();
        this.children = new HashMap<>();
        source.children.forEach((key, value) -> this.children.put(key.copy(), value.copy()));
    }

    /**
     * The update is called from the LogCollector, on the parent with the next
     * (potentially last) child from the trace.
     *
     * @param nanos the number of ns between the {@link TraceStart} preceding
     *            the stackTrace and the {@link TraceStart} following it
     * @param child the child {@link FrameInfo} of the current Node
     * @param last a boolean indicating if the child is last in the trace
     * @return this node
     */
    public LeanNode update(long nanos, FrameInfo child, boolean last)
    {
        data.update(nanos, false);

        return children.compute(
            child,
            (k, v) -> v == null ? (last ? new LeanNode(nanos) : new LeanNode())
                : (last ? v.updateSelf(nanos) : v));
    }

    public LeanNode copy()
    {
        return new LeanNode(this);
    }

    private LeanNode updateSelf(long nanos)
    {
        data.update(nanos, true);
        return this;
    }
}
