package com.insightfullogic.honest_profiler.core.aggregation.grouping;

import java.util.function.BiFunction;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;

public class CombinedGrouping implements BiFunction<AggregationProfile, LeanNode, String>
{
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;

    public CombinedGrouping(ThreadGrouping threadGrouping, FrameGrouping frameGrouping)
    {
        super();
        this.threadGrouping = threadGrouping;
        this.frameGrouping = frameGrouping;
    }

    /**
     * Calculate the key for a {@link LeanNode}, based on the provided groupings.
     *
     * @param profile the input {@link AggregationProfile}
     * @param node the {@link LeanNode} for which the key will be calculated
     * @return
     */
    @Override
    public String apply(AggregationProfile profile, LeanNode node)
    {
        return (node instanceof LeanThreadNode) ? threadGrouping.apply((LeanThreadNode)node)
            : frameGrouping.apply(profile, node);
    }
}
