package com.insightfullogic.honest_profiler.ports.javafx.util;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;

import javafx.beans.value.ObservableObjectValue;

public class FlatExtractor implements Function<Object, Flat>
{
    ObservableObjectValue<CombinedGrouping> grouping;

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
