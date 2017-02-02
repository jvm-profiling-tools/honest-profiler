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
        Tree result = new Tree(input);

        LeanProfile profile = input.getSource();

        Map<String, Node> nodeMap = profile.getThreads().values().stream().collect(
            groupingBy(
                // Group LeanNodes by calculated key
                node -> grouping.apply(input, node),
                // Downstream collector, collects LeanNodes in a single group
                of(
                    // Supplier, creates an empty Node
                    () -> new Node(result),
                    // Accumulator, aggregates a LeanNode into the Entry accumulator
                    (x, y) ->
                    {
                        x.add(y);
                        x.setKey(grouping.apply(input, y));
                        y.getChildren().forEach(child -> x.addChild(child, grouping));
                    },
                    // Combiner, combines two Entries with the same key
                    (x, y) -> x.combine(y)
                )
            )
        );

        // Set the reference by default for all nodes to the global aggregation.
        nodeMap.values().stream().flatMap(Node::flatten).forEach(node ->
        {

            node.setReference(input.getGlobalData());
        });

        result.getData().addAll(nodeMap.values());

        return result;
    }
}
