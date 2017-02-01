package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Aggregator which takes an {@link AggregationProfile}, and uses the data to aggregate the values into trees of
 * {@link Node}s aggregated by FQMN.
 */
public class TreeByFqmnAggregator implements Aggregator<AggregationProfile, String, Node<String>>
{
    // Aggregator Implementation

    /**
     * Iterate over the top-level {@link LeanNode}s containing the thread-level information, and recursively traverse
     * the descendants, aggregating them on each level by FQMN.
     *
     * @see Aggregator#aggregate(Object, LeanNode)
     */
    @Override
    public Tree<String> aggregate(AggregationProfile source, AggregationProfile input)
    {
        List<Node<String>> nodes = new ArrayList<>();
        Tree<String> result = new Tree<>(source, nodes);

        LeanProfile profile = input.getSource();

        input.getSource().getThreads().forEach((threadId, threadNode) ->
        {
            // Create the top-level Node with the thread information
            Node<String> node = getThreadNode(result, profile, threadId, threadNode);
            node.setReference(input.getGlobalData());
            nodes.add(node);

            // Create the subtree with the method information, aggregated at each level by FQMN
            add(result, profile, node, threadNode, input.getGlobalData());
        });

        return result;
    }

    // Helper methods

    /**
     * Create a top-level {@link Node} containing the information aggregated at thread level.
     *
     * @param tree the {@link Tree} which is the result of the aggregation
     * @param profile the source {@link LeanProfile}
     * @param threadId the id of the thread
     * @param node the {@link LeanNode} containing the thread information
     * @return a {@link Node} containing the aggregated thread-level information
     */
    private Node<String> getThreadNode(Tree<String> tree, LeanProfile profile, Long threadId,
        LeanNode node)
    {
        return new Node<>(profile.getThreadName(threadId), node.getData(), tree);
    }

    /**
     * Aggregate the given {@link LeanNode} into the given {@link Node}. The aggregation is done though the
     * {@link Collectors#groupingBy(java.util.function.Function, Collector)} {@link Collector}, which groups the nodes
     * by FQMN. Each such group is aggregated using the {@link Collector} created in
     * {@link #getCollector(Tree, LeanProfile)}.
     *
     * @param tree the {@link Tree} which is the result of the aggregation
     * @param profile the source {@link LeanProfile}
     * @param parentNode the {@link Node} into which the input is being aggregated
     * @param parent the {@link LeanNode} being aggregated
     */
    private void add(Tree<String> tree, LeanProfile profile, Node<String> parentNode,
        LeanNode parent, NumericInfo reference)
    {
        Map<String, Node<String>> nodeMap = parent.getChildren().stream().collect(
            groupingBy(node -> profile.getFqmn(node), getCollector(tree, profile, reference)));
        parentNode.addAll(nodeMap);
    }

    /**
     * Provide the {@link Collector} for aggregating the groups in the
     * {@link Collectors#groupingBy(java.util.function.Function, Collector)} {@link Collector} in the
     * {@link #add(Tree, LeanProfile, Node, LeanNode)} method.
     *
     * Note that this {@link Collector} provides the recursion for the {@link #add(Tree, LeanProfile, Node, LeanNode)}
     * method in the accumulator.
     *
     * @param tree the {@link Tree} which is the result of the aggregation
     * @param profile the source {@link LeanProfile}
     * @return a {@link Collector} which accumulates {@link LeanNode}s into a {@link Node}.
     */
    private Collector<LeanNode, Node<String>, Node<String>> getCollector(Tree<String> tree,
        LeanProfile profile, NumericInfo reference)
    {
        // The key has to be specified in the update. I couldn't find a way to
        // easily or elegantly recuperate the String from the enclosing
        // groupingBy().
        return of(
            // Supplier
            () ->
            {
                Node<String> node = new Node<>(tree);
                node.setReference(reference);
                return node;
            },
            // Accumulator
            (accumulator, node) ->
            {
                accumulator.add(profile.getFqmn(node), node);
                add(tree, profile, accumulator, node, reference); // Recursion here !
            },
            // Combiner
            (e1, e2) -> e1.combine(e2));
    }
}
