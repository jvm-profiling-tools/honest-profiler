package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;

public class TreeByFqmnAggregator implements Aggregator<AggregationProfile, String, Node<String>>
{
    @Override
    public Tree<String> aggregate(AggregationProfile input, LeanNode reference)
    {

        List<Node<String>> nodes = new ArrayList<>();
        Tree<String> result = new Tree<>(nodes, reference);

        LeanProfile source = input.getSource();

        input.getSource().getThreads().forEach((threadId, threadNode) ->
        {
            Node<String> node = getThreadNode(result, source, threadId, threadNode);
            nodes.add(node);
            add(result, source, node, threadNode);
        });

        return result;
    }

    private Node<String> getThreadNode(Tree<String> tree, LeanProfile profile, Long threadId,
        LeanNode node)
    {
        Entry<String> entry = new Entry<>(profile.getThreadName(threadId), node.getData(), tree);
        return new Node<>(entry);
    }

    private void add(Tree<String> tree, LeanProfile profile, Node<String> parentNode,
        LeanNode parent)
    {
        Map<String, Node<String>> nodeMap = parent.getChildren().stream().collect(
            groupingBy(node -> profile.getFqmn(node), getCollector(tree, profile)));
        parentNode.getChildren().addAll(nodeMap.values());
    }

    private Collector<LeanNode, Node<String>, Node<String>> getCollector(Tree<String> tree,
        LeanProfile profile)
    {
        // The key has to be specified in the update. I couldn't find a way to
        // easily or elegantly recuperate the String from the enclosing
        // groupingBy().
        return of(
            () -> new Node<>(tree),
            (entry, node) ->
            {
                entry.add(profile.getFqmn(node), node.getFrame(), node.getData());
                add(tree, profile, entry, node);
            },
            (e1, e2) -> e1.combine(e2));
    }
}
