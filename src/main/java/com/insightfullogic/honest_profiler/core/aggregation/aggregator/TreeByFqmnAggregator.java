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
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

public class TreeByFqmnAggregator implements Aggregator<AggregationProfile, List<AggregatedNode>>
{
    @Override
    public Aggregation<List<AggregatedNode>> aggregate(AggregationProfile input, LeanNode reference)
    {

        List<AggregatedNode> threadNodes = new ArrayList<>();
        Aggregation<List<AggregatedNode>> aggregation = new Aggregation<List<AggregatedNode>>(
            threadNodes,
            reference);

        input.getThreadRoots().forEach((threadId, threadNode) ->
        {
            AggregatedNode threadAggregatedNode = getThreadNode(
                aggregation,
                input.getSourceProfile(),
                threadId);
            threadNodes.add(threadAggregatedNode);
            add(aggregation, input.getSourceProfile(), threadAggregatedNode, threadNode);
        });

        return aggregation;
    }

    private AggregatedNode getThreadNode(Aggregation<List<AggregatedNode>> aggregation,
        LeanProfile profile, Long threadId)
    {
        String name = profile.getThreadMap().get(threadId).getName();
        NumericInfo data = profile.getThreadData().values().stream().findFirst().get().getData();
        AggregatedEntry threadEntry = new AggregatedEntry(name, data, aggregation);
        return new AggregatedNode(threadEntry);
    }

    private void add(Aggregation<List<AggregatedNode>> aggregation, LeanProfile profile,
        AggregatedNode parentAggregation, LeanNode parent)
    {
        Map<String, AggregatedNode> nodeMap = parent.getChildren().stream().collect(
            groupingBy(
                node -> profile.getMethodMap().get(node.getFrame().getMethodId()).getFqmn(),
                getAggrCollector(aggregation, profile)));
        parentAggregation.getChildren().addAll(nodeMap.values());
    }

    private Collector<LeanNode, AggregatedNode, AggregatedNode> getAggrCollector(
        Aggregation<List<AggregatedNode>> aggregation, LeanProfile profile)
    {
        // The key has to be specified in the update. I couldn't find a way to
        // easily or elegantly recuperate the String from the enclosing
        // groupingBy().
        return of(
            () -> new AggregatedNode(aggregation),
            (AggregatedNode entry, LeanNode node) ->
            {
                entry.add(
                    profile.getFqmn(node),
                    node.getFrame(),
                    node.getData());
                add(aggregation, profile, entry, node);
            },
            (AggregatedNode e1, AggregatedNode e2) -> e1.combine(e2));
    }
}
