package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.aggregator.Aggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatByFqmnAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.TreeByFqmnAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Class which lazily calculates aggregated views.
 */
public class AggregationProfile
{
    private static final Aggregator<AggregationProfile, List<AggregatedEntry>> flatAggregator = new FlatByFqmnAggregator();
    private static final Aggregator<AggregationProfile, List<AggregatedNode>> treeAggregator = new TreeByFqmnAggregator();

    private final LeanProfile sourceProfile;
    private final Map<String, FqmnLink> fqmnLinks;
    private final Map<Long, LeanNode> threadRoots;

    private final LeanNode profileNode;

    private Aggregation<List<AggregatedEntry>> flatAggregation;
    private Aggregation<List<AggregatedNode>> treeAggregation;

    public AggregationProfile(LeanProfile sourceProfile)
    {
        this.sourceProfile = sourceProfile;

        fqmnLinks = new HashMap<>();
        threadRoots = new HashMap<>();

        this.profileNode = new LeanNode(null, null);

        aggregateTopLevel();
        calculateLinks();
        aggregate();
    }

    public LeanProfile getSourceProfile()
    {
        return sourceProfile;
    }

    public NumericInfo getProfileData()
    {
        return profileNode.getData();
    }

    public FqmnLink getFqmnLink(LeanNode node)
    {
        return fqmnLinks
            .get(sourceProfile.getMethodMap().get(node.getFrame().getMethodId()).getFqmn());
    }

    public Map<String, FqmnLink> getFqmnLinks()
    {
        return fqmnLinks;
    }

    public Map<Long, LeanNode> getThreadRoots()
    {
        return threadRoots;
    }

    public Aggregation<List<AggregatedEntry>> getFlatAggregation()
    {
        return flatAggregation;
    }

    public Aggregation<List<AggregatedNode>> getTreeAggregation()
    {
        return treeAggregation;
    }

    private void aggregate()
    {
        flatAggregation = flatAggregator.aggregate(this, profileNode);
        treeAggregation = treeAggregator.aggregate(this, profileNode);
    }

    private void aggregateTopLevel()
    {
        NumericInfo aggregated = threadRoots.values().stream().collect(
            NumericInfo::new,
            (data, node) -> data.add(node.getData()),
            (data1, data2) -> data1.add(data2));
        profileNode.getData().add(aggregated);
    }

    private void calculateLinks()
    {
        sourceProfile.getThreadData()
            .forEach((threadId, threadData) -> threadData.getChildren().forEach(
                node -> threadRoots.put(threadId, link(threadId, node))));
    }

    private LeanNode link(Long threadId, LeanNode node)
    {
        String fqmn = sourceProfile.getMethodMap().get(node.getFrame().getMethodId()).getFqmn();
        FqmnLink link = fqmnLinks.computeIfAbsent(fqmn, FqmnLink::new);

        link.addSibling(threadId, node);

        LeanNode parent = node.getParent();
        if (parent != null)
        {
            // Add FQMN Link Parent-Child relations
            link.addParent(threadId, parent);
            if (parent.getFrame() != null)
            {
                getFqmnLink(parent).addChild(threadId, node);
            }
        }
        return node;
    }
}
