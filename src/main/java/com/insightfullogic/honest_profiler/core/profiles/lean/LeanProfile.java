package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.Profile;

/**
 * Alternative to {@link Profile} which stores/collects all info only once,
 * eliminating redundancy. The granularity is at stackframe level, keyed by
 * class name, method name, bci and line number.
 *
 * Any aggregation other than counts and time addition at the lowest level is
 * relegated to post-processing.
 */
public class LeanProfile
{
    private final Map<Long, MethodInfo> methodMap;
    private final Map<Long, ThreadInfo> threadMap;
    private final Map<Long, LeanNode> threads;

    public LeanProfile(Map<Long, MethodInfo> methodMap,
                       Map<Long, ThreadInfo> threadMap,
                       Map<Long, LeanNode> threadData)
    {
        this.methodMap = new HashMap<>(methodMap);
        this.threadMap = new HashMap<>(threadMap);
        this.threads = new HashMap<>();
        threadData.forEach((key, value) -> this.threads.put(key, value.copy()));
    }

    public Map<Long, MethodInfo> getMethodMap()
    {
        return methodMap;
    }

    public Map<Long, ThreadInfo> getThreadMap()
    {
        return threadMap;
    }

    public Map<Long, LeanNode> getThreads()
    {
        return threads;
    }

    public String getFqmn(LeanNode node)
    {
        return getMethodMap().get(node.getFrame().getMethodId()).getFqmn();
    }

    public String getThreadName(Long threadId)
    {
        ThreadInfo info = getThreadMap().get(threadId);
        return info == null ? "Unknown <" + threadId + ">" : info.getIdentification();
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("LP :\n");
        threads.forEach(
            (id, node) -> result.append(" Thread ")
                .append(threadMap.get(id) == null ? "UNKNOWN" : threadMap.get(id).getName())
                .append(node.toDeepString(1, methodMap)));
        return result.toString();
    }
}
