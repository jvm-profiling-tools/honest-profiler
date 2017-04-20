package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.keysFor;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_BCI;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN_LINENR;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_METHOD_ID;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.ALL_TOGETHER;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.BY_ID;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.BY_NAME;
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
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_07;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_08;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_10;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.T_11;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario06;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario07;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario09;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.TreeGenerator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;

public class TreeProfileAggregatorTest
{

    @Test
    public void checkTreeAggregationByFqmnByThreadId()
    {
        ThreadGrouping tg = BY_ID;
        FrameGrouping fg = BY_FQMN;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(4);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_02, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(12);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(16);
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_1));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_01, F_02, F_08_1));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_1, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(122);

        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));

        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_04, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, T_04, F_03, F_02, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_05, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_05, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, T_05, F_03, F_02, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 3, keysFor(tg, fg, null, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_02, F_01));

        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_1));

        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_1));

        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01, F_02));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_1));

        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_1));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_1, F_03));
    }

    @Test
    public void checkTreeAggregationByFqmnByThreadName()
    {
        ThreadGrouping tg = BY_NAME;
        FrameGrouping fg = BY_FQMN;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(4);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_02, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(12);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(16);
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_1));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_01, F_02, F_08_1));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_1, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        // gen.assertAggregationSizeEquals(93);

        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));

        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_04, F_02));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 8, keysFor(tg, fg, T_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, T_04, F_03, F_02, F_01));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_02, F_01));

        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_05, F_02));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 8, keysFor(tg, fg, T_05, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_05, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, T_05, F_03, F_02, F_01));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 3, keysFor(tg, fg, null, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_02, F_01));

        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_1));

        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_1));

        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01, F_02));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_1));

        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_1));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_1, F_03));
    }

    @Test
    public void checkTreeAggregationByFqmnAllThreads()
    {
        ThreadGrouping tg = ALL_TOGETHER;
        FrameGrouping fg = BY_FQMN;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(2);
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(6);
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(11);
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_07_1));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, null, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_03, F_04, F_07_1, F_05));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_08_1));
        gen.assertContains(0, 5, keysFor(tg, fg, null, F_03, F_04, F_08_1));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_03, F_04, F_08_1, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(35);

        gen.assertContains(6, 60, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 48, keysFor(tg, fg, null, F_01, F_02));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02, F_03));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02, F_03, F_04));
        gen.assertContains(10, 10, keysFor(tg, fg, null, F_01, F_02, F_03, F_04, F_05));
        gen.assertContains(19, 38, keysFor(tg, fg, null, F_01, F_02, F_10_1));
        gen.assertContains(19, 19, keysFor(tg, fg, null, F_01, F_02, F_10_1, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(15, 23, keysFor(tg, fg, null, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 12, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 9, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(9, 9, keysFor(tg, fg, null, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(13, 13, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(6, 60, keysFor(tg, fg, null, F_07_1));
        gen.assertContains(15, 23, keysFor(tg, fg, null, F_08_1));
    }

    @Test
    public void checkTreeAggregationByFqmnBciByThreadId()
    {
        ThreadGrouping tg = BY_ID;
        FrameGrouping fg = BY_BCI;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(4);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_02, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(12);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(22);
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_07, F_01, F_02, F_07_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_07, F_01, F_02, F_07_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_07, F_03, F_04, F_07_1));
        gen.assertContains(2, 2, keysFor(tg, fg, T_07, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, T_07, F_03, F_04, F_07_2));
        gen.assertContains(3, 3, keysFor(tg, fg, T_07, F_03, F_04, F_07_2, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_01, F_02, F_08_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_08, F_01, F_02, F_08_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_08, F_03, F_04, F_08_3));
        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, T_08, F_03, F_04, F_08_4));
        gen.assertContains(3, 3, keysFor(tg, fg, T_08, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(134);

        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));

        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_04, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, T_04, F_03, F_02, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_05, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_05, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, T_05, F_03, F_02, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 3, keysFor(tg, fg, null, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_02, F_01));

        gen.assertContains(3, 3, keysFor(tg, fg, T_07, F_07_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_07, F_07_2));

        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_08_1));
        gen.assertContains(7, 7, keysFor(tg, fg, T_08, F_08_2));
        gen.assertContains(7, 7, keysFor(tg, fg, T_08, F_08_3));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_08_4));

        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_10, F_01, F_02, F_10_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_10, F_01, F_02, F_10_2));
        gen.assertContains(9, 9, keysFor(tg, fg, T_10, F_01, F_02, F_10_3));
        gen.assertContains(9, 9, keysFor(tg, fg, T_10, F_01, F_02, F_10_4));
        gen.assertContains(5, 5, keysFor(tg, fg, T_10, F_01, F_02, F_10_5));
        gen.assertContains(5, 5, keysFor(tg, fg, T_10, F_01, F_02, F_10_6));

        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_11, F_01, F_02, F_10_1));
        gen.assertContains(2, 2, keysFor(tg, fg, T_11, F_01, F_02, F_10_1, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, T_11, F_01, F_02, F_10_2));
        gen.assertContains(3, 3, keysFor(tg, fg, T_11, F_01, F_02, F_10_2, F_03));
        gen.assertContains(0, 9, keysFor(tg, fg, T_11, F_01, F_02, F_10_3));
        gen.assertContains(9, 9, keysFor(tg, fg, T_11, F_01, F_02, F_10_3, F_03));
        gen.assertContains(0, 9, keysFor(tg, fg, T_11, F_01, F_02, F_10_4));
        gen.assertContains(9, 9, keysFor(tg, fg, T_11, F_01, F_02, F_10_4, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_11, F_01, F_02, F_10_5));
        gen.assertContains(5, 5, keysFor(tg, fg, T_11, F_01, F_02, F_10_5, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_11, F_01, F_02, F_10_6));
        gen.assertContains(5, 5, keysFor(tg, fg, T_11, F_01, F_02, F_10_6, F_03));
    }

    @Test
    public void checkTreeAggregationByFqmnBciByThreadName()
    {
        ThreadGrouping tg = BY_NAME;
        FrameGrouping fg = BY_BCI;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(4);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_02, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(12);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(22);
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_07, F_01, F_02, F_07_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_07, F_01, F_02, F_07_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_07, F_03, F_04, F_07_1));
        gen.assertContains(2, 2, keysFor(tg, fg, T_07, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, T_07, F_03, F_04, F_07_2));
        gen.assertContains(3, 3, keysFor(tg, fg, T_07, F_03, F_04, F_07_2, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_01, F_02, F_08_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_08, F_01, F_02, F_08_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_08, F_03, F_04, F_08_3));
        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, T_08, F_03, F_04, F_08_4));
        gen.assertContains(3, 3, keysFor(tg, fg, T_08, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(105);

        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));

        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_04, F_02));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 8, keysFor(tg, fg, T_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, T_04, F_03, F_02, F_01));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_02, F_01));

        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_05, F_02));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 8, keysFor(tg, fg, T_05, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_05, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, T_05, F_03, F_02, F_01));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 3, keysFor(tg, fg, null, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_02, F_01));

        gen.assertContains(3, 3, keysFor(tg, fg, T_07, F_07_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_07, F_07_2));

        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_08_1));
        gen.assertContains(7, 7, keysFor(tg, fg, T_08, F_08_2));
        gen.assertContains(7, 7, keysFor(tg, fg, T_08, F_08_3));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_08_4));

        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_10, F_01, F_02, F_10_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_10, F_01, F_02, F_10_2));
        gen.assertContains(9, 9, keysFor(tg, fg, T_10, F_01, F_02, F_10_3));
        gen.assertContains(9, 9, keysFor(tg, fg, T_10, F_01, F_02, F_10_4));
        gen.assertContains(5, 5, keysFor(tg, fg, T_10, F_01, F_02, F_10_5));
        gen.assertContains(5, 5, keysFor(tg, fg, T_10, F_01, F_02, F_10_6));

        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_11, F_01, F_02, F_10_1));
        gen.assertContains(2, 2, keysFor(tg, fg, T_11, F_01, F_02, F_10_1, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, T_11, F_01, F_02, F_10_2));
        gen.assertContains(3, 3, keysFor(tg, fg, T_11, F_01, F_02, F_10_2, F_03));
        gen.assertContains(0, 9, keysFor(tg, fg, T_11, F_01, F_02, F_10_3));
        gen.assertContains(9, 9, keysFor(tg, fg, T_11, F_01, F_02, F_10_3, F_03));
        gen.assertContains(0, 9, keysFor(tg, fg, T_11, F_01, F_02, F_10_4));
        gen.assertContains(9, 9, keysFor(tg, fg, T_11, F_01, F_02, F_10_4, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_11, F_01, F_02, F_10_5));
        gen.assertContains(5, 5, keysFor(tg, fg, T_11, F_01, F_02, F_10_5, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_11, F_01, F_02, F_10_6));
        gen.assertContains(5, 5, keysFor(tg, fg, T_11, F_01, F_02, F_10_6, F_03));
    }

    @Test
    public void checkTreeAggregationByFqmnBciAllThreads()
    {
        ThreadGrouping tg = ALL_TOGETHER;
        FrameGrouping fg = BY_BCI;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(2);
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(6);
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(17);

        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_02, F_07_1));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_01, F_02, F_07_2));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_03, F_04, F_07_1));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_07_2));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_04, F_07_2, F_05));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_02, F_08_1));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_01, F_02, F_08_2));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_03, F_04, F_08_3));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_08_4));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(45);

        gen.assertContains(3, 57, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 48, keysFor(tg, fg, null, F_01, F_02));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02, F_03));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02, F_03, F_04));
        gen.assertContains(10, 10, keysFor(tg, fg, null, F_01, F_02, F_03, F_04, F_05));
        gen.assertContains(2, 4, keysFor(tg, fg, null, F_01, F_02, F_10_1));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_02, F_10_1, F_03));
        gen.assertContains(3, 6, keysFor(tg, fg, null, F_01, F_02, F_10_2));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_01, F_02, F_10_2, F_03));
        gen.assertContains(9, 18, keysFor(tg, fg, null, F_01, F_02, F_10_3));
        gen.assertContains(9, 9, keysFor(tg, fg, null, F_01, F_02, F_10_3, F_03));
        gen.assertContains(9, 18, keysFor(tg, fg, null, F_01, F_02, F_10_4));
        gen.assertContains(9, 9, keysFor(tg, fg, null, F_01, F_02, F_10_4, F_03));
        gen.assertContains(5, 10, keysFor(tg, fg, null, F_01, F_02, F_10_5));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_10_5, F_03));
        gen.assertContains(5, 10, keysFor(tg, fg, null, F_01, F_02, F_10_6));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_10_6, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 9, keysFor(tg, fg, null, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 12, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 9, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(9, 9, keysFor(tg, fg, null, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(13, 13, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_07_1));
        gen.assertContains(3, 57, keysFor(tg, fg, null, F_07_2));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_08_1));
        gen.assertContains(7, 7, keysFor(tg, fg, null, F_08_2));
        gen.assertContains(7, 7, keysFor(tg, fg, null, F_08_3));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_08_4));
    }

    @Test
    public void checkTreeAggregationByFqmnLineNrByThreadId()
    {
        ThreadGrouping tg = BY_ID;
        FrameGrouping fg = BY_FQMN_LINENR;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(4);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_02, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(12);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(19);
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_2));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_2, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_01, F_02, F_08_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_08, F_01, F_02, F_08_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_08, F_03, F_04, F_08_3));
        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, T_08, F_03, F_04, F_08_4));
        gen.assertContains(3, 3, keysFor(tg, fg, T_08, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(130);

        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));

        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_04, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, T_04, F_03, F_02, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_05, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_05, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, T_05, F_03, F_02, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 3, keysFor(tg, fg, null, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_02, F_01));

        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_1));
        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_2));

        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_08_1));
        gen.assertContains(8, 8, keysFor(tg, fg, T_08, F_08_2));
        gen.assertContains(4, 4, keysFor(tg, fg, T_08, F_08_3));
        gen.assertContains(8, 8, keysFor(tg, fg, T_08, F_08_4));

        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01, F_02));
        gen.assertContains(7, 7, keysFor(tg, fg, T_10, F_01, F_02, F_10_1));
        gen.assertContains(7, 7, keysFor(tg, fg, T_10, F_01, F_02, F_10_2));
        gen.assertContains(4, 4, keysFor(tg, fg, T_10, F_01, F_02, F_10_3));
        gen.assertContains(8, 8, keysFor(tg, fg, T_10, F_01, F_02, F_10_4));
        gen.assertContains(7, 7, keysFor(tg, fg, T_10, F_01, F_02, F_10_5));
        gen.assertContains(8, 8, keysFor(tg, fg, T_10, F_01, F_02, F_10_6));

        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02));
        gen.assertContains(0, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_1));
        gen.assertContains(7, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_1, F_03));
        gen.assertContains(0, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_2));
        gen.assertContains(7, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_2, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_11, F_01, F_02, F_10_3));
        gen.assertContains(4, 4, keysFor(tg, fg, T_11, F_01, F_02, F_10_3, F_03));
        gen.assertContains(0, 8, keysFor(tg, fg, T_11, F_01, F_02, F_10_4));
        gen.assertContains(8, 8, keysFor(tg, fg, T_11, F_01, F_02, F_10_4, F_03));
        gen.assertContains(0, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_5));
        gen.assertContains(7, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_5, F_03));
        gen.assertContains(0, 8, keysFor(tg, fg, T_11, F_01, F_02, F_10_6));
        gen.assertContains(8, 8, keysFor(tg, fg, T_11, F_01, F_02, F_10_6, F_03));
    }

    @Test
    public void checkTreeAggregationByFqmnLineNrByThreadName()
    {
        ThreadGrouping tg = BY_NAME;
        FrameGrouping fg = BY_FQMN_LINENR;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(4);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_02, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(12);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(19);
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_2));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_2, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_01, F_02, F_08_1));
        gen.assertContains(3, 3, keysFor(tg, fg, T_08, F_01, F_02, F_08_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_08, F_03, F_04, F_08_3));
        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, T_08, F_03, F_04, F_08_4));
        gen.assertContains(3, 3, keysFor(tg, fg, T_08, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(101);

        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));

        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_04, F_02));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 8, keysFor(tg, fg, T_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, T_04, F_03, F_02, F_01));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_02, F_01));

        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_05, F_02));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 8, keysFor(tg, fg, T_05, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_05, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, T_05, F_03, F_02, F_01));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 3, keysFor(tg, fg, null, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_02, F_01));

        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_1));
        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_2));

        gen.assertContains(2, 2, keysFor(tg, fg, T_08, F_08_1));
        gen.assertContains(8, 8, keysFor(tg, fg, T_08, F_08_2));
        gen.assertContains(4, 4, keysFor(tg, fg, T_08, F_08_3));
        gen.assertContains(8, 8, keysFor(tg, fg, T_08, F_08_4));

        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01, F_02));
        gen.assertContains(7, 7, keysFor(tg, fg, T_10, F_01, F_02, F_10_1));
        gen.assertContains(7, 7, keysFor(tg, fg, T_10, F_01, F_02, F_10_2));
        gen.assertContains(4, 4, keysFor(tg, fg, T_10, F_01, F_02, F_10_3));
        gen.assertContains(8, 8, keysFor(tg, fg, T_10, F_01, F_02, F_10_4));
        gen.assertContains(7, 7, keysFor(tg, fg, T_10, F_01, F_02, F_10_5));
        gen.assertContains(8, 8, keysFor(tg, fg, T_10, F_01, F_02, F_10_6));

        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02));
        gen.assertContains(0, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_1));
        gen.assertContains(7, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_1, F_03));
        gen.assertContains(0, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_2));
        gen.assertContains(7, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_2, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_11, F_01, F_02, F_10_3));
        gen.assertContains(4, 4, keysFor(tg, fg, T_11, F_01, F_02, F_10_3, F_03));
        gen.assertContains(0, 8, keysFor(tg, fg, T_11, F_01, F_02, F_10_4));
        gen.assertContains(8, 8, keysFor(tg, fg, T_11, F_01, F_02, F_10_4, F_03));
        gen.assertContains(0, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_5));
        gen.assertContains(7, 7, keysFor(tg, fg, T_11, F_01, F_02, F_10_5, F_03));
        gen.assertContains(0, 8, keysFor(tg, fg, T_11, F_01, F_02, F_10_6));
        gen.assertContains(8, 8, keysFor(tg, fg, T_11, F_01, F_02, F_10_6, F_03));
    }

    @Test
    public void checkTreeAggregationByFqmnLineNrAllThreads()
    {
        ThreadGrouping tg = ALL_TOGETHER;
        FrameGrouping fg = BY_FQMN_LINENR;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(2);
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(6);
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(14);

        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_07_2));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, null, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, null, F_03, F_04, F_07_2));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_03, F_04, F_07_2, F_05));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_02, F_08_1));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_01, F_02, F_08_2));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_03, F_04, F_08_3));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_08_4));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(42);

        gen.assertContains(6, 60, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 48, keysFor(tg, fg, null, F_01, F_02));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02, F_03));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02, F_03, F_04));
        gen.assertContains(10, 10, keysFor(tg, fg, null, F_01, F_02, F_03, F_04, F_05));
        gen.assertContains(7, 14, keysFor(tg, fg, null, F_01, F_02, F_10_1));
        gen.assertContains(7, 7, keysFor(tg, fg, null, F_01, F_02, F_10_1, F_03));
        gen.assertContains(7, 14, keysFor(tg, fg, null, F_01, F_02, F_10_2));
        gen.assertContains(7, 7, keysFor(tg, fg, null, F_01, F_02, F_10_2, F_03));
        gen.assertContains(4, 8, keysFor(tg, fg, null, F_01, F_02, F_10_3));
        gen.assertContains(4, 4, keysFor(tg, fg, null, F_01, F_02, F_10_3, F_03));
        gen.assertContains(8, 16, keysFor(tg, fg, null, F_01, F_02, F_10_4));
        gen.assertContains(8, 8, keysFor(tg, fg, null, F_01, F_02, F_10_4, F_03));
        gen.assertContains(7, 14, keysFor(tg, fg, null, F_01, F_02, F_10_5));
        gen.assertContains(7, 7, keysFor(tg, fg, null, F_01, F_02, F_10_5, F_03));
        gen.assertContains(8, 16, keysFor(tg, fg, null, F_01, F_02, F_10_6));
        gen.assertContains(8, 8, keysFor(tg, fg, null, F_01, F_02, F_10_6, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 9, keysFor(tg, fg, null, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 12, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 9, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(9, 9, keysFor(tg, fg, null, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(13, 13, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(6, 60, keysFor(tg, fg, null, F_07_1));
        gen.assertContains(6, 60, keysFor(tg, fg, null, F_07_2));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_08_1));
        gen.assertContains(8, 8, keysFor(tg, fg, null, F_08_2));
        gen.assertContains(4, 4, keysFor(tg, fg, null, F_08_3));
        gen.assertContains(8, 8, keysFor(tg, fg, null, F_08_4));
    }

    @Test
    public void checkTreeAggregationByMethodIdByThreadId()
    {
        ThreadGrouping tg = BY_ID;
        FrameGrouping fg = BY_METHOD_ID;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(4);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_02, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(12);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(16);
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_2));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_2, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_01, F_02, F_08_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_01, F_02, F_08_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_3));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_4));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(122);

        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));

        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_04, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, T_04, F_03, F_02, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_04, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_05, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, T_05, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, T_05, F_03, F_02, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_05, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 3, keysFor(tg, fg, null, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_02, F_01));

        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_1));
        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_2));

        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_1));
        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_2));
        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_3));
        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_4));

        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01, F_02));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_1));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_2));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_3));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_4));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_5));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_6));

        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_1));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_1, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_2));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_2, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_3));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_3, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_4));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_4, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_5));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_5, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_6));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_6, F_03));
    }

    @Test
    public void checkTreeAggregationByMethodIdByThreadName()
    {
        ThreadGrouping tg = BY_NAME;
        FrameGrouping fg = BY_METHOD_ID;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(4);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(1, 1, keysFor(tg, fg, T_02, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(12);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(16);
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_01, F_02, F_07_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_2));
        gen.assertContains(5, 5, keysFor(tg, fg, T_07, F_03, F_04, F_07_2, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_01, F_02, F_08_1));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_01, F_02, F_08_2));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_3));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_4));
        gen.assertContains(5, 5, keysFor(tg, fg, T_08, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(93);

        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));

        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_02, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03));
        gen.assertContains(0, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04));
        gen.assertContains(5, 5, keysFor(tg, fg, T_03, F_01, F_02, F_03, F_04, F_05));

        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_04, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_04, F_02));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_04, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 8, keysFor(tg, fg, T_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, T_04, F_03, F_02, F_01));
        gen.assertContains(2, 2, keysFor(tg, fg, T_04, F_02, F_01));

        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_05, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_05, F_02));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03));
        gen.assertContains(0, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02));
        gen.assertContains(4, 4, keysFor(tg, fg, T_05, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 8, keysFor(tg, fg, T_05, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, T_05, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, T_05, F_03, F_02, F_01));
        gen.assertContains(2, 2, keysFor(tg, fg, T_05, F_02, F_01));

        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 3, keysFor(tg, fg, null, F_02));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 4, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_02, F_01));

        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_1));
        gen.assertContains(6, 6, keysFor(tg, fg, T_07, F_07_2));

        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_1));
        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_2));
        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_3));
        gen.assertContains(14, 14, keysFor(tg, fg, T_08, F_08_4));

        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_10, F_01, F_02));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_1));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_2));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_3));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_4));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_5));
        gen.assertContains(19, 19, keysFor(tg, fg, T_10, F_01, F_02, F_10_6));

        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_1));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_1, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_2));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_2, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_3));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_3, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_4));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_4, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_5));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_5, F_03));
        gen.assertContains(0, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_6));
        gen.assertContains(19, 19, keysFor(tg, fg, T_11, F_01, F_02, F_10_6, F_03));
    }

    @Test
    public void checkTreeAggregationByMethodIdAllThreads()
    {
        ThreadGrouping tg = ALL_TOGETHER;
        FrameGrouping fg = BY_METHOD_ID;

        checkScenario01(tg, fg);
        checkScenario02(tg, fg);
        checkScenario03(tg, fg);
        checkScenario04(tg, fg);

        TreeGenerator gen;

        // Two threads, single frame

        gen = new TreeGenerator(tg, fg);
        applyScenario05(gen);

        gen.assertAggregationSizeEquals(2);
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_01));

        // Two threads, single stack

        gen = new TreeGenerator(tg, fg);
        applyScenario06(gen);

        gen.assertAggregationSizeEquals(6);
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));

        // Two threads, mixed line nrs & BCI

        gen = new TreeGenerator(tg, fg);
        applyScenario07(gen);

        gen.assertAggregationSizeEquals(11);

        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_07_2));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 5, keysFor(tg, fg, null, F_03, F_04, F_07_1));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_03, F_04, F_07_1, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, null, F_03, F_04, F_07_2));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_03, F_04, F_07_2, F_05));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_08_1));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_01, F_02, F_08_2));
        gen.assertContains(0, 5, keysFor(tg, fg, null, F_03, F_04, F_08_3));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_03, F_04, F_08_3, F_05));
        gen.assertContains(0, 5, keysFor(tg, fg, null, F_03, F_04, F_08_4));
        gen.assertContains(5, 5, keysFor(tg, fg, null, F_03, F_04, F_08_4, F_05));

        // Full Scenario

        gen = new TreeGenerator(tg, fg);
        applyScenario09(gen);

        gen.assertAggregationSizeEquals(37);

        gen.assertContains(0, 54, keysFor(tg, fg, null, F_01));
        gen.assertContains(0, 48, keysFor(tg, fg, null, F_01, F_02));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02, F_03));
        gen.assertContains(0, 10, keysFor(tg, fg, null, F_01, F_02, F_03, F_04));
        gen.assertContains(10, 10, keysFor(tg, fg, null, F_01, F_02, F_03, F_04, F_05));
        gen.assertContains(19, 38, keysFor(tg, fg, null, F_01, F_02, F_10_1));
        gen.assertContains(19, 19, keysFor(tg, fg, null, F_01, F_02, F_10_1, F_03));
        gen.assertContains(19, 38, keysFor(tg, fg, null, F_01, F_02, F_10_2));
        gen.assertContains(19, 19, keysFor(tg, fg, null, F_01, F_02, F_10_2, F_03));
        gen.assertContains(19, 38, keysFor(tg, fg, null, F_01, F_02, F_10_3));
        gen.assertContains(19, 19, keysFor(tg, fg, null, F_01, F_02, F_10_3, F_03));
        gen.assertContains(19, 38, keysFor(tg, fg, null, F_01, F_02, F_10_4));
        gen.assertContains(19, 19, keysFor(tg, fg, null, F_01, F_02, F_10_4, F_03));
        gen.assertContains(19, 38, keysFor(tg, fg, null, F_01, F_02, F_10_5));
        gen.assertContains(19, 19, keysFor(tg, fg, null, F_01, F_02, F_10_5, F_03));
        gen.assertContains(19, 38, keysFor(tg, fg, null, F_01, F_02, F_10_6));
        gen.assertContains(19, 19, keysFor(tg, fg, null, F_01, F_02, F_10_6, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_01, F_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_01, F_04, F_03, F_02, F_01));
        gen.assertContains(1, 9, keysFor(tg, fg, null, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, null, F_02, F_01));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04, F_03));
        gen.assertContains(0, 6, keysFor(tg, fg, null, F_02, F_04, F_03, F_02));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_02, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 12, keysFor(tg, fg, null, F_03));
        gen.assertContains(0, 9, keysFor(tg, fg, null, F_03, F_02));
        gen.assertContains(9, 9, keysFor(tg, fg, null, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_03, F_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_03, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04, F_03));
        gen.assertContains(0, 3, keysFor(tg, fg, null, F_04, F_04, F_03, F_02));
        gen.assertContains(3, 3, keysFor(tg, fg, null, F_04, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04, F_03));
        gen.assertContains(0, 13, keysFor(tg, fg, null, F_05, F_04, F_03, F_02));
        gen.assertContains(13, 13, keysFor(tg, fg, null, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_07_1));
        gen.assertContains(6, 6, keysFor(tg, fg, null, F_07_2));
        gen.assertContains(14, 14, keysFor(tg, fg, null, F_08_1));
        gen.assertContains(14, 14, keysFor(tg, fg, null, F_08_2));
        gen.assertContains(14, 14, keysFor(tg, fg, null, F_08_3));
        gen.assertContains(14, 14, keysFor(tg, fg, null, F_08_4));
    }

    // Single thread, single frame
    private void checkScenario01(ThreadGrouping tg, FrameGrouping fg)
    {
        TreeGenerator gen = new TreeGenerator(tg, fg);
        applyScenario01(gen);

        gen.assertAggregationSizeEquals(2);
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01));
    }

    // Single thread, single stack
    private void checkScenario02(ThreadGrouping tg, FrameGrouping fg)
    {
        TreeGenerator gen = new TreeGenerator(tg, fg);
        applyScenario02(gen);

        gen.assertAggregationSizeEquals(6);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
    }

    // Single thread, two identical stacks
    private void checkScenario03(ThreadGrouping tg, FrameGrouping fg)
    {
        TreeGenerator gen = new TreeGenerator(tg, fg);
        applyScenario03(gen);

        gen.assertAggregationSizeEquals(6);
        gen.assertContains(0, 2, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 2, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 2, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 2, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(2, 2, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
    }

    // Single thread, two opposite stacks
    private void checkScenario04(ThreadGrouping tg, FrameGrouping fg)
    {
        TreeGenerator gen = new TreeGenerator(tg, fg);
        applyScenario04(gen);

        gen.assertAggregationSizeEquals(11);
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_05, F_04, F_03, F_02, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_01));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_01, F_02));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_01, F_02, F_03));
        gen.assertContains(0, 1, keysFor(tg, fg, T_01, F_01, F_02, F_03, F_04));
        gen.assertContains(1, 1, keysFor(tg, fg, T_01, F_01, F_02, F_03, F_04, F_05));
    }
}
