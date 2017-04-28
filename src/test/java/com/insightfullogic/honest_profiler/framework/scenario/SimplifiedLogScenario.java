package com.insightfullogic.honest_profiler.framework.scenario;

import static com.insightfullogic.honest_profiler.framework.AggregationUtil.keyFor;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.keysFor;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.start;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static java.util.Collections.reverse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.framework.checker.CheckAdapter;

/**
 * Implementation of {@link LogScenario} which allows creation of simplified {@link LogScenario}s, and which provides
 * logic for testing aggregations based on the scenario contents.
 *
 * When stack traces are added, they are automatically assigned a duration of 1 second (i.e. the {@link TraceStart}s
 * preceding and following the stack trace are spaced exactly 1 second apart). This means there is no need to keep track
 * of timing data : self count and total count can be used to trivially calculate self time and total time.
 */
public class SimplifiedLogScenario extends LogScenario
{
    // Instance Properties

    /**
     * Keeps the Thread Id in the most recently executed {@link TraceStart} in the scenario during execution. It is used
     * in the {@link #end()} method.
     */
    private long lastStartThreadId;

    /** Total number of traces. */
    private int nrTraces = 0;

    /** Maps for looking up the emitted {@link ThreadMeta}s. */
    private Map<String, Set<Long>> threadNameToIdMap;
    private Map<Long, ThreadMeta> threadIdToMetaMap;

    /** Map which keeps track of the total count for each thread. */
    private Map<Long, Integer> threadTotalCounts;

    /**
     * Maps keeping the actual aggregation data. The key of the outer map is the thread Id. The inner maps map the
     * aggregation key (frame or stack) to the number of times that aggregation key was seen.
     */
    private Map<Long, Map<StackFrame, Integer>> flatSelfCountsPerThread;
    private Map<Long, Map<StackFrame, Integer>> flatTotalCountsPerThread;
    private Map<Long, Map<Tuple<StackFrame>, Integer>> treeSelfCountsPerThread;
    private Map<Long, Map<Tuple<StackFrame>, Integer>> treeTotalCountsPerThread;

    // Instance Constructors

    /**
     * Constructs an empty scenario.
     * <p>
     * @param name the name of the scenario
     */
    public SimplifiedLogScenario(String name)
    {
        super(name);

        threadNameToIdMap = new HashMap<>();
        threadIdToMetaMap = new HashMap<>();

        threadTotalCounts = new HashMap<>();

        flatSelfCountsPerThread = new HashMap<>();
        flatTotalCountsPerThread = new HashMap<>();
        treeSelfCountsPerThread = new HashMap<>();
        treeTotalCountsPerThread = new HashMap<>();
    }

    // Instance Accessors

    public int getTraceCount()
    {
        return nrTraces;
    }

    // LogScenario Implementation

    @Override
    protected void addThreads(ThreadMeta... threads)
    {
        super.addThreads(threads);

        for (ThreadMeta thread : threads)
        {
            Set<Long> ids = threadNameToIdMap
                .computeIfAbsent(thread.getThreadName(), key -> new HashSet<>());
            ids.add(thread.getThreadId());
            threadIdToMetaMap.put(thread.getThreadId(), thread);
        }
    }

    // Log Event Addition

    /**
     * Adds a {@link TraceStart} and a stack trace (consisting of the provided series of {@link StackFrame}s) to the
     * scenario, for the given thread id. The {@link TraceStart}s are spaced apart by 1 second.
     * <p>
     * @param threadId the id of the thread for which the stack trace is added
     * @param frames the stack trace
     */
    protected void addStack(long threadId, StackFrame... frames)
    {
        super.addStack(start(1, threadId, ++nrTraces, 0), frames);
        lastStartThreadId = threadId;

        StackFrame[] reversed = reverseStack(frames);

        incrementThreadTotalCount(threadId);
        addFlatSelfCount(threadId, frames);
        addFlatTotalCount(threadId, frames);
        addTreeSelfCount(threadId, reversed);
        addTreeTotalCount(threadId, reversed);
    }

