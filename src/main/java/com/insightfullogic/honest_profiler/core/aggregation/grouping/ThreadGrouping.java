package com.insightfullogic.honest_profiler.core.aggregation.grouping;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;

/**
 * A ThreadGrouping describes how a collection of {@link LeanThreadNode}s describing thread-level aggregations can be
 * partitioned for aggregation. The grouping maps each {@link LeanThreadNode} to a String key, and
 * {@link LeanThreadNode}s with the same key will be aggregated together.
 * <p>
 * Every ThreadGrouping contains a name for front-end display purposes, and wraps a {@link Function} which maps an a
 * {@link LeanThreadNode} to the String key.
 */
public enum ThreadGrouping implements Function<LeanThreadNode, String>
{
    /**
     * Group all threads together into a single group.
     */
    ALL_TOGETHER("All threads", node -> "All Threads"),
    /**
     * Group threads by Thread name.
     */
    BY_NAME("By name", node -> node.getThreadInfo() == null || node.getThreadInfo().getName() == null || node.getThreadInfo().getName().isEmpty() ? "Unknown Thread(s)" : node.getThreadInfo().getName()),
    /**
     * Group threads by Thread Id. This is more specific than by name, since several threads in aggregations can have
     * the same name (e.g. in Diffs).
     */
    BY_ID("By ID", node -> node.getThreadInfo() == null ? "Unknown Thread <Unknown ID>" : node.getThreadInfo().getIdentification());

    // Instance Properties

    private String name;
    private Function<LeanThreadNode, String> function;

    // Instance Constructors

    private ThreadGrouping(String name, Function<LeanThreadNode, String> function)
    {
        this.name = name;
        this.function = function;
    }

    // Function Implementation

    @Override
    public String apply(LeanThreadNode node)
    {
        return function.apply(node);
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
