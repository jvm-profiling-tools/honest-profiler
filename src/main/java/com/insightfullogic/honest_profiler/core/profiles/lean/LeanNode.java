package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.util.Collection;
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
    private final FrameInfo frame;
    private final NumericInfo data;
    private LeanNode parent;
    private final Map<FrameInfo, LeanNode> childMap;

    public LeanNode(FrameInfo frame, LeanNode parent)
    {
        this.frame = frame;
        data = new NumericInfo();
        this.parent = parent;
        childMap = new HashMap<>();
    }

    public LeanNode(FrameInfo frame, long nanos, LeanNode parent)
    {
        this.frame = frame;
        data = new NumericInfo(nanos);
        this.parent = parent;
        childMap = new HashMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param source the source SlimNode which is being copied
     */
    private LeanNode(LeanNode source, LeanNode newParent)
    {
        this.frame = source.frame;
        this.data = source.data.copy();
        this.parent = newParent;
        this.childMap = new HashMap<>();
        source.childMap.forEach((key, value) -> this.childMap.put(key.copy(), value.copy(this)));
    }

    public FrameInfo getFrame()
    {
        return frame;
    }

    public NumericInfo getData()
    {
        return data;
    }

    public LeanNode getParent()
    {
        return parent;
    }

    public Map<FrameInfo, LeanNode> getChildMap()
    {
        return childMap;
    }

    public Collection<LeanNode> getChildren()
    {
        return childMap.values();
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

        return childMap.compute(
            child,
            (k, v) -> v == null
                ? (last ? new LeanNode(child, nanos, this) : new LeanNode(child, this))
                : (last ? v.updateSelf(nanos) : v));
    }

    /**
     * Copy method for the top of the tree, which has no parent.
     *
     * @return a copy of this object
     */
    public LeanNode copy()
    {
        return new LeanNode(this, null);
    }

    /**
     * Copy method for children.
     *
     * @param newParent the new parent for the children
     * @return a copy of this object
     */
    public LeanNode copy(LeanNode newParent)
    {
        return new LeanNode(this, newParent);
    }

    private LeanNode updateSelf(long nanos)
    {
        data.update(nanos, true);
        return this;
    }

    @Override
    public String toString()
    {
        return "LN [" + frame + ":" + data + "]";
    }

    public String toDeepString(int level, Map<Long, MethodInfo> methodMap)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < level; i++)
        {
            result.append("  ");
        }
        result.append(toString()).append(" (")
            .append(frame == null ? "--" : methodMap.get(frame.getMethodId()).getFqmn())
            .append(")\n");
        childMap.values().forEach(child -> result.append(child.toDeepString(level + 1, methodMap)));
        return result.toString();
    }
}
