package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo;

/**
 * A {@link LeanProfile} is a profile which which collects stack trace samples, and stores the information with as
 * little redundancy as possible. The granularity is at stack frame level, keyed by class name, method name, BCI and
 * line number.
 * <p>
 * The profile is modeled as a collection of trees consisting of {@link LeanNode}s.
 * <p>
 * The root of each tree is a {@link LeanThreadNode} representing the thread-level information. The children of that
 * {@link LeanThreadNode} represent the methods called directly at the top-level of the thread (typically
 * {@link Thread#run()}, but there can be others too).
 * <p>
 * For the descendants, the following holds : whenever a stack trace sample has been added to the profile in which a
 * method A is called by a method B, the profile will contain a {@link LeanNode} representing B with as child a
 * {@link LeanNode} representing A.
 * <p>
 * Other than the {@link LeanNode} trees, the profile also contains the information for mapping thread and method ids to
 * the corresponding {@link ThreadInfo} and {@link MethodInfo} metadata.
 * <p>
 * Any aggregation other than counts and time addition at the lowest level is relegated to post-processing.
 */
public class LeanProfile
{
    // Instance Properties

    private final Map<Long, MethodInfo> methodInfoMap;
    private final Map<Long, ThreadInfo> threadInfoMap;
    private final Map<Long, LeanThreadNode> threads;

    // Instance constructors

    /**
     * Constructor which specifies the maps containing the method id - {@link MethodInfo} and thread id -
     * {@link ThreadInfo} mappings, and a map containing the thread id - {@link LeanThreadNode} stack tree mappings.
     * <p>
     * @param methodMap a {@link Map} mapping the method id to the corresponding {@link MethodInfo}
     * @param threadMap a {@link Map} mapping the thread id to the corresponding {@link ThreadInfo}
     * @param threadData a {@link Map} mapping the thread id to the {@link LeanThreadNode} root of the {@link LeanNode}
     *            tree containing the aggregated stack trace sample information for that thread
     */
    public LeanProfile(Map<Long, MethodInfo> methodMap,
                       Map<Long, ThreadInfo> threadMap,
                       Map<Long, LeanThreadNode> threadData)
    {
        methodInfoMap = new HashMap<>(methodMap);
        threadInfoMap = new HashMap<>(threadMap);
        threads = new HashMap<>();
        threadData.forEach((key, value) -> threads.put(key, value.copy()));
    }

    // Instance Accessors

    /**
     * Returns the mapping between method ids and their corresponding {@link MethodInfo} objects.
     * <p>
     * @return the mapping between method ids and their corresponding {@link MethodInfo} objects
     */
    public Map<Long, MethodInfo> getMethodInfoMap()
    {
        return methodInfoMap;
    }

    /**
     * Returns the mapping between thread ids and their corresponding {@link ThreadInfo} objects.
     * <p>
     * @return the mapping between thread ids and their corresponding {@link ThreadInfo} objects
     */
    public Map<Long, ThreadInfo> getThreadInfoMap()
    {
        return threadInfoMap;
    }

    /**
     * Returns the mapping between thread ids and their corresponding {@link ThreadInfo} objects.
     * <p>
     * @param id the thread id
     * @return the mapping between thread ids and their corresponding {@link ThreadInfo} objects
     */
    public ThreadInfo getThreadInfo(long id)
    {
        return threadInfoMap.get(id);
    }

    /**
     * Returns the mapping between thread Ids and the root {@link LeanThreadNode} objects.
     * <p>
     * @return the mapping between thread Ids and the root {@link LeanThreadNode} objects
     */
    public Map<Long, LeanThreadNode> getThreads()
    {
        return threads;
    }

    // Key and/or name Construction Methods

    /**
     * Calculates the FQMN. Introduced for hardening, based on a profile which contained a Method Id for which no
     * MethodInfo was available.
     * <p>
     *
     * @return the FQMN for the node
     */
    public String getFqmn(LeanNode node)
    {
        long methodId = node.getFrame().getMethodId();
        MethodInfo info = getMethodInfoMap().get(methodId);

        // Explicit NULL check because we encountered a profile where a particular MethodInfo wasn't present
        return info == null ? "<UNIDENTIFIED : Method Id = " + methodId + ">" : info.getFqmn();
    }

    /**
     * Return the key for a {@link LeanNode} representing a frame, constructed by appending the line number to the FQMN,
     * separated by a colon.
     * <p>
     * @param node the node for which the key is calculated
     * @return the aggregation key for the node consisting of the FQMN and the line number
     */
    public String getFqmnPlusLineNr(LeanNode node)
    {
        StringBuilder result = new StringBuilder();
        result.append(getFqmn(node)).append(":").append(node.getFrame().getLineNr());
        return result.toString();
    }

    /**
     * Return the key for a {@link LeanNode} representing a frame, constructed by appending the BCI to the FQMN,
     * separated by a colon.
     * <p>
     * @param node the node for which the key is calculated
     * @return the aggregation key for the node consisting of the FQMN and the BCI
     */
    public String getBciKey(LeanNode node)
    {
        StringBuilder result = new StringBuilder();
        result.append(getFqmn(node)).append(":").append(node.getFrame().getBci());
        return result.toString();
    }

    /**
     * Return the key for a {@link LeanNode} representing a frame, constructed by prepending the method Id to the FQMN.
     * <p>
     * @param node the node for which the key is calculated
     * @return the aggregation key for the node consisting of the method Id and the FQMN
     */
    public String getMethodIdKey(LeanNode node)
    {
        StringBuilder result = new StringBuilder();
        result.append("(").append(Long.toString(node.getFrame().getMethodId())).append(") ");
        result.append(getFqmn(node));
        return result.toString();
    }

    /**
     * Returns the display name for the thread identified by the specified id. If there is no {@link ThreadInfo} for the
     * thread id, a name is constructed.
     * <p>
     * @param threadId the id of the thread
     * @return the calculated display name
     */
    public String getThreadName(Long threadId)
    {
        ThreadInfo info = getThreadInfo(threadId);
        return info == null ? "Unknown <" + threadId + ">" : info.getIdentification();
    }

    // Object Implementation

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
