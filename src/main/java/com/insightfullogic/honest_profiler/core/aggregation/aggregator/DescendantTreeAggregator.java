package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Aggregator which takes an {@link Entry} and aggregates the descendants of all the {@link LeanNode}s aggregated by
 * that {@link Entry} into a {@link Tree}.
 */
public class DescendantTreeAggregator implements SubAggregator<Entry, Node>
{
    // Aggregator Implementation

    /**
     * @see SubAggregator#aggregate(Object)
     */
    @Override
    public Tree aggregate(Entry input)
    {
        Aggregation<Keyed<String>> aggregation = input.getAggregation();
        AggregationProfile source = aggregation.getSource();
        CombinedGrouping grouping = aggregation.getGrouping();

        Tree result = new Tree(source, input.getAggregation().getGrouping());

        Node root = new Node(input);
        result.getData().add(root);

        addChildren(source, root, result, grouping);
        return result;
    }

    /**
     * Recursive method for aggregating the children (and descendants) of the {@link LeanNode}s which are aggregated by
     * the provided {@link Node}.
     * <p>
     * @param source the original {@link AggregationProfile}
     * @param child the input {@link Node}
     * @param tree the resulting {@link Tree}
     * @param grouping the key calculation grouping
     */
    private void addChildren(AggregationProfile source, Node child, Tree tree,
        CombinedGrouping grouping)
    {
        Map<String, Node> result = child.getAggregatedNodes().stream()
            // Create a stream of all LeanNodes aggregated by the Entry, and aggregate according to the Grouping
            .flatMap(node -> node.getChildren().stream())
            .collect(groupingBy(
                // Group LeanNodes by calculated key
                node -> grouping.apply(source, node),
                // Downstream collector, collects LeanNodes in a single group
                of(
                    // Supplier, creates an empty Node
                    () ->
                    {
                        Node node = new Node(tree);
                        // Set the reference by default for all nodes to the global aggregation.
                        node.setReference(source.getGlobalData());
                        return node;
                    },
                    // Accumulator, aggregates a LeanNode into the Node accumulator
                    (x, y) ->
                    {
                        x.add(y);
                        x.setKey(grouping.apply(source, y));
                    },
                    // Combiner, combines two Nodes with the same key
                    (x, y) -> x.combine(y)
                )
            ));

        // Add the aggregated children as children to the Node, and recurse
        result.values().forEach(parent ->
        {
            child.addChild(parent);
            // Recursively add descendants
            addChildren(source, parent, tree, grouping);
        });
    }
}
