package com.insightfullogic.honest_profiler.core.aggregation.grouping;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;

public enum ThreadGrouping implements Function<LeanThreadNode, String>
{

    ALL_THREADS_TOGETHER(node -> "All Threads"),
    THREADS_BY_NAME(node -> node.getThreadInfo() == null
        || node.getThreadInfo().getName() == null
        || node.getThreadInfo().getName().isEmpty() ? "Unknown Thread(s)"
            : node.getThreadInfo().getName()),
    THREADS_BY_ID(node -> node.getThreadInfo().getIdentification());

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
