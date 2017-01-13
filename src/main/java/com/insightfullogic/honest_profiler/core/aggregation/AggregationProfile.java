package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.lean.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;

/**
 * Class which lazily calculates aggregated views.
 */
public class AggregationProfile
{
    private final LeanProfile sourceProfile;
    private final Map<String, FqmnLink> fqmnLinks;
    private final Map<Long, LinkedLeanNode> threadRoots;

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

    public Map<String, FqmnLink> getFqmnLinks()
    {
        return fqmnLinks;
    }

    public Map<Long, LinkedLeanNode> getThreadRoots()
    {
        return threadRoots;
    }

    private void calculateLinks()
    {
        sourceProfile.getThreadData().forEach((threadId, threadData) ->
        {
            threadData.getChildren().forEach(
                (frame, node) -> threadRoots.put(threadId, link(threadId, frame, node, null)));
        });
    }

    private LinkedLeanNode link(Long threadId, FrameInfo frame, LeanNode node,
        LinkedLeanNode parent)
    {
        String fqmn = sourceProfile.getMethodMap().get(frame.getMethodId()).getFqmn();
        FqmnLink link = fqmnLinks.computeIfAbsent(fqmn, FqmnLink::new);

        LinkedLeanNode linkedNode = new LinkedLeanNode(node, parent, link);
        link.addSibling(threadId, linkedNode);

        if (parent != null)
        {
            // Add Linked Node parent-child relation
            parent.addChild(linkedNode);

            // Add FQMN Link Parent-Child relations
            link.addParent(threadId, parent);
            parent.getFqmnLink().addChild(threadId, linkedNode);
        }

        node.getChildren().forEach((childFrame, childNode) ->
        {
            linkedNode.addChild(link(threadId, childFrame, childNode, linkedNode));
        });

        return linkedNode;
    }
}
