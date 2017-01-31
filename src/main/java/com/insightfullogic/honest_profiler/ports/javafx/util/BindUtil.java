package com.insightfullogic.honest_profiler.ports.javafx.util;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantTreeAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.AncestorTreeAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantFlatAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;

import javafx.scene.control.TreeItem;

public class BindUtil
{
    private static final DescendantFlatAggregator DESCENDANT_AGGREGATOR = new DescendantFlatAggregator();

    private static final AncestorTreeAggregator CALLING_AGGREGATOR = new AncestorTreeAggregator();
    private static final DescendantTreeAggregator CALLED_AGGREGATOR = new DescendantTreeAggregator();

    public static final Function<Object, Flat<String>> FLAT_EXTRACTOR = o -> o == null ? null
        : ((AggregationProfile)o).getFlat();

    public static final Function<Object, Tree<String>> TREE_EXTRACTOR = o -> o == null ? null
        : ((AggregationProfile)o).getTree();

    public static final Function<Object, FlameGraph> FLAME_EXTRACTOR = o -> (FlameGraph)o;

    public static final Function<Object, Flat<String>> DESCENDANT_FLAT_EXTRACTOR = o ->
    {
        @SuppressWarnings("unchecked")
        TreeItem<Node<String>> treeItem = (TreeItem<Node<String>>)o;
        Node<String> node = treeItem == null ? null : treeItem.getValue();
        if (node == null)
        {
            return null;
        }

        return DESCENDANT_AGGREGATOR.aggregate(
            node.getAggregation().getSource(),
            node,
            node.getAggregation().getReference());
    };

    public static final Function<Object, Tree<String>> CALLING_EXTRACTOR = o ->
    {
        @SuppressWarnings("unchecked")
        Entry<String> entry = (Entry<String>)o;
        if (o == null)
        {
            return null;
        }
        return CALLING_AGGREGATOR.aggregate(
            entry.getAggregation().getSource(),
            entry,
            entry.getAggregation().getReference());
    };

    public static final Function<Object, Tree<String>> CALLED_EXTRACTOR = o ->
    {
        @SuppressWarnings("unchecked")
        Entry<String> entry = (Entry<String>)o;
        if (o == null)
        {
            return null;
        }
        return CALLED_AGGREGATOR.aggregate(
            entry.getAggregation().getSource(),
            entry,
            entry.getAggregation().getReference());
    };

    private BindUtil()
    {
        // Private Constructor for utility class
    }
}
