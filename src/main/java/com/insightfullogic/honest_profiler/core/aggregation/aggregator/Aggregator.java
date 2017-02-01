package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;

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
     * @param input the input for the aggregation
     * @return the resulting {@link Aggregation}
     */
    Aggregation<K, T> aggregate(AggregationProfile source, I input);
}
