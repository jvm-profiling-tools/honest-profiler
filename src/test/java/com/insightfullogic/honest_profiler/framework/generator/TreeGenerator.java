package com.insightfullogic.honest_profiler.framework.generator;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping.combine;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.nano;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.TreeProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.framework.LeanLogCollectorDriver;

public class TreeGenerator extends LeanLogCollectorDriver
{
    // Class Methods

    public static final void assertAggregationSizeEquals(Tree tree, int size)
    {
        assertEquals("Wrong size of the Tree aggregation.", size, tree.flatten().count());
    }

    public static final void assertContains(Tree tree, int selfCount, int totalCount, long selfTime,
        long totalTime, String... keys)
    {
        Node node = getNode(tree, keys);

        assertNotNull(
            "No node found with keys " + Arrays.toString(keys) + " (" + tree.getGrouping() + ")",
            node);

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

    public static final void assertContains(Tree tree, int selfCount, int totalCount,
        String... keys)
    {
        assertContains(tree, selfCount, totalCount, nano(selfCount), nano(totalCount), keys);
    }

    public static final Node getNode(Tree tree, String... keys)
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

    public Tree getTree()
    {
        return tree;
    }

    public Node getNode(String... keys)
    {
        return getNode(tree, keys);
    }

    // Filter delegation

    public Tree filter(FilterSpecification<Node> filter)
    {
        return tree.filter(filter);
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
        assertAggregationSizeEquals(tree, size);
    }

    public void assertContains(int selfCount, int totalCount, long selfTime, long totalTime,
        String... keys)
    {
        assertContains(tree, selfCount, totalCount, selfTime, totalTime, keys);
    }

    public void assertContains(int selfCount, int totalCount, String... keys)
    {
        assertContains(tree, selfCount, totalCount, keys);
    }
}
