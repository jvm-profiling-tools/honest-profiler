package com.insightfullogic.honest_profiler.core;

import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping.combine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.TreeProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;

public class TreeGenerator extends LeanLogCollectorDriver
{
    // Instance Properties

    private CombinedGrouping grouping;

    private Tree tree;

    // Instance Constructors

    public TreeGenerator(ThreadGrouping threadGrouping, FrameGrouping frameGrouping)
    {
        grouping = combine(threadGrouping, frameGrouping);

        reset();
    }

    // Instance Accessors

    private Node getNode(String... keys)
    {
        if (keys == null || keys.length == 0)
        {
            return null;
        }

        List<Node> children = tree.getData();
        Optional<Node> result = null;

        for (String key : keys)
        {
            result = children.stream().filter(node -> key.equals(node.getKey())).findFirst();

            if (!result.isPresent())
            {
                return null;
            }

            children = result.get().getChildren();
        }
        return result.get();
    }

    // LeanProfileLister Implementation

    @Override
    public void accept(LeanProfile profile)
    {
        tree = new TreeProfileAggregator().aggregate(new AggregationProfile(profile), grouping);
    }

    // Assertions

    public void assertAggregationSizeEquals(int size)
    {
        assertEquals("Wrong size of the Tree aggregation.", size, tree.flatten().count());
    }

    public void assertContains(int selfCount, int totalCount, long selfTime, long totalTime,
        String... keys)
    {
        Node node = getNode(keys);

        assertNotNull("No node found with keys " + Arrays.toString(keys), node);

        assertEquals(
            "Wrong self count for entry " + Arrays.toString(keys),
            selfCount,
            node.getSelfCnt());
        assertEquals(
            "Wrong total count for entry " + Arrays.toString(keys),
            totalCount,
            node.getTotalCnt());
        assertEquals(
            "Wrong self time for entry " + Arrays.toString(keys),
            selfTime,
            node.getSelfTime());
        assertEquals(
            "Wrong total time for entry " + Arrays.toString(keys),
            totalTime,
            node.getTotalTime());
    }

    public void assertContains(int selfCount, int totalCount, String... keys)
    {
        assertContains(selfCount, totalCount, nano(selfCount), nano(totalCount), keys);
    }
}
