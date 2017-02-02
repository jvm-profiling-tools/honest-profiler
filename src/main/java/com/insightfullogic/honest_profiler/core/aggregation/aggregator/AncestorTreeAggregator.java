package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.FqmnLink;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Aggregator which takes an {@link Entry} and aggregates the ancestors into a {@link Tree}.
 */
public class AncestorTreeAggregator implements SubAggregator<Entry, Node>
{
    // Aggregator Implementation

    /**
     * @see SubAggregator#aggregate(Object, LeanNode)
     */
    @Override
    public Tree aggregate(AggregationProfile source, Entry input)
    {
        Node root = new Node(input);
        List<Node> list = new ArrayList<>();
        list.add(root);

        Tree result = new Tree(source, list);
        Set<String> processed = new HashSet<>();

        addAncestors(source, root, result, processed);
        return result;
    }

    private void addAncestors(AggregationProfile source, Node parent, Tree tree,
        Set<String> processed)
    {
        FqmnLink fqmnLink = source.getFqmnLinks().get(parent.getKey());
        Map<String, Node> callers = fqmnLink.getParents().values().stream()
            .flatMap(set -> set.stream()).filter(
                node -> node != null
                    && !node.isThreadNode()
                    && !processed.contains(source.getSource().getFqmn(node)))
            .collect(
                groupingBy(
                    node -> source.getSource().getFqmn(node),
                    getCollector(source, tree, processed)));
        parent.addAll(callers);
    }

    private Collector<LeanNode, Node, Node> getCollector(AggregationProfile source, Tree tree,
        Set<String> processed)
    {
        return of(
            // Supplier
            () ->
            {
                Node node = new Node(tree);
                node.setReference(source.getGlobalData());
                return node;
            },
            // Accumulator
            (accumulator, node) ->
            {
                String fqmn = source.getSource().getFqmn(node);
                processed.add(fqmn);
                accumulator.add(fqmn, node);
                if (accumulator.getChildren().isEmpty())
                {
                    addAncestors(source, accumulator, tree, processed); // Recursion here !
                }
            },
            // Combiner
            (e1, e2) -> e1.combine(e2));
    }
}
