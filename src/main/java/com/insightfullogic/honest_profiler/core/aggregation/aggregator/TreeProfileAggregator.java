package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;

/**
 * Aggregator which takes an {@link AggregationProfile}, and uses the data to aggregate the values into a {@link Tree}.
 */
public class TreeProfileAggregator implements ProfileAggregator<Node>
{
    // Aggregator Implementation

    /**
     * Aggregates an {@link AggregationProfile} into a {@link Tree}. The {@link CombinedGrouping} specifies which
     * {@link LeanNode}s are aggregated together.
     *
     * @see ProfileAggregator#aggregate(AggregationProfile, CombinedGrouping)
     */
    @Override
    public Tree aggregate(AggregationProfile input, CombinedGrouping grouping)
    {
        // Prepare result.
        Tree result = new Tree(input, grouping);

        LeanProfile profile = input.getSource();

        Map<String, Node> nodeMap = profile.getThreads().values().stream().collect(
            groupingBy(
                // Group LeanNodes by calculated key
                node -> grouping.apply(input, node),
                // Downstream collector, aggregates LeanNodes in a single group
                of(
                    // Supplier, creates an empty Node
                    () -> new Node(result),
                    // Accumulator, aggregates a LeanNode into the Node accumulator
                    (node, leanNode) ->
                    {
                        node.add(leanNode);
                        node.setKey(grouping.apply(input, leanNode));
                        // Aggregate descendants of a LeanNode recursively into children of accumulator. The recursion
                        // happens inside the Node.addChild() method, triggered by the boolean parameter.
                        leanNode.getChildren()
                            .forEach(child -> node.addChild(child, grouping, true));
                    },
                    // Combiner, combines two Nodes with the same key
                    (node1, node2) -> node1.combine(node2)
                )
            )
        );

        // Set the reference by default for all nodes to the global aggregation.
        // We do this here because the addChild() method doesn't propagate the reference, and proably shouldn't.
        nodeMap.values().stream().flatMap(Node::flatten).forEach(node ->
        {
            node.setReference(input.getGlobalData());
        });

        result.getData().addAll(nodeMap.values());

        return result;
    }
}
