package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;

public class TreeByFqmnAggregator
    implements Aggregator<AggregationProfile, String, AggregatedNode<String>>
{
    @Override
    public Aggregation<String, AggregatedNode<String>> aggregate(AggregationProfile input,
        LeanNode reference)
    {

        List<AggregatedNode<String>> nodes = new ArrayList<>();
        Aggregation<String, AggregatedNode<String>> result = new Aggregation<>(nodes, reference);

        LeanProfile source = input.getSource();

        input.getSource().getThreads().forEach((threadId, threadNode) ->
        {
            AggregatedNode<String> node = getThreadNode(result, source, threadId, threadNode);
            nodes.add(node);
            add(result, source, node, threadNode);
        });

        return result;
    }

    private AggregatedNode<String> getThreadNode(
        Aggregation<String, AggregatedNode<String>> aggregation, LeanProfile profile, Long threadId,
        LeanNode node)
    {
        AggregatedEntry<String> entry = new AggregatedEntry<>(
            profile.getThreadName(threadId),
            node.getData(),
            aggregation);
        return new AggregatedNode<>(entry);
    }

    private void add(Aggregation<String, AggregatedNode<String>> aggregation, LeanProfile profile,
        AggregatedNode<String> parentAggregation, LeanNode parent)
    {
        Map<String, AggregatedNode<String>> nodeMap = parent.getChildren().stream().collect(
            groupingBy(
                node -> profile.getFqmn(node),
                getAggrCollector(aggregation, profile)));
        parentAggregation.getChildren().addAll(nodeMap.values());
    }

    private Collector<LeanNode, AggregatedNode<String>, AggregatedNode<String>> getAggrCollector(
        Aggregation<String, AggregatedNode<String>> aggregation, LeanProfile profile)
    {
        // The key has to be specified in the update. I couldn't find a way to
        // easily or elegantly recuperate the String from the enclosing
        // groupingBy().
        return of(
            () -> new AggregatedNode<>(aggregation),
            (entry, node) ->
            {
                entry.add(
                    profile.getFqmn(node),
                    node.getFrame(),
                    node.getData());
                add(aggregation, profile, entry, node);
            },
            (e1, e2) -> e1.combine(e2));
    }
}
