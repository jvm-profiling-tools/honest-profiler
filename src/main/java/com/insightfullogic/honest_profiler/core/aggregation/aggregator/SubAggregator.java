package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;

/**
 * Generic interface for aggregation functions which operate on already aggregated items. An Aggregator aggregates input
 * of type I into an {@link Aggregation} containing results of type T, which are keyed by a key of type String.
 * <p>
 * @param I the type of the input being aggregated
 * @param T the type of the content items in the resulting {@link Aggregation}
 */
public interface SubAggregator<I, T extends Keyed<String>>
{
    /**
     * Aggregate the provided input.
     * <p>
     * @param input the input for the aggregation
     * @return the resulting {@link Aggregation}
     */
    Aggregation<T> aggregate(I input);
}
