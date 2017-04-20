package com.insightfullogic.honest_profiler.core;

import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping.combine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;

public class FlatGenerator extends LeanLogCollectorDriver
{
    // Instance Properties

    private CombinedGrouping grouping;

    private Flat flat;

    // Instance Constructors

    public FlatGenerator(ThreadGrouping threadGrouping, FrameGrouping frameGrouping)
    {
        grouping = combine(threadGrouping, frameGrouping);

        reset();
    }

    // LeanProfileLister Implementation

    @Override
    public void accept(LeanProfile profile)
    {
        flat = new FlatProfileAggregator().aggregate(new AggregationProfile(profile), grouping);
    }

    // Assertions

    public void assertAggregationSizeEquals(int size)
    {
        assertEquals("Wrong size of the Flat aggregation.", size, flat.getData().size());
    }

    public void assertContains(String key, int selfCount, int totalCount, long selfTime,
        long totalTime)
    {
        Optional<Entry> result = flat.getData().stream().filter(entry -> key.equals(entry.getKey()))
            .findFirst();

        assertTrue("No entry found with key " + key, result.isPresent());

        Entry entry = result.get();

        assertEquals("Wrong self count for entry " + key, selfCount, entry.getSelfCnt());
        assertEquals("Wrong total count for entry " + key, totalCount, entry.getTotalCnt());
        assertEquals("Wrong self time for entry " + key, selfTime, entry.getSelfTime());
        assertEquals("Wrong total time for entry " + key, totalTime, entry.getTotalTime());
    }

    public void assertContains(String key, int selfCount, int totalCount)
    {
        assertContains(key, selfCount, totalCount, nano(selfCount), nano(totalCount));
    }
}
