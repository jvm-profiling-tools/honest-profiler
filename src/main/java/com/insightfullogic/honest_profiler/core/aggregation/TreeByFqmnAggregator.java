package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

public class TreeByFqmnAggregator implements Aggregator<AggregationProfile, List<AggregatedNode>>
{

    @Override
    public List<AggregatedNode> aggregate(AggregationProfile input, NumericInfo reference)
    {
        List<AggregatedNode> threadNodes = new ArrayList<>();

        input.getThreadRoots().forEach((threadId, threadLinkedNode) ->
        {
            AggregatedNode threadNode = getThreadNode(input.getSourceProfile(), threadId);
            threadNodes.add(threadNode);

            // TODO...
        });

        return threadNodes;
    }

    private AggregatedNode getThreadNode(LeanProfile profile, Long threadId)
    {
        String name = profile.getThreadMap().get(threadId).getName();
        NumericInfo data = profile.getThreadData().values().stream().findFirst().get().getData();
        AggregatedEntry threadEntry = new AggregatedEntry(name, data, data);
        return new AggregatedNode(threadEntry);
    }
}
