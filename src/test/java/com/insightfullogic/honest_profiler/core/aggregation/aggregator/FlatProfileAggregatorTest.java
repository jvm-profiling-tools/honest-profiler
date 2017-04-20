package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.keyFor;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_BCI;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN_LINENR;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_METHOD_ID;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_07_1;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_07_2;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_08_1;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_08_2;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_08_3;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_08_4;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_10_1;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_10_2;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_10_3;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_10_4;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_10_5;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.F_10_6;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario06;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario07;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario09;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.FlatGenerator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;

public class FlatProfileAggregatorTest
{
    @Test
    public void checkFlatAggregationByFqmn()
    {
        FlatGenerator gen;

        FrameGrouping fg = BY_FQMN;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario01(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(keyFor(fg, F_01), 1, 1);

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario02(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 1, 1);
            gen.assertContains(keyFor(fg, F_02), 0, 1);
            gen.assertContains(keyFor(fg, F_03), 0, 1);
            gen.assertContains(keyFor(fg, F_04), 0, 1);
            gen.assertContains(keyFor(fg, F_05), 0, 1);

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario03(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 2, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 0, 2);

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario04(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 1, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 1, 2);

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario05(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(keyFor(fg, F_01), 2, 2);

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario06(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 2, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 0, 2);

            // Two threads, mixed line nrs & BCI

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario07(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 5, 20);
            gen.assertContains(keyFor(fg, F_02), 5, 20);
            gen.assertContains(keyFor(fg, F_03), 0, 10);
            gen.assertContains(keyFor(fg, F_04), 0, 10);
            gen.assertContains(keyFor(fg, F_05), 10, 10);
            gen.assertContains(keyFor(fg, F_07_1), 5, 20);
            gen.assertContains(keyFor(fg, F_08_1), 5, 20);

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario09(gen);

            gen.assertAggregationSizeEquals(6);
            gen.assertContains(keyFor(fg, F_01), 48, 102);
            gen.assertContains(keyFor(fg, F_02), 15, 111);
            gen.assertContains(keyFor(fg, F_03), 19, 72);
            gen.assertContains(keyFor(fg, F_04), 0, 44);
            gen.assertContains(keyFor(fg, F_05), 10, 23);
            gen.assertContains(keyFor(fg, F_07_1), 48, 102);
            gen.assertContains(keyFor(fg, F_08_1), 15, 111);
            gen.assertContains(keyFor(fg, F_10_1), 19, 38);
        }
    }

    @Test
    public void checkFlatAggregationByFqmnPlusBci()
    {
        FlatGenerator gen;

        FrameGrouping fg = BY_BCI;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario01(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(keyFor(fg, F_01), 1, 1);

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario02(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 1, 1);
            gen.assertContains(keyFor(fg, F_02), 0, 1);
            gen.assertContains(keyFor(fg, F_03), 0, 1);
            gen.assertContains(keyFor(fg, F_04), 0, 1);
            gen.assertContains(keyFor(fg, F_05), 0, 1);

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario03(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 2, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 0, 2);

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario04(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 1, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 1, 2);

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario05(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(keyFor(fg, F_01), 2, 2);

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario06(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 2, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 0, 2);

            // Two threads, mixed line nrs & BCI

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario07(gen);

            gen.assertAggregationSizeEquals(9);
            gen.assertContains(keyFor(fg, F_01), 3, 16);
            gen.assertContains(keyFor(fg, F_02), 0, 10);
            gen.assertContains(keyFor(fg, F_03), 0, 10);
            gen.assertContains(keyFor(fg, F_04), 0, 10);
            gen.assertContains(keyFor(fg, F_05), 10, 10);
            gen.assertContains(keyFor(fg, F_07_1), 2, 4);
            gen.assertContains(keyFor(fg, F_07_2), 3, 16);
            gen.assertContains(keyFor(fg, F_08_1), 2, 2);
            gen.assertContains(keyFor(fg, F_08_2), 3, 5);
            gen.assertContains(keyFor(fg, F_08_3), 3, 5);
            gen.assertContains(keyFor(fg, F_08_4), 0, 3);

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario09(gen);

            gen.assertAggregationSizeEquals(13);
            gen.assertContains(keyFor(fg, F_01), 45, 99);
            gen.assertContains(keyFor(fg, F_02), 1, 97);
            gen.assertContains(keyFor(fg, F_03), 19, 72);
            gen.assertContains(keyFor(fg, F_04), 0, 44);
            gen.assertContains(keyFor(fg, F_05), 10, 23);
            gen.assertContains(keyFor(fg, F_07_1), 3, 3);
            gen.assertContains(keyFor(fg, F_07_2), 45, 99);
            gen.assertContains(keyFor(fg, F_08_1), 2, 2);
            gen.assertContains(keyFor(fg, F_08_2), 7, 7);
            gen.assertContains(keyFor(fg, F_08_3), 7, 7);
            gen.assertContains(keyFor(fg, F_08_4), 5, 5);
            gen.assertContains(keyFor(fg, F_10_1), 2, 4);
            gen.assertContains(keyFor(fg, F_10_2), 3, 6);
            gen.assertContains(keyFor(fg, F_10_3), 9, 18);
            gen.assertContains(keyFor(fg, F_10_4), 9, 18);
            gen.assertContains(keyFor(fg, F_10_5), 5, 10);
            gen.assertContains(keyFor(fg, F_10_6), 5, 10);
        }
    }

    @Test
    public void checkFlatAggregationByFqmnPlusLineNr()
    {
        FlatGenerator gen;

        FrameGrouping fg = BY_FQMN_LINENR;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario01(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(keyFor(fg, F_01), 1, 1);

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario02(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 1, 1);
            gen.assertContains(keyFor(fg, F_02), 0, 1);
            gen.assertContains(keyFor(fg, F_03), 0, 1);
            gen.assertContains(keyFor(fg, F_04), 0, 1);
            gen.assertContains(keyFor(fg, F_05), 0, 1);

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario03(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 2, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 0, 2);

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario04(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 1, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 1, 2);

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario05(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(keyFor(fg, F_01), 2, 2);

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario06(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 2, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 0, 2);

            // Two threads, mixed line nrs & BCI

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario07(gen);

            gen.assertAggregationSizeEquals(8);
            gen.assertContains(keyFor(fg, F_01), 5, 20);
            gen.assertContains(keyFor(fg, F_02), 0, 10);
            gen.assertContains(keyFor(fg, F_03), 0, 10);
            gen.assertContains(keyFor(fg, F_04), 0, 10);
            gen.assertContains(keyFor(fg, F_05), 10, 10);
            gen.assertContains(keyFor(fg, F_07_1), 5, 20);
            gen.assertContains(keyFor(fg, F_07_2), 5, 20);
            gen.assertContains(keyFor(fg, F_08_1), 2, 2);
            gen.assertContains(keyFor(fg, F_08_2), 3, 6);
            gen.assertContains(keyFor(fg, F_08_3), 0, 2);
            gen.assertContains(keyFor(fg, F_08_4), 3, 6);

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, fg);
            applyScenario09(gen);

            gen.assertAggregationSizeEquals(11);
            gen.assertContains(keyFor(fg, F_01), 48, 102);
            gen.assertContains(keyFor(fg, F_02), 1, 97);
            gen.assertContains(keyFor(fg, F_03), 19, 72);
            gen.assertContains(keyFor(fg, F_04), 0, 44);
            gen.assertContains(keyFor(fg, F_05), 10, 23);
            gen.assertContains(keyFor(fg, F_07_1), 48, 102);
            gen.assertContains(keyFor(fg, F_07_2), 48, 102);
            gen.assertContains(keyFor(fg, F_08_1), 2, 2);
            gen.assertContains(keyFor(fg, F_08_2), 8, 8);
            gen.assertContains(keyFor(fg, F_08_3), 4, 4);
            gen.assertContains(keyFor(fg, F_08_4), 8, 8);
            gen.assertContains(keyFor(fg, F_10_1), 7, 14);
            gen.assertContains(keyFor(fg, F_10_2), 7, 14);
            gen.assertContains(keyFor(fg, F_10_3), 4, 8);
            gen.assertContains(keyFor(fg, F_10_4), 8, 16);
            gen.assertContains(keyFor(fg, F_10_5), 7, 14);
            gen.assertContains(keyFor(fg, F_10_6), 8, 16);
        }
    }

    @Test
    public void checkFlatAggregationByMethodId()
    {
        FlatGenerator gen;

        FrameGrouping fg = BY_METHOD_ID;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario01(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(keyFor(fg, F_01), 1, 1);

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario02(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 1, 1);
            gen.assertContains(keyFor(fg, F_02), 0, 1);
            gen.assertContains(keyFor(fg, F_03), 0, 1);
            gen.assertContains(keyFor(fg, F_04), 0, 1);
            gen.assertContains(keyFor(fg, F_05), 0, 1);

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario03(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 2, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 0, 2);

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario04(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 1, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 1, 2);

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario05(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(keyFor(fg, F_01), 2, 2, nano(2), nano(2));

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario06(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(keyFor(fg, F_01), 2, 2);
            gen.assertContains(keyFor(fg, F_02), 0, 2);
            gen.assertContains(keyFor(fg, F_03), 0, 2);
            gen.assertContains(keyFor(fg, F_04), 0, 2);
            gen.assertContains(keyFor(fg, F_05), 0, 2);

            // Two threads, mixed line nrs & BCI

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario07(gen);

            gen.assertAggregationSizeEquals(7);
            gen.assertContains(keyFor(fg, F_01), 0, 10);
            gen.assertContains(keyFor(fg, F_02), 0, 10);
            gen.assertContains(keyFor(fg, F_03), 0, 10);
            gen.assertContains(keyFor(fg, F_04), 0, 10);
            gen.assertContains(keyFor(fg, F_05), 10, 10);
            gen.assertContains(keyFor(fg, F_07_1), 5, 10);
            gen.assertContains(keyFor(fg, F_08_1), 5, 10);

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario09(gen);

            gen.assertAggregationSizeEquals(8);
            gen.assertContains(keyFor(fg, F_01), 42, 96);
            gen.assertContains(keyFor(fg, F_02), 1, 97);
            gen.assertContains(keyFor(fg, F_03), 19, 72);
            gen.assertContains(keyFor(fg, F_04), 0, 44);
            gen.assertContains(keyFor(fg, F_05), 10, 23);
            gen.assertContains(keyFor(fg, F_07_1), 6, 6);
            gen.assertContains(keyFor(fg, F_08_1), 14, 14);
            gen.assertContains(keyFor(fg, F_10_1), 19, 38);
        }
    }
}
