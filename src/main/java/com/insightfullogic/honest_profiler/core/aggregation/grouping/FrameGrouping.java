package com.insightfullogic.honest_profiler.core.aggregation.grouping;

import java.util.function.BiFunction;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * A FrameGrouping describes how a collection of {@link LeanNode}s describing frames can be partitioned for aggregation.
 * The grouping maps each {@link LeanNode} to a String key, and {@link LeanNode}s with the same key will be aggregated
 * together.
 * <p>
 * Every FrameGrouping contains a name for front-end display purposes, and wraps a {@link BiFunction} which maps an
 * {@link AggregationProfile} and a {@link LeanNode} to the String key. The {@link AggregationProfile} is needed because
 * the {@link LeanNode} only contains the method Id, which has to be mapped to an actual name using the
 * {@link AggregationProfile} method info map.
 */
public enum FrameGrouping implements BiFunction<AggregationProfile, LeanNode, String>
{
    /**
     * Group frames by Fully Qualified Method Name. The constructed key is "[Fully Qualified ClassName].[Method Name]".
     */
    BY_FQMN("By FQMN", (profile, node) -> profile.getSource().getFqmn(node)),
    /**
     * Group frames by Fully Qualified Method Name and line number. The constructed key is "[Fully Qualified
     * ClassName].[Method Name]:[Line Number]".
     */
    BY_FQMN_LINENR("By FQMN + Line Nr", (profile, node) -> profile.getSource().getFqmnPlusLineNr(node)),
    /**
     * Group frames by Fully Qualified Method Name and BCI (byte code index). The constructed key is "[Fully Qualified
     * ClassName].[Method Name]:[BCI]".
     */
    BY_BCI("By FQMN + BCI", (profile, node) -> profile.getSource().getBciKey(node)),
    /**
     * Group frames by Method Id and Fully Qualified Method Name. The constructed key is "([Method Id]) [Fully Qualified
     * ClassName].[Method Name]".
     */
    BY_METHOD_ID("By Method ID", (profile, node) -> profile.getSource().getMethodIdKey(node));

    // Instance Properties

    private String name;
    private BiFunction<AggregationProfile, LeanNode, String> function;

    // Instance Constructors

    private FrameGrouping(String name, BiFunction<AggregationProfile, LeanNode, String> function)
    {
        this.name = name;
        this.function = function;
    }

    // BiFunction Implementation

    @Override
    public String apply(AggregationProfile profile, LeanNode node)
    {
        return function.apply(profile, node);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
