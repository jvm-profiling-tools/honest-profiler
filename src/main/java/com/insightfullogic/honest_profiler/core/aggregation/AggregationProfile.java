package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.aggregator.Aggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatByFqmnAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.TreeByFqmnAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Class which lazily calculates aggregated views.
 */
public class AggregationProfile
{
    private static final Aggregator<AggregationProfile, String, Entry<String>> flatAggregator = new FlatByFqmnAggregator();
    private static final Aggregator<AggregationProfile, String, Node<String>> treeAggregator = new TreeByFqmnAggregator();

    private final LeanProfile source;
    private final Map<String, FqmnLink> fqmnLinks;

    private final LeanNode profileNode;

    private Map<Aggregator<?, ?, ?>, Aggregation<?, ?>> cachedAggregations;

    public AggregationProfile(LeanProfile sourceProfile)
    {
        source = sourceProfile;
        fqmnLinks = new HashMap<>();
        profileNode = new LeanNode(null, null);
        cachedAggregations = new HashMap<>();

        aggregateTopLevel();
        calculateLinks();
    }

    public LeanProfile getSource()
    {
        return source;
    }

    public NumericInfo getProfileData()
    {
        return profileNode.getData();
    }

    public FqmnLink getFqmnLink(LeanNode node)
    {
        return fqmnLinks.get(source.getMethodMap().get(node.getFrame().getMethodId()).getFqmn());
    }

    public Map<String, FqmnLink> getFqmnLinks()
    {
        return fqmnLinks;
    }

    @SuppressWarnings("unchecked")
    public Flat<String> getFlat()
    {
        cachedAggregations.putIfAbsent(flatAggregator, flatAggregator.aggregate(this, profileNode));
        return (Flat<String>) cachedAggregations.get(flatAggregator);
    }

    @SuppressWarnings("unchecked")
    public Tree<String> getTree()
    {
        cachedAggregations.putIfAbsent(treeAggregator, treeAggregator.aggregate(this, profileNode));
        return (Tree<String>) cachedAggregations.get(treeAggregator);
    }

    private void aggregateTopLevel()
    {
        NumericInfo aggregated = source.getThreads().values().stream().collect(
            NumericInfo::new,
            (data, node) -> data.add(node.getData()),
            (data1, data2) -> data1.add(data2));
        profileNode.getData().add(aggregated);
    }

    private void calculateLinks()
    {
        source.getThreads()
            .forEach((id, data) -> data.getChildren().forEach(node -> link(id, node)));
    }

    private void link(Long threadId, LeanNode node)
    {
        String fqmn = source.getMethodMap().get(node.getFrame().getMethodId()).getFqmn();
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

        node.getChildren().forEach(child -> link(threadId, child));
    }
}
