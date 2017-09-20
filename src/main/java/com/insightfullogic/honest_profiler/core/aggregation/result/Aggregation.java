package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;

/**
 * Superclass for the aggregation container data structures which aggregate an {@link AggregationProfile} using a
 * {@link CombinedGrouping}.
 * <p>
 * @param <T> the type of data item contained in the Aggregation.
 */
public abstract class Aggregation<T extends Keyed<String>>
{
    // Instance Properties

    private final AggregationProfile source;
    private final CombinedGrouping grouping;
    private final List<T> data;

    // Instance Constructors

    /**
     * Constructor specifying the source {@link AggregationProfile}, the {@link CombinedGrouping} used for aggregating
     * and the data in the aggregation.
     * <p>
     * @param source the source {@link AggregationProfile} whose contents are aggregated
     * @param grouping the {@link CombinedGrouping} used for aggregating
     * @param data the data in the Aggregation
     */
    public Aggregation(AggregationProfile source, CombinedGrouping grouping, List<T> data)
    {
        super();
        this.source = source;
        this.grouping = grouping;
        this.data = data;
    }

    // Instance Accessors

    /**
     * Returns the source {@link AggregationProfile}.
     * <p>
     * @return the source {@link AggregationProfile}
     */
    public AggregationProfile getSource()
    {
        return source;
    }

    /**
     * Returns the {@link CombinedGrouping} used to aggregate the data.
     * <p>
     * @return the {@link CombinedGrouping} used to aggregate the data
     */
    public CombinedGrouping getGrouping()
    {
        return grouping;
    }

    /**
     * Returns the list of data items contained in this Aggregation.
     * <p>
     * @return the list of data items contained in this Aggregation
     */
    public List<T> getData()
    {
        return data;
    }

    // Main Methods

    /**
     * Filters the Aggregation, keeping only contained items which are accepted by the filter, and returns a new
     * {@link Aggregation} containing the result.
     * <p>
     * @param filterSpec the {@link FilterSpecification} which specifies the filter to be applied
     * @return a new {@link Aggregation} containing the filtered result
     */
    public abstract Aggregation<T> filter(FilterSpecification<T> filterSpec);

    // Object Implementation

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("Aggregation " + getClass().getSimpleName() + " (" + grouping + ") :\n");
        data.forEach(t -> result.append(t).append("\n"));
        return result.toString();
    }
}
