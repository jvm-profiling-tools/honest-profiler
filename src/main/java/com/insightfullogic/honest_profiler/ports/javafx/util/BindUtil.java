package com.insightfullogic.honest_profiler.ports.javafx.util;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.AncestorTreeAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantFlatAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.DescendantTreeAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.TreeProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;
import com.insightfullogic.honest_profiler.ports.javafx.controller.AbstractViewController;
import com.insightfullogic.honest_profiler.ports.javafx.util.extraction.FlatExtractor;
import com.insightfullogic.honest_profiler.ports.javafx.util.extraction.TreeExtractor;

import javafx.scene.control.TreeItem;

/**
 * Utility class for binding-related code.
 */
public class BindUtil
{
    // Class Properties

    // - Internal Aggregators

    private static final DescendantFlatAggregator DESCENDANT_FLAT_AGGREGATOR = new DescendantFlatAggregator();
    private static final AncestorTreeAggregator ANCESTOR_TREE_AGGREGATOR = new AncestorTreeAggregator();
    private static final DescendantTreeAggregator DESCENDANT_TREE_AGGREGATOR = new DescendantTreeAggregator();

    /**
     * Extraction {@link Function} for binding a target to a {@link FlameGraph}.
     */
    public static final Function<Object, FlameGraph> FLAME_EXTRACTOR = o -> (FlameGraph)o;

    /**
     * Extraction {@link Function} for binding a target to the {@link Tree} result of aggregating the source
     * {@link Entry} with the {@link AncestorTreeAggregator}.
     */
    public static final Function<Object, Tree> ANCESTOR_TREE_EXTRACTOR = o ->
    {
        Entry entry = (Entry)o;
        if (o == null)
        {
            return null;
        }
        return ANCESTOR_TREE_AGGREGATOR.aggregate(entry);
    };

    /**
     * Extraction {@link Function} for binding a target to the {@link Tree} result of aggregating the source
     * {@link Entry} with the {@link DescendantTreeAggregator}.
     */
    public static final Function<Object, Tree> DESCENDANT_TREE_EXTRACTOR = o ->
    {
        Entry entry = (Entry)o;
        if (o == null)
        {
            return null;
        }
        return DESCENDANT_TREE_AGGREGATOR.aggregate(entry);
    };

    /**
     * Extraction {@link Function} for binding a target to the {@link Flat} result of aggregating the {@link Node}
     * contained in the source {@link TreeItem} with the {@link DescendantFlatAggregator}.
     */
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

    /**
     * Returns an extraction {@link Function} for binding a target to the {@link Flat} result of aggregating the source
     * {@link AggregationProfile} with the {@link FlatProfileAggregator} using the {@link CombinedGrouping} currently
     * selected in the specified {@link AbstractViewController}.
     * <p>
     * @param view the {@link AbstractViewController} which provides the {@link CombinedGrouping} for the aggregation
     * @return the extraction {@link Function}
     */
    public static final Function<Object, Flat> flatExtractor(AbstractViewController<?> view)
    {
        return new FlatExtractor(view.getGrouping());
    }

    /**
     * Returns an extraction {@link Function} for binding a target to the {@link Tree} result of aggregating the source
     * {@link AggregationProfile} with the {@link TreeProfileAggregator} using the {@link CombinedGrouping} currently
     * selected in the specified {@link AbstractViewController}.
     * <p>
     * @param view the {@link AbstractViewController} which provides the {@link CombinedGrouping} for the aggregation
     * @return the extraction {@link Function}
     */
    public static final Function<Object, Tree> treeExtractor(AbstractViewController<?> view)
    {
        return new TreeExtractor(view.getGrouping());
    }

    // Instance Constructors

    /**
     * Private Constructor for utility class.
     */
    private BindUtil()
    {
        // Private Constructor for utility class
    }
}
