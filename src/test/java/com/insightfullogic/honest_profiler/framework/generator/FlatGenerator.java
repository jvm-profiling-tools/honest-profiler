package com.insightfullogic.honest_profiler.framework.generator;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping.combine;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.nano;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.framework.LeanLogCollectorDriver;

public class FlatGenerator extends LeanLogCollectorDriver
{
    // Class Methods

    public static final void assertAggregationSizeEquals(Flat flat, int size)
    {
        assertEquals("Wrong size of the Flat aggregation.", size, flat.getData().size());
    }

    public static final void assertContains(Flat flat, String key, int selfCount, int totalCount,
        long selfTime, long totalTime)
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

    public static final void assertContains(Flat flat, String key, int selfCount, int totalCount)
    {
        assertContains(flat, key, selfCount, totalCount, nano(selfCount), nano(totalCount));
    }

    public static final Entry getEntry(Flat flat, String key)
    {
        return flat.getData().stream().filter(entry -> key.equals(entry.getKey())).findFirst()
            .get();
    }

    // Instance Properties

    private CombinedGrouping grouping;

    private Flat flat;

    // Instance Constructors

    public FlatGenerator(ThreadGrouping threadGrouping, FrameGrouping frameGrouping)
    {
        grouping = combine(threadGrouping, frameGrouping);

        reset();
    }

    // Instance accessors

    public Flat getFlat()
    {
        return flat;
    }

    public Entry getEntry(String key)
    {
        return getEntry(flat, key);
    }

    // Filter delegation

    public Flat filter(FilterSpecification<Entry> filter)
    {
        return flat.filter(filter);
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
        assertAggregationSizeEquals(flat, size);
    }

    public void assertContains(String key, int selfCount, int totalCount, long selfTime,
        long totalTime)
    {
        assertContains(flat, key, selfCount, totalCount, selfTime, totalTime);
    }

    public void assertContains(String key, int selfCount, int totalCount)
    {
        assertContains(flat, key, selfCount, totalCount);
    }
}
