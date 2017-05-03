package com.insightfullogic.honest_profiler.core.aggregation.grouping;

import static java.util.Objects.hash;

import java.util.function.BiFunction;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;

/**
 * A CombinedGrouping is a utility class for combining a {@link ThreadGrouping} and a {@link FrameGrouping}. It can be
 * applied to any {@link LeanNode} in a collection irrespective of whether it is a {@link LeanThreadNode} or not.
 */
public class CombinedGrouping implements BiFunction<AggregationProfile, LeanNode, String>
{
    /**
     * Factory method for creating CombinedGroupings.
     * <p>
     * @param threadGrouping the {@link ThreadGrouping} in the resulting CombinedGrouping
     * @param frameGrouping the {@link FrameGrouping} in the resulting CombinedGrouping
     * @return a CombinedGrouping wrapping the specified {@link ThreadGrouping} and {@link FrameGrouping}/
     */
    public static final CombinedGrouping combine(ThreadGrouping threadGrouping,
        FrameGrouping frameGrouping)
    {
        return new CombinedGrouping(threadGrouping, frameGrouping);
    }

    // Instance Properties

    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;

    // Instance Accessors

    public ThreadGrouping getThreadGrouping()
    {
        return threadGrouping;
    }

    public FrameGrouping getFrameGrouping()
    {
        return frameGrouping;
    }

    // Instance Constructors

    private CombinedGrouping(ThreadGrouping threadGrouping, FrameGrouping frameGrouping)
    {
        super();
        this.threadGrouping = threadGrouping;
        this.frameGrouping = frameGrouping;
    }

    // BiFunction implementation

    /**
     * Calculate the key for a {@link LeanNode}, based on the provided groupings.
     * <p>
     * @param profile the input {@link AggregationProfile}
     * @param node the {@link LeanNode} for which the key will be calculated
     * @return the calculated key
     */
    @Override
    public String apply(AggregationProfile profile, LeanNode node)
    {
        return (node instanceof LeanThreadNode) ? threadGrouping.apply((LeanThreadNode)node)
            : frameGrouping.apply(profile, node);
    }

    // Object implementation

    @Override
    public boolean equals(Object other)
    {
        return (other instanceof CombinedGrouping)
            && (threadGrouping == ((CombinedGrouping)other).threadGrouping)
            && (frameGrouping == ((CombinedGrouping)other).frameGrouping);
    }

    @Override
    public int hashCode()
    {
        return hash(threadGrouping, frameGrouping);
    }

    @Override
    public String toString()
    {
        return threadGrouping + " - " + frameGrouping;
    }
}
