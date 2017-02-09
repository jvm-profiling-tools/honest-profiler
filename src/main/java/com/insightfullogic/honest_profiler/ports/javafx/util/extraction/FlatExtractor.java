package com.insightfullogic.honest_profiler.ports.javafx.util.extraction;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil;

import javafx.beans.value.ObservableObjectValue;

/**
 * {@link Function} which retrieves the {@link Flat} aggregation for the specified {@link CombinedGrouping} from an
 * {@link AggregationProfile}, for use in {@link BindUtil}.
 */
public class FlatExtractor implements Function<Object, Flat>
{
    // Instance Properties

    private final ObservableObjectValue<CombinedGrouping> grouping;

    // Instance Constructors

    /**
     * Constructor specifying the {@link CombinedGrouping} to be used when aggregating.
     * <p>
     * @param grouping the {@link CombinedGrouping} to be used when aggregating
     */
    public FlatExtractor(ObservableObjectValue<CombinedGrouping> grouping)
    {
        super();
        this.grouping = grouping;
    }

    @Override
    public Flat apply(Object t)
    {
        return t == null ? null : ((AggregationProfile)t).getFlat(grouping.get());
    }
}
