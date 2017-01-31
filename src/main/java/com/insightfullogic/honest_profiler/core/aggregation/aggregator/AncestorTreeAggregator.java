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
public class AncestorTreeAggregator implements Aggregator<Entry<String>, String, Node<String>>
{
    // Aggregator Implementation

    /**
     * @see Aggregator#aggregate(Object, LeanNode)
     */
    @Override
    public Tree<String> aggregate(AggregationProfile source, Entry<String> input,
        LeanNode reference)
    {
        Node<String> root = new Node<>(input, new ArrayList<>());
        List<Node<String>> list = new ArrayList<>();
        list.add(root);

        Tree<String> result = new Tree<>(source, list, reference);
        Set<String> processed = new HashSet<>();

        addAncestors(source, root, result, processed);
        return result;
    }

    private void addAncestors(AggregationProfile source, Node<String> parent, Tree<String> tree,
        Set<String> processed)
    {
        FqmnLink fqmnLink = source.getFqmnLinks().get(parent.getKey());
        Map<String, Node<String>> callers = fqmnLink.getParents().values().stream()
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

    private Collector<LeanNode, Node<String>, Node<String>> getCollector(AggregationProfile source,
        Tree<String> tree, Set<String> processed)
    {
        return of(
            // Supplier
            () -> new Node<>(tree),
            // Accumulator
            (accumulator, node) ->
            {
                String fqmn = source.getSource().getFqmn(node);
                processed.add(fqmn);
                accumulator.add(fqmn, node);
                // Wrong, will potentially add to the accumulator several times which leads to double-counting
                addAncestors(source, accumulator, tree, processed); // Recursion here !
            },
            // Combiner
            (e1, e2) -> e1.combine(e2));
    }
}