    /**
     * Adds the final {@link TraceStart} log event to the scenario, using the thread id which was most recently emitted.
     */
    protected void end()
    {
        super.addStart(start(1, lastStartThreadId, nrTraces + 1, 0));
    }

    // Automated Aggregation Checking

    /**
     * Checks a representation of an aggregation based on a {@link Flat} using the provided {@link CheckAdapter} against
     * the numbers calculated internally by the scenario.
     * <p>
     * @param adapter the adapter which can interpret the aggregation representation and verify the expected results
     * @param filters the filters which will be applied to the results
     */
    public void checkFlatAggregation(CheckAdapter<String> adapter,
        ScenarioStraightFilter... filters)
    {
        Map<String, Integer> selfCounts = calculateFlatMap(
            flatSelfCountsPerThread,
            adapter.getThreadGrouping(),
            adapter.getFrameGrouping());

        Map<String, Integer> totalCounts = calculateFlatMap(
            flatTotalCountsPerThread,
            adapter.getThreadGrouping(),
            adapter.getFrameGrouping());

        if (filters != null && filters.length > 0)
        {
            filterFlat(selfCounts, totalCounts, filters);
        }

        adapter.assertSizeEquals(totalCounts.size());
        selfCounts.entrySet().forEach(entry -> checkFlatSelf(adapter, entry));
        totalCounts.entrySet().forEach(entry -> checkFlatTotal(adapter, entry));
    }

    /**
     * Checks a representation of an aggregation based on a {@link Tree} using the provided {@link CheckAdapter} against
     * the numbers calculated internally by the scenario.
     * <p>
     * @param adapter the adapter which can interpret the aggregation representation and verify the expected results
     * @param filters any filters which will be applied to the results
     */
    public void checkTreeAggregation(CheckAdapter<String[]> adapter,
        ScenarioStraightFilter... filters)
    {
        Map<Tuple<String>, Integer> selfCounts = calculateTreeMap(
            treeSelfCountsPerThread,
            adapter.getThreadGrouping(),
            adapter.getFrameGrouping(),
            true);

        Map<Tuple<String>, Integer> totalCounts = calculateTreeMap(
            treeTotalCountsPerThread,
            adapter.getThreadGrouping(),
            adapter.getFrameGrouping(),
            false);

        if (filters != null && filters.length > 0)
        {
            filterTree(selfCounts, totalCounts, filters);
        }

        int expectedSize = totalCounts.size();

        adapter.assertSizeEquals(expectedSize);
        selfCounts.entrySet().forEach(entry -> checkTreeSelf(adapter, entry));
        totalCounts.entrySet().forEach(entry -> checkTreeTotal(adapter, entry));
    }

    // Check Helper Methods

    /**
     * Asks the {@link CheckAdapter} to verify the "self" data items against the calculated values for a single frame.
     * The self time is calculated using the self count, since this is a built-in correspondence in the
     * {@link SimplifiedLogScenario}.
     * <p>
     * @param checker the {@link CheckAdapter}
     * @param entry the calculated aggregation key and the expected self count
     */
    private void checkFlatSelf(CheckAdapter<String> checker, Entry<String, Integer> entry)
    {
        String key = entry.getKey();
        Integer value = entry.getValue();

        checker.assertSelfCntEquals(key, value);
        checker.assertSelfTimeEquals(key, nano(value));
        checker.assertSelfCntPctEquals(key, value / (double)nrTraces);
        checker.assertSelfTimePctEquals(key, nano(value) / (double)nano(nrTraces));
    }

