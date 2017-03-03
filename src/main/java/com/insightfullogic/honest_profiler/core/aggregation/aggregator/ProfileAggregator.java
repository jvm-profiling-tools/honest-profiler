package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;

/**
 * Generic interface for aggregation functions which operate on the entire {@link AggregationProfile}. An Aggregator
 * aggregates an input {@link AggregationProfile} into an {@link Aggregation} containing results of type T, which are
 * keyed by a String.
 * <p>
 * @param <T> the type of the content items in the resulting {@link Aggregation}
 */
public interface ProfileAggregator<T extends Keyed<String>>
{
    /**
     * Aggregate the provided {@link AggregationProfile}.
     * <p>
     * @param input the {@link AggregationProfile} to be aggregated
     * @param grouping the {@link CombinedGrouping} to be used when aggregating
     * @return the resulting {@link Aggregation}
     */
    Aggregation<T> aggregate(AggregationProfile input, CombinedGrouping grouping);
}
