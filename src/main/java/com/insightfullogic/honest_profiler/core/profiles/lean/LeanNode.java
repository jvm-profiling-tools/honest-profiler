package com.insightfullogic.honest_profiler.core.profiles.lean;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo;

/**
 * LeanNode is a tree node recording the aggregation of the numerical data for a thread or a frame inside a
 * {@link LeanProfile}.
 *
 * Its children represent frames that were directly called from the represented frame or thread at some point.
 *
 * It also has a unique id, needed in some case to prevent duplicate aggregation.
 */
public class LeanNode
{
    // Class Properties

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final int id;
    private final FrameInfo frame;
    private final NumericInfo data;
    private LeanNode parent;
    private final Map<FrameInfo, LeanNode> childMap;

    /**
     * "Non-self constructor" which sets the {@link FrameInfo} and the parent LeanNode, used for constructing a new
     * frame LeanNode which is has no self time (i.e. the stack trace sample still has more descendant frames), or a
     * {@link LeanThreadNode}.
     *
     * @param frame the {@link FrameInfo} for this LeanNode
     * @param parent the parent LeanNode
     */
    protected LeanNode(FrameInfo frame, LeanNode parent)
    {
        id = ID_GENERATOR.getAndIncrement();

        this.frame = frame;
        // The use of the NumericInfo constructor sets all values, such as sample count, to 0. This is OK because this
        // LeanNode constructor is only called for frames (or threads) which are known to have at least one descendant
        // in the stack trace sample being processed. When the child is processed, the add() method will update the
        // values of this LeanNode.
        data = new NumericInfo();
        this.parent = parent;
        childMap = new HashMap<>();
    }

    /**
     * "Self constructor" for a LeanNode associated with the "bottom frame" of a stack trace, i.e. the one for which
     * self time is recorded.
     *
     * @param frame the {@link FrameInfo} for this LeanNode
     * @param nanos the self time associated with the frame
     * @param parent the parent LeanNode
     */
    private LeanNode(FrameInfo frame, long nanos, LeanNode parent)
    {
        id = ID_GENERATOR.getAndIncrement();

        this.frame = frame;
        data = new NumericInfo(nanos);
        this.parent = parent;
        childMap = new HashMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param source the source LeanNode which is being copied
     * @param newParent the parent of the copy (which itself generally is a copy)
     */
    protected LeanNode(LeanNode source, LeanNode newParent)
    {
        this.id = source.id;

        this.frame = source.frame;
        this.data = source.data.copy();
        this.parent = newParent;
        this.childMap = new HashMap<>();
        // The FrameInfo key is an immutable object, no need to copy it.
        source.childMap.forEach((key, value) -> this.childMap.put(key, new LeanNode(value, this)));
    }

    // Instance Accessors

    /**
     * Returns the unique id of this LeanNode.
     *
     * @return the unique id of this LeanNode
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the {@link FrameInfo} associated with this LeanNode, or <null> if this LeanNode represents a thread and
     * is in fact a {@link LeanThreadNode}.
     *
     * @return the {@link FrameInfo} associated with this LeanNode, or <null> if this LeanNode represents a thread
     */
    public FrameInfo getFrame()
    {
        return frame;
    }

    /**
     * Returns the {@link NumericInfo} for this LeanNode
     *
     * @return the {@link NumericInfo} for this LeanNode
     */
    public NumericInfo getData()
    {
        return data;
    }

    /**
     * Returns the parent of this LeanNode
     *
     * @return the parent of this LeanNode
     */
    public LeanNode getParent()
    {
        return parent;
    }

    /**
     * Returns a {@link Collection} containing the children of this LeanNode.
     *
     * @return a {@link Collection} containing the children of this LeanNode
     */
    public Collection<LeanNode> getChildren()
    {
        return childMap.values();
    }

    /**
     * Returns a boolean indicating whether this node represents a thread and is a {@link LeanThreadNode}.
     *
     * @return a boolean indicating whether this node represents a thread and is a {@link LeanThreadNode}
     */
    public boolean isThreadNode()
    {
        return false;
    }

    // Aggregation Methods

    /**
     * Aggregates the information from a child {@link StackFrame} into the children of this LeanNode, updating the total
     * data for this LeanNode in the process.
     *
     * The add method is called from the LogCollector, on the parent with the next (potentially last) child from the
     * trace.
     *
     * @param nanos the number of nanoseconds between the {@link TraceStart} preceding the stack trace sample and the
     *            {@link TraceStart} following it
     * @param child the child {@link FrameInfo} of the current LeanNode
     * @param last a boolean indicating if the child is the last in the stack trace sample
     * @return this node
     */
    public LeanNode add(long nanos, FrameInfo child, boolean last)
    {
        // Non-self add, which updates total time and sample count only.
        data.add(nanos, false);

        return childMap.compute(
            child,
            (k, v) ->
            // Create a new LeanNode if no children have been recorded for this FrameInfo
            v == null ? (last ?
            // New child is the last in stack, use "Self constructor".
                new LeanNode(child, nanos, this)
                // New child is not the last in stack, use "Non-self constructor".
                : new LeanNode(child, this))
                // Aggregate the information into an existing child
                : (last ?
                // Child is last in stack, use the "self add" on existing child
                    v.addSelf(nanos) :
                    // Child is not last in stack, return existing child (its "total" numbers will be updated when its
                    // child is added)
                    v));
    }

    /**
     * Aggregate the self and total data in the {@link NumericInfo} for this LeanNode.
     *
     * @param nanos the self time for the frame
     * @return this object
     */
    private LeanNode addSelf(long nanos)
    {
        data.add(nanos, true);
        return this;
    }

    // Tree-related Methods

    /**
     * Returns a {@link Stream} containing this LeanNode and all its descendants.
     *
     * @return a {@link Stream} containing this LeanNode and all its descendants
     */
    public Stream<LeanNode> flatten()
    {
        return concat(of(this), childMap.values().stream().flatMap(LeanNode::flatten));
    }

    // Debug Methods

    /**
     * Returns a String representation of the frame represented by this node, including descendant information, indented
     * according to the specified indentation level.
     *
     * @param level the level of the indentation
     * @param methodMap the {@link Map} with the {@link MethodInfo} objects used to print the method info in the frame
     * @return a String containing the frame information of this LeanNode and its descendants.
     */
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

    // Object Implementation

    @Override
    public boolean equals(Object other)
    {
        return other instanceof LeanNode && ((LeanNode)other).id == id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return "LN [" + frame + ":" + data + "]";
    }
}
