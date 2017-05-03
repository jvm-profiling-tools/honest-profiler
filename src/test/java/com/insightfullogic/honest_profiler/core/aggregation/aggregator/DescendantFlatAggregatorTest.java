package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.ALL_TOGETHER;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.keyFor;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.keysFor;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.SCENARIOS;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_01;
import static com.insightfullogic.honest_profiler.framework.generator.FlatGenerator.assertAggregationSizeEquals;
import static com.insightfullogic.honest_profiler.framework.generator.FlatGenerator.assertContains;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.framework.generator.TreeGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;

public class DescendantFlatAggregatorTest
{
    @Test
    public void testDescendantFlatAggregator()
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
        Flat flat = get(SCENARIOS.get(0), tg, fg, T_01, F_01);

        assertAggregationSizeEquals(flat, 0);
    }

    // Descendants of intermediate node
    private void checkMultipleDescendantsSingleThread(ThreadGrouping tg, FrameGrouping fg)
    {
        Flat flat = get(SCENARIOS.get(1), tg, fg, T_01, F_05, F_04, F_03);

        assertAggregationSizeEquals(flat, 2);
        assertContains(flat, keyFor(fg, F_02), 0, 1);
        assertContains(flat, keyFor(fg, F_01), 1, 1);
    }

    // Descendants of intermediate node
    private void checkMultipleDescendantsTwoThreads(ThreadGrouping tg, FrameGrouping fg)
    {
        Flat flat = get(SCENARIOS.get(5), tg, fg, T_01, F_05, F_04, F_03);

        int expected = tg == ALL_TOGETHER ? 2 : 1;
        assertAggregationSizeEquals(flat, 2);
        assertContains(flat, keyFor(fg, F_02), 0, expected);
        assertContains(flat, keyFor(fg, F_01), expected, expected);
    }

    private Flat get(SimplifiedLogScenario scenario, ThreadGrouping tg, FrameGrouping fg,
        ThreadMeta thread, StackFrame... frames)
    {
        DescendantFlatAggregator aggregator = new DescendantFlatAggregator();
        TreeGenerator gen = new TreeGenerator(tg, fg);
        scenario.executeAndEnd(gen);
        Node node = gen.getNode(keysFor(tg, fg, thread, frames));
        return aggregator.aggregate(node);
    }
}
