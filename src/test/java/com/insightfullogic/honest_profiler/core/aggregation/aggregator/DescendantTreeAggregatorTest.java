package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static com.insightfullogic.honest_profiler.core.TreeGenerator.assertAggregationSizeEquals;
import static com.insightfullogic.honest_profiler.core.TreeGenerator.assertContains;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.keyFor;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.keysFor;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_03;

import java.util.function.Consumer;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.FlatGenerator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;

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
        Tree tree = get(LogEventFactory::applyScenario01, tg, fg, F_01);

        assertAggregationSizeEquals(tree, 1);
        assertContains(tree, 1, 1, keysFor(fg, F_01));
    }

    // Descendants of intermediate node
    private void checkMultipleDescendantsSingleThread(ThreadGrouping tg, FrameGrouping fg)
    {
        Tree tree = get(LogEventFactory::applyScenario02, tg, fg, F_03);

        assertAggregationSizeEquals(tree, 3);
        assertContains(tree, 0, 1, keysFor(fg, F_03));
        assertContains(tree, 0, 1, keysFor(fg, F_03, F_02));
        assertContains(tree, 1, 1, keysFor(fg, F_03, F_02, F_01));
    }

    // Descendants of intermediate node
    private void checkMultipleDescendantsTwoThreads(ThreadGrouping tg, FrameGrouping fg)
    {
        Tree tree = get(LogEventFactory::applyScenario06, tg, fg, F_03);

        assertAggregationSizeEquals(tree, 3);
        assertContains(tree, 0, 2, keysFor(fg, F_03));
        assertContains(tree, 0, 2, keysFor(fg, F_03, F_02));
        assertContains(tree, 2, 2, keysFor(fg, F_03, F_02, F_01));
    }

    private Tree get(Consumer<FlatGenerator> scenario, ThreadGrouping tg, FrameGrouping fg,
        StackFrame frame)
    {
        DescendantTreeAggregator aggregator = new DescendantTreeAggregator();
        FlatGenerator gen = new FlatGenerator(tg, fg);
        scenario.accept(gen);
        Entry entry = gen.getEntry(keyFor(fg, frame));
        return aggregator.aggregate(entry);
    }
}
