package com.insightfullogic.honest_profiler.core.aggregation.grouping;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;

public enum ThreadGrouping implements Function<LeanThreadNode, String>
{

    ALL_TOGETHER(node -> "All Threads"),
    BY_NAME(node -> node.getThreadInfo() == null
        || node.getThreadInfo().getName() == null
        || node.getThreadInfo().getName().isEmpty() ? "Unknown Thread(s)"
            : node.getThreadInfo().getName()),
    BY_ID(node -> node.getThreadInfo() == null ? "Unknown Thread <Unknown ID>"
        : node.getThreadInfo().getIdentification());

    private Function<LeanThreadNode, String> function;

    private ThreadGrouping(Function<LeanThreadNode, String> function)
    {
        this.function = function;
    }

    @Override
    public String apply(LeanThreadNode node)
    {
        return function.apply(node);
    }
}
