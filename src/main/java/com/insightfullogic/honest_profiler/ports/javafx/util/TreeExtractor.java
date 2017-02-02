package com.insightfullogic.honest_profiler.ports.javafx.util;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

import javafx.beans.value.ObservableObjectValue;

public class TreeExtractor implements Function<Object, Tree>
{
    ObservableObjectValue<CombinedGrouping> grouping;

    public TreeExtractor(ObservableObjectValue<CombinedGrouping> grouping)
    {
        super();
        this.grouping = grouping;
    }

    @Override
    public Tree apply(Object t)
    {
        return t == null ? null : ((AggregationProfile)t).getTree(grouping.get());
    }
}
