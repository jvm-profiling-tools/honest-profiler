package com.insightfullogic.honest_profiler.ports.javafx.util.extraction;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil;

import javafx.beans.value.ObservableObjectValue;

/**
 * {@link Function} which retrieves the {@link Tree} aggregation for the specified {@link CombinedGrouping} from an
 * {@link AggregationProfile}, for use in {@link BindUtil}.
 */
public class TreeExtractor implements Function<Object, Tree>
{
    // Instance Properties

    ObservableObjectValue<CombinedGrouping> grouping;

    // Instance Constructors

    /**
     * Constructor specifying the {@link CombinedGrouping} to be used when aggregating.
     * <p>
     * @param grouping the {@link CombinedGrouping} to be used when aggregating
     */
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
