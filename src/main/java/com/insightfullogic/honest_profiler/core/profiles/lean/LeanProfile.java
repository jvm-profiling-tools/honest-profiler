package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.Profile;

/**
 * Alternative to {@link Profile} which stores/collects all info only once, eliminating redundancy. The granularity is
 * at stackframe level, keyed by class name, method name, bci and line number.
 *
 * Any aggregation other than counts and time addition at the lowest level is relegated to post-processing.
 */
public class LeanProfile
{
    private final Map<Long, MethodInfo> methodInfoMap;
    private final Map<Long, ThreadInfo> threadInfoMap;
    private final Map<Long, LeanThreadNode> threads;

    public LeanProfile(Map<Long, MethodInfo> methodMap,
                       Map<Long, ThreadInfo> threadMap,
                       Map<Long, LeanThreadNode> threadData)
    {
        this.methodInfoMap = new HashMap<>(methodMap);
        this.threadInfoMap = new HashMap<>(threadMap);
        this.threads = new HashMap<>();
        threadData.forEach((key, value) -> this.threads.put(key, value.copy()));
    }

    public Map<Long, MethodInfo> getMethodInfoMap()
    {
        return methodInfoMap;
    }

    public ThreadInfo getThreadInfo(long id)
    {
        return threadInfoMap.get(id);
    }

    public Map<Long, LeanThreadNode> getThreads()
    {
        return threads;
    }

    public String getFqmn(LeanNode node)
    {
        return getMethodInfoMap().get(node.getFrame().getMethodId()).getFqmn();
    }

    public String getThreadName(Long threadId)
    {
        ThreadInfo info = getThreadInfo(threadId);
        return info == null ? "Unknown <" + threadId + ">" : info.getIdentification();
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("LP :\n");
        threads.forEach(
            (id, node) -> result.append(" Thread ")
                .append(threadInfoMap.get(id) == null ? "UNKNOWN" : threadInfoMap.get(id).getName())
                .append(node.toDeepString(1, methodInfoMap)));
        return result.toString();
    }
}
