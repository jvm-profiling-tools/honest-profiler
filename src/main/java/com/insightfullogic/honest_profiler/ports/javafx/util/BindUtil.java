package com.insightfullogic.honest_profiler.ports.javafx.util;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;

public class BindUtil
{
    public static final Function<Object, Flat<String>> FLAT_EXTRACTOR = o -> o == null ? null
        : ((AggregationProfile)o).getFlat();

    public static final Function<Object, Tree<String>> TREE_EXTRACTOR = o -> o == null ? null
        : ((AggregationProfile)o).getTree();

    public static final Function<Object, FlameGraph> FLAME_EXTRACTOR = o -> (FlameGraph)o;

    private BindUtil()
    {
        // Private Constructor for utility class
    }
}
