package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.ALL_TOGETHER;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.keyFor;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.keysFor;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_05;
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

public class AncestorTreeAggregatorTest
{
    @Test
    public void testAncestorTreeAggregator()
    {

        for (ThreadGrouping tg : ThreadGrouping.values())
        {
            for (FrameGrouping fg : FrameGrouping.values())
            {
                checkLeafAncestors(tg, fg);
                checkMultipleAncestorsSingleThread(tg, fg);
                checkMultipleAncestorsTwoThreads(tg, fg);
            }
        }
    }

    // Descendants of leaf node
    private void checkLeafAncestors(ThreadGrouping tg, FrameGrouping fg)
    {
        Tree tree = get(SCENARIOS.get(0), tg, fg, F_01);

        assertAggregationSizeEquals(tree, 2);
        assertContains(tree, 1, 1, keysFor(fg, F_01));
    }

    // Descendants of intermediate node
    private void checkMultipleAncestorsSingleThread(ThreadGrouping tg, FrameGrouping fg)
    {
        Tree tree = get(SCENARIOS.get(1), tg, fg, F_03);

        assertAggregationSizeEquals(tree, 4);
        assertContains(tree, 0, 1, keysFor(fg, F_03));
        assertContains(tree, 0, 1, keysFor(fg, F_03, F_04));
        assertContains(tree, 0, 1, keysFor(fg, F_03, F_04, F_05));
    }

    // Descendants of intermediate node
    private void checkMultipleAncestorsTwoThreads(ThreadGrouping tg, FrameGrouping fg)
    {
        Tree tree = get(SCENARIOS.get(5), tg, fg, F_03);

        int expected = tg == ALL_TOGETHER ? 1 : 2;

        // Leaf children are nodes representing aggregated thread info. So only 1 if all threads are aggregated
        // together.
        assertAggregationSizeEquals(tree, 3 + expected);
        assertContains(tree, 0, 2, keysFor(fg, F_03));
        assertContains(tree, 0, 2, keysFor(fg, F_03, F_04));
        assertContains(tree, 0, 2, keysFor(fg, F_03, F_04, F_05));
    }

    private Tree get(SimplifiedLogScenario scenario, ThreadGrouping tg, FrameGrouping fg,
        StackFrame frame)
    {
        AncestorTreeAggregator aggregator = new AncestorTreeAggregator();
        FlatGenerator gen = new FlatGenerator(tg, fg);
        scenario.executeAndEnd(gen);
        Entry entry = gen.getEntry(keyFor(fg, frame));
        return aggregator.aggregate(entry);
    }
}
