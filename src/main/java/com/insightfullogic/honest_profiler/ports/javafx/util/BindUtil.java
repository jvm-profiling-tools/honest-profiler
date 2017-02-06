package com.insightfullogic.honest_profiler.ports.javafx.util;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.aggregator.AncestorTreeAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantFlatAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantTreeAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;
import com.insightfullogic.honest_profiler.ports.javafx.controller.AbstractViewController;
import com.insightfullogic.honest_profiler.ports.javafx.util.extraction.FlatExtractor;
import com.insightfullogic.honest_profiler.ports.javafx.util.extraction.TreeExtractor;

import javafx.scene.control.TreeItem;

public class BindUtil
{
    private static final DescendantFlatAggregator DESCENDANT_FLAT_AGGREGATOR = new DescendantFlatAggregator();

    private static final AncestorTreeAggregator ANCESTOR_TREE_AGGREGATOR = new AncestorTreeAggregator();
    private static final DescendantTreeAggregator DESCENDANT_TREE_AGGREGATOR = new DescendantTreeAggregator();

    public static final Function<Object, FlameGraph> FLAME_EXTRACTOR = o -> (FlameGraph)o;

    public static final Function<Object, Tree> ANCESTOR_TREE_EXTRACTOR = o ->
    {
        Entry entry = (Entry)o;
        if (o == null)
        {
            return null;
        }
        return ANCESTOR_TREE_AGGREGATOR.aggregate(entry);
    };

    public static final Function<Object, Tree> DESCENDANT_TREE_EXTRACTOR = o ->
    {
        Entry entry = (Entry)o;
        if (o == null)
        {
            return null;
        }
        return DESCENDANT_TREE_AGGREGATOR.aggregate(entry);
    };

    public static final Function<Object, Flat> DESCENDANT_FLAT_EXTRACTOR = o ->
    {
        @SuppressWarnings("unchecked")
        TreeItem<Node> treeItem = (TreeItem<Node>)o;
        Node node = treeItem == null ? null : treeItem.getValue();
        if (node == null)
        {
            return null;
        }

        return DESCENDANT_FLAT_AGGREGATOR.aggregate(node);
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
