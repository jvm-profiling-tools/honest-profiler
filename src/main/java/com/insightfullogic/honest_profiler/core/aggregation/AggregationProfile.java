package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;

/**
 * Class which lazily calculates aggregated views.
 */
public class AggregationProfile
{
    private final LeanProfile sourceProfile;
    private final Map<String, FqmnLink> fqmnLinks;
    private final Map<Long, LeanNode> threadRoots;

    public AggregationProfile(LeanProfile sourceProfile)
    {
        this.sourceProfile = sourceProfile;

        fqmnLinks = new HashMap<>();
        threadRoots = new HashMap<>();

        calculateLinks();
    }

    public LeanProfile getSourceProfile()
    {
        return sourceProfile;
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

    private void calculateLinks()
    {
        sourceProfile.getThreadData().forEach((threadId, threadData) ->
        {
            threadData.getChildren().forEach(
                node -> threadRoots.put(threadId, link(threadId, node)));
        });
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
