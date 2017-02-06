package com.insightfullogic.honest_profiler.core.aggregation.grouping;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;

public enum ThreadGrouping implements Function<LeanThreadNode, String>
{

    ALL_TOGETHER("All threads", node -> "All Threads"),
    BY_NAME("By name",
        node -> node.getThreadInfo() == null
            || node.getThreadInfo().getName() == null
            || node.getThreadInfo().getName().isEmpty() ? "Unknown Thread(s)"
                : node.getThreadInfo().getName()),
    BY_ID("By ID", node -> node.getThreadInfo() == null ? "Unknown Thread <Unknown ID>"
        : node.getThreadInfo().getIdentification());

    private String name;
    private Function<LeanThreadNode, String> function;

    private ThreadGrouping(String name, Function<LeanThreadNode, String> function)
    {
        this.name = name;
        this.function = function;
    }

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