    /**
     * Asks the {@link CheckAdapter} to verify the "total" data items against the calculated values for a single frame.
     * The total time is calculated using the total count, since this is a built-in correspondence in the
     * {@link SimplifiedLogScenario}.
     * <p>
     * @param checker the {@link CheckAdapter}
     * @param entry the calculated aggregation key and the expected total count
     */
    private void checkFlatTotal(CheckAdapter<String> checker, Entry<String, Integer> entry)
    {
        String key = entry.getKey();
        Integer value = entry.getValue();

        checker.assertTotalCntEquals(key, value);
        checker.assertTotalTimeEquals(key, nano(value));
        checker.assertTotalCntPctEquals(key, value / (double)nrTraces);
        checker.assertTotalTimePctEquals(key, nano(value) / (double)nano(nrTraces));
    }

    /**
     * Asks the {@link CheckAdapter} to verify the "self" data items against the calculated values for a (partial)
     * stack. The self time is calculated using the self count, since this is a built-in correspondence in the
     * {@link SimplifiedLogScenario}.
     * <p>
     * @param checker the {@link CheckAdapter}
     * @param entry the calculated aggregation keys and the expected self count
     */
    private void checkTreeSelf(CheckAdapter<String[]> checker, Entry<Tuple<String>, Integer> entry)
    {
        String[] key = entry.getKey().elements;
        Integer value = entry.getValue();

        checker.assertSelfCntEquals(key, value);
        checker.assertSelfTimeEquals(key, nano(value));
        checker.assertSelfCntPctEquals(key, value / (double)nrTraces);
        checker.assertSelfTimePctEquals(key, nano(value) / (double)nano(nrTraces));
    }

    /**
     * Asks the {@link CheckAdapter} to verify the "total" data items against the calculated values for a (partial)
     * stack. The total time is calculated using the total count, since this is a built-in correspondence in the
     * {@link SimplifiedLogScenario}.
     * <p>
     * @param checker the {@link CheckAdapter}
     * @param entry the calculated aggregation keys and the expected total count
     */
    private void checkTreeTotal(CheckAdapter<String[]> checker, Entry<Tuple<String>, Integer> entry)
    {
        String[] key = entry.getKey().elements;
        Integer value = entry.getValue();

        checker.assertTotalCntEquals(key, value);
        checker.assertTotalTimeEquals(key, nano(value));
        checker.assertTotalCntPctEquals(key, value / (double)nrTraces);
        checker.assertTotalTimePctEquals(key, nano(value) / (double)nano(nrTraces));
    }

    private void filterFlat(Map<String, Integer> selfCounts, Map<String, Integer> totalCounts,
        ScenarioStraightFilter... filters)
    {
        Set<String> keys = new HashSet<>(totalCounts.keySet());

        keys.forEach(key ->
        {
            int selfCnt = selfCounts.get(key) == null ? 0 : selfCounts.get(key);
            int totalCnt = totalCounts.get(key);
            long selfTime = nano(selfCnt);
            long totalTime = nano(totalCnt);
            double selfCntPct = selfCnt / (double)nrTraces;
            double totalCntPct = totalCnt / (double)nrTraces;
            double selfTimePct = selfTime / (double)nano(nrTraces);
            double totalTimePct = totalTime / (double)nano(nrTraces);

            if (!asList(filters).stream().allMatch(filter -> filter.accept(
                key,
                selfCnt,
                totalCnt,
                selfTime,
                totalTime,
                selfCntPct,
                totalCntPct,
                selfTimePct,
                totalTimePct)))
            {
                selfCounts.remove(key);
                totalCounts.remove(key);
            }
        });
    }

