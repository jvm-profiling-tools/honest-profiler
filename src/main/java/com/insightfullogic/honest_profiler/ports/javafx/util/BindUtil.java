package com.insightfullogic.honest_profiler.ports.javafx.util;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping.combine;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.ALL_TOGETHER;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.AncestorTreeAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantFlatAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantTreeAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;
import com.insightfullogic.honest_profiler.ports.javafx.controller.AbstractViewController;

import javafx.scene.control.TreeItem;

public class BindUtil
{
    private static final DescendantFlatAggregator DESCENDANT_AGGREGATOR = new DescendantFlatAggregator();

    private static final AncestorTreeAggregator CALLING_AGGREGATOR = new AncestorTreeAggregator();
    private static final DescendantTreeAggregator CALLED_AGGREGATOR = new DescendantTreeAggregator();

    public static final Function<Object, Flat> FLAT_EXTRACTOR = o -> o == null ? null
        : ((AggregationProfile)o).getFlat(combine(ALL_TOGETHER, BY_FQMN));

    public static final Function<Object, Tree> TREE_EXTRACTOR = o -> o == null ? null
        : ((AggregationProfile)o).getTree(combine(ThreadGrouping.BY_NAME, BY_FQMN));

    public static final Function<Object, FlameGraph> FLAME_EXTRACTOR = o -> (FlameGraph)o;

    public static final Function<Object, Flat> DESCENDANT_FLAT_EXTRACTOR = o ->
    {
        @SuppressWarnings("unchecked")
        TreeItem<Node> treeItem = (TreeItem<Node>)o;
        Node node = treeItem == null ? null : treeItem.getValue();
        if (node == null)
        {
            return null;
        }

        return DESCENDANT_AGGREGATOR.aggregate(node.getAggregation().getSource(), node);
    };

    public static final Function<Object, Tree> CALLING_EXTRACTOR = o ->
    {
        Entry entry = (Entry)o;
        if (o == null)
        {
            return null;
        }
        return CALLING_AGGREGATOR.aggregate(entry.getAggregation().getSource(), entry);
    };

    public static final Function<Object, Tree> CALLED_EXTRACTOR = o ->
    {
        Entry entry = (Entry)o;
        if (o == null)
        {
            return null;
        }
        return CALLED_AGGREGATOR.aggregate(entry.getAggregation().getSource(), entry);
    };

    public static final Function<Object, Flat> flatExtractor(AbstractViewController<?> view)
    {
        return new FlatExtractor(view.getGrouping());
    }

    public static final Function<Object, Tree> treeExtractor(AbstractViewController<?> view)
    {
        return new TreeExtractor(view.getGrouping());
    }

    private BindUtil()
    {
        // Private Constructor for utility class
    }
}
