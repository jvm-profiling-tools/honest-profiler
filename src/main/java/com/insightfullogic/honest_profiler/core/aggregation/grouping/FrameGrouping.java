package com.insightfullogic.honest_profiler.core.aggregation.grouping;

import java.util.function.BiFunction;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

public enum FrameGrouping implements BiFunction<AggregationProfile, LeanNode, String>
{
    BY_FQMN((profile, node) -> profile.getSource().getMethodInfoMap()
        .get(node.getFrame().getMethodId()).getFqmn()),
    BY_FQMN_LINENR((profile, node) -> profile.getSource().getFqmnPlusLineNr(node)),
    BY_BCI((profile, node) -> profile.getSource().getBciKey(node));

    private BiFunction<AggregationProfile, LeanNode, String> function;

    private FrameGrouping(BiFunction<AggregationProfile, LeanNode, String> function)
    {
        this.function = function;
    }

    @Override
    public String apply(AggregationProfile profile, LeanNode node)
    {
        return function.apply(profile, node);
    }
}