    private void filterTree(Map<Tuple<String>, Integer> selfCounts,
        Map<Tuple<String>, Integer> totalCounts, ScenarioStraightFilter... filters)
    {
        Set<Tuple<String>> keys = new HashSet<>(totalCounts.keySet());
        final Set<Tuple<String>> acceptedKeys = new HashSet<>();
        final Set<Tuple<String>> retainedKeys = new HashSet<>();

        keys.forEach(key ->
        {
            int selfCnt = selfCounts.get(key) == null ? 0 : selfCounts.get(key);
            int totalCnt = totalCounts.get(key);
            long selfTime = nano(selfCnt);
            long totalTime = nano(totalCnt);
            double selfCntPct = selfCnt / (double)nrTraces;
            double totalCntPct = totalCnt / (double)nrTraces;
            double selfTimePct = selfTime / (double)nano(nrTraces);
            double totalTimePct = totalTime / (double)nano(nrTraces);

            if (asList(filters).stream().allMatch(filter -> filter.accept(
                key.elements[key.elements.length - 1],
                selfCnt,
                totalCnt,
                selfTime,
                totalTime,
                selfCntPct,
                totalCntPct,
                selfTimePct,
                totalTimePct)))
            {
                acceptedKeys.add(key);
            }
        });

        acceptedKeys.forEach(acceptedKey -> keys.forEach(key ->
        {
            if (acceptedKey.startsWith(key))
            {
                retainedKeys.add(key);
            }
        }));

        selfCounts.keySet().retainAll(retainedKeys);
        totalCounts.keySet().retainAll(retainedKeys);
    }

    // Aggregation Calculation Helper Methods

    /**
     * Increments the total count for the thread.
     * @param threadId the id of the thread
     */
    private void incrementThreadTotalCount(long threadId)
    {
        threadTotalCounts.compute(threadId, (k, v) -> v == null ? 1 : v + 1);
    }

    /**
     * Adds the self count for an emitted stack trace when aggregated into a {@link Flat}.
     * <p>
     * @param threadId the id of the thread on which the stack trace was emitted
     * @param frames the {@link StackFrame}s making up the stack trace
     */
    private void addFlatSelfCount(long threadId, StackFrame... frames)
    {
        Map<StackFrame, Integer> flatSelfCounts = flatSelfCountsPerThread
            .computeIfAbsent(threadId, id -> new HashMap<>());
        flatSelfCounts.compute(frames[0], (frame, count) -> count == null ? 1 : count + 1);
    }

    /**
     * Adds the total count for an emitted stack trace when aggregated into a {@link Flat}.
     * <p>
     * @param threadId the id of the thread on which the stack trace was emitted
     * @param frames the {@link StackFrame}s making up the stack trace
     */
    private void addFlatTotalCount(long threadId, StackFrame... frames)
    {
        Map<StackFrame, Integer> flatTotalCounts = flatTotalCountsPerThread
            .computeIfAbsent(threadId, id -> new HashMap<>());
        asList(frames).forEach(
            newFrame -> flatTotalCounts
                .compute(newFrame, (frame, count) -> count == null ? 1 : count + 1));
    }

    /**
     * Returns an array consisting of the {@link StackFrame}s in the stack trace, in reverse order of emission.
     * <p>
     * @param frames the {@link StackFrame}s in a stack trace in order of emission
     * @return an array with the specified frames in reversed order
     */
    private StackFrame[] reverseStack(StackFrame[] frames)
    {
        List<StackFrame> reversedList = asList(copyOf(frames, frames.length));
        reverse(reversedList);
        return reversedList.toArray(new StackFrame[frames.length]);
    }

    /**
     * Adds the self count for an emitted stack trace when aggregated into a {@link Tree}.
     * <p>
     * @param threadId the id of the thread on which the stack trace was emitted
     * @param frames the {@link StackFrame}s making up the stack trace
     */
    private void addTreeSelfCount(long threadId, StackFrame... frames)
    {
        Map<Tuple<StackFrame>, Integer> treeSelfCounts = treeSelfCountsPerThread
            .computeIfAbsent(threadId, id -> new HashMap<>());
        treeSelfCounts.compute(
            new Tuple<StackFrame>(frames),
            (frame, count) -> count == null ? 1 : count + 1);
    }

