package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.util.HashMap;
import java.util.Map;

/**
 * Alternative Profile which stores/collects all info only once. Any aggregation
 * is relegated to post-processing.
 */
public class LeanProfile
{
    private final Map<Long, MethodInfo> methodMap;
    private final Map<Long, ThreadInfo> threadMap;
    private final Map<Long, LeanNode> threadData;

    public LeanProfile(Map<Long, MethodInfo> methodMap,
                       Map<Long, ThreadInfo> threadMap,
                       Map<Long, LeanNode> threadData)
    {
        this.methodMap = new HashMap<>(methodMap);
        this.threadMap = new HashMap<>(threadMap);
        this.threadData = new HashMap<>();
        threadData.forEach((key, value) -> this.threadData.put(key, value.copy()));
    }

    public Map<Long, MethodInfo> getMethodMap()
    {
        return methodMap;
    }

    public Map<Long, ThreadInfo> getThreadMap()
    {
        return threadMap;
    }

    public Map<Long, LeanNode> getThreadData()
    {
        return threadData;
    }
}
