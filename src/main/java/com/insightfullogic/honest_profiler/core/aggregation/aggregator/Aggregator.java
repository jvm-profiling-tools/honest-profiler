package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Generic interface for aggregation functions. An Aggregator aggregates input of type <I> into an {@link Aggregation}
 * containing results of type <T>, which are keyed by a key of type <K>.
 *
 * @param <I> the type of the input being aggregated
 * @param <K> the type of the key of the content items in the resulting {@link Aggregation}
 * @param <T> the type of the content items in the resulting {@link Aggregation}
 */
public interface Aggregator<I, K, T extends Keyed<K>>
{
    /**
     * Aggregate the provided input.
     *
     * NOTE : The reference will be removed at some point. The original idea was as follows : in the profiles,
     * percentages are shown. These are calculated with respect to a particular reference. E.g. Self Time Share or Self
     * Time Percent of a method is equal to the Self Time of the method divided by the Total Time at the top-level of
     * the aggregation. But you might want to calculate it relative to the Total Time of the containing Thread for
     * instance. So the items have alink to their containing {@link Aggregation}s, and take the reference data from
     * there.
     *
     * Unfortunately the error in reasoning here obviously is that a single reference at {@link Aggregation} level isn't
     * correct - if we want items to show percentages relative to their containing Threads, you need one "reference" per
     * thread, so this won't work. Will rework this some time soon, hopefully.
     *
     * @param input the input for the aggregation
     * @param reference the reference for relative values calculated by the items
     * @return the resulting {@link Aggregation}
     */
    Aggregation<K, T> aggregate(I input, LeanNode reference);
}