    /**
     * Adds the total count for an emitted stack trace when aggregated into a {@link Tree}.
     * <p>
     * @param threadId the id of the thread on which the stack trace was emitted
     * @param frames the {@link StackFrame}s making up the stack trace
     */
    private void addTreeTotalCount(long threadId, StackFrame... frames)
    {
        Map<Tuple<StackFrame>, Integer> treeTotalCounts = treeTotalCountsPerThread
            .computeIfAbsent(threadId, id -> new HashMap<>());
        for (int i = 0; i < frames.length; i++)
        {
            Tuple<StackFrame> treeKey = new Tuple<StackFrame>(copyOf(frames, i + 1));
            treeTotalCounts.compute(treeKey, (frame, count) -> count == null ? 1 : count + 1);
        }
    }

    /**
     * Calculates a map which associates the calculated aggregation key with the expected count for {@link Flat}
     * aggregations.
     * <p>
     * @param countMap the self or total count map for which the result will be calculated
     * @param threadGrouping the {@link ThreadGrouping} used for aggregation
     * @param frameGrouping the {@link FrameGrouping} used for aggregation
     * @return the calculated map
     */
    private Map<String, Integer> calculateFlatMap(Map<Long, Map<StackFrame, Integer>> countMap,
        ThreadGrouping threadGrouping, FrameGrouping frameGrouping)
    {
        Map<String, Integer> result = new HashMap<>();

        countMap.values().forEach(
            map -> map.entrySet().forEach(
                entry -> result.compute(
                    keyFor(frameGrouping, entry.getKey()),
                    (key, value) -> value == null ? entry.getValue() : entry.getValue() + value)));
        return result;
    }

    /**
     * Calculates a map which associates the calculated aggregation keys with the expected count for {@link Tree}
     * aggregations.
     * <p>
     * @param countMap the self or total count map for which the result will be calculated
     * @param threadGrouping the {@link ThreadGrouping} used for aggregation
     * @param frameGrouping the {@link FrameGrouping} used for aggregation
     * @param isSelf a boolean indicating whether the calculation is for the self counts
     * @return the calculated map
     */
    private Map<Tuple<String>, Integer> calculateTreeMap(
        Map<Long, Map<Tuple<StackFrame>, Integer>> countMap, ThreadGrouping threadGrouping,
        FrameGrouping frameGrouping, boolean isSelf)
    {
        Map<Tuple<String>, Integer> result = new HashMap<>();

        countMap.entrySet().forEach(
            threadEntry -> threadEntry.getValue().entrySet().forEach(
                entry -> result.compute(
                    new Tuple<String>(
                        keysFor(
                            threadGrouping,
                            frameGrouping,
                            threadIdToMetaMap.get(threadEntry.getKey()),
                            entry.getKey().elements)),
                    (key, value) -> value == null ? entry.getValue() : entry.getValue() + value))
        );

        if (!isSelf)
        {
            threadTotalCounts.entrySet().forEach(entry ->
            {
                result.compute(
                    new Tuple<String>(
                        keyFor(threadGrouping, threadIdToMetaMap.get(entry.getKey()))),
                    (key, value) -> value == null ? entry.getValue() : entry.getValue() + value);
            });
        }

        return result;
    }

    // Internal classes

    /**
     * Helper class which allows to use arrays as keys in the tree-based maps, ensuring that {@link #equals(Object)} and
     * {@link #hashCode()} work as desired.
     *
     * @param <T> the type of {@link Object} stored in the Tuple
     */
    private class Tuple<T>
    {
        T[] elements;

        @SafeVarargs
        private Tuple(T... elements)
        {
            this.elements = elements;
        }

        public boolean startsWith(Tuple<T> other)
        {
            if (other.elements.length > elements.length)
            {
                return false;
            }
            for (int i = 0; i < other.elements.length; i++)
            {
                if (!elements[i].equals(other.elements[i]))
                {
                    return false;
                }
            }
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof Tuple))
            {
                return false;
            }

            return Arrays.equals(elements, ((Tuple<T>)other).elements);
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(elements);
        }

        @Override
        public String toString()
        {
            return Arrays.toString(elements);
        }
    }
}
