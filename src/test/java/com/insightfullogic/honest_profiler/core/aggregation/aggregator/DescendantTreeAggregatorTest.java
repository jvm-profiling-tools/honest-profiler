package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static com.insightfullogic.honest_profiler.framework.AggregationUtil.keyFor;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.keysFor;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.SCENARIOS;
import static com.insightfullogic.honest_profiler.framework.generator.TreeGenerator.assertAggregationSizeEquals;
import static com.insightfullogic.honest_profiler.framework.generator.TreeGenerator.assertContains;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.framework.generator.FlatGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;

public class DescendantTreeAggregatorTest
{
    @Test
    public void testDescendantTreeAggregator()
    {

        for (ThreadGrouping tg : ThreadGrouping.values())
        {
            for (FrameGrouping fg : FrameGrouping.values())
            {
                checkLeafDescendants(tg, fg);
                checkMultipleDescendantsSingleThread(tg, fg);
                checkMultipleDescendantsTwoThreads(tg, fg);
            }
        }
    }

    // Descendants of leaf node
    private void checkLeafDescendants(ThreadGrouping tg, FrameGrouping fg)
    {
        Tree tree = get(SCENARIOS.get(0), tg, fg, F_01);

        assertAggregationSizeEquals(tree, 1);
        assertContains(tree, 1, 1, keysFor(fg, F_01));
    }

    // Descendants of intermediate node
    private void checkMultipleDescendantsSingleThread(ThreadGrouping tg, FrameGrouping fg)
    {
        Tree tree = get(SCENARIOS.get(1), tg, fg, F_03);

        assertAggregationSizeEquals(tree, 3);
        assertContains(tree, 0, 1, keysFor(fg, F_03));
        assertContains(tree, 0, 1, keysFor(fg, F_03, F_02));
        assertContains(tree, 1, 1, keysFor(fg, F_03, F_02, F_01));
    }

    // Descendants of intermediate node
    private void checkMultipleDescendantsTwoThreads(ThreadGrouping tg, FrameGrouping fg)
    {
        Tree tree = get(SCENARIOS.get(5), tg, fg, F_03);

        assertAggregationSizeEquals(tree, 3);
        assertContains(tree, 0, 2, keysFor(fg, F_03));
        assertContains(tree, 0, 2, keysFor(fg, F_03, F_02));
        assertContains(tree, 2, 2, keysFor(fg, F_03, F_02, F_01));
    }

    private Tree get(SimplifiedLogScenario scenario, ThreadGrouping tg, FrameGrouping fg,
        StackFrame frame)
    {
        DescendantTreeAggregator aggregator = new DescendantTreeAggregator();
        FlatGenerator gen = new FlatGenerator(tg, fg);
        scenario.executeAndEnd(gen);
        Entry entry = gen.getEntry(keyFor(fg, frame));
        return aggregator.aggregate(entry);
    }
}
