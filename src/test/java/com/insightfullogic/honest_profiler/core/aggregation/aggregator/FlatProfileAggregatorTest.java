package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.getFqmn;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.getFqmnPlusBci;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.getFqmnPlusLineNr;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.getMethodIdPlusFqmn;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_BCI;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN_LINENR;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_METHOD_ID;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_07_1;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_07_2;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_08_1;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_08_2;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_08_3;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_08_4;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_10_1;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_10_2;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_10_3;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_10_4;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_10_5;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_10_6;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_07;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_08;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_10;
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
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;

public class FlatProfileAggregatorTest
{
    @Test
    public void checkFlatAggregationByFqmn()
    {
        FlatGenerator gen;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, BY_FQMN);
            applyScenario01(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(getFqmn(METHOD_01), 1, 1);

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, BY_FQMN);
            applyScenario02(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmn(METHOD_01), 1, 1);
            gen.assertContains(getFqmn(METHOD_02), 0, 1);
            gen.assertContains(getFqmn(METHOD_03), 0, 1);
            gen.assertContains(getFqmn(METHOD_04), 0, 1);
            gen.assertContains(getFqmn(METHOD_05), 0, 1);

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, BY_FQMN);
            applyScenario03(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmn(METHOD_01), 2, 2);
            gen.assertContains(getFqmn(METHOD_02), 0, 2);
            gen.assertContains(getFqmn(METHOD_03), 0, 2);
            gen.assertContains(getFqmn(METHOD_04), 0, 2);
            gen.assertContains(getFqmn(METHOD_05), 0, 2);

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, BY_FQMN);
            applyScenario04(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmn(METHOD_01), 1, 2);
            gen.assertContains(getFqmn(METHOD_02), 0, 2);
            gen.assertContains(getFqmn(METHOD_03), 0, 2);
            gen.assertContains(getFqmn(METHOD_04), 0, 2);
            gen.assertContains(getFqmn(METHOD_05), 1, 2);

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, BY_FQMN);
            applyScenario05(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(getFqmn(METHOD_01), 2, 2);

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, BY_FQMN);
            applyScenario06(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmn(METHOD_01), 2, 2);
            gen.assertContains(getFqmn(METHOD_02), 0, 2);
            gen.assertContains(getFqmn(METHOD_03), 0, 2);
            gen.assertContains(getFqmn(METHOD_04), 0, 2);
            gen.assertContains(getFqmn(METHOD_05), 0, 2);

            // Two threads, mixed line nrs & BCI

            gen = new FlatGenerator(threadGrouping, BY_FQMN);
            applyScenario07(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmn(METHOD_01), 5, 20);
            gen.assertContains(getFqmn(METHOD_02), 5, 20);
            gen.assertContains(getFqmn(METHOD_03), 0, 10);
            gen.assertContains(getFqmn(METHOD_04), 0, 10);
            gen.assertContains(getFqmn(METHOD_05), 10, 10);
            gen.assertContains(getFqmn(METHOD_07), 5, 20);
            gen.assertContains(getFqmn(METHOD_08), 5, 20);

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, BY_FQMN);
            applyScenario09(gen);

            gen.assertAggregationSizeEquals(6);
            gen.assertContains(getFqmn(METHOD_01), 48, 102);
            gen.assertContains(getFqmn(METHOD_02), 15, 111);
            gen.assertContains(getFqmn(METHOD_03), 19, 72);
            gen.assertContains(getFqmn(METHOD_04), 0, 44);
            gen.assertContains(getFqmn(METHOD_05), 10, 23);
            gen.assertContains(getFqmn(METHOD_07), 48, 102);
            gen.assertContains(getFqmn(METHOD_08), 15, 111);
            gen.assertContains(getFqmn(METHOD_10), 19, 38);
        }
    }

    @Test
    public void checkFlatAggregationByFqmnPlusBci()
    {
        FlatGenerator gen;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, BY_BCI);
            applyScenario01(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(getFqmnPlusBci(METHOD_01, FRAME_01), 1, 1);

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, BY_BCI);
            applyScenario02(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmnPlusBci(METHOD_01, FRAME_01), 1, 1);
            gen.assertContains(getFqmnPlusBci(METHOD_02, FRAME_02), 0, 1);
            gen.assertContains(getFqmnPlusBci(METHOD_03, FRAME_03), 0, 1);
            gen.assertContains(getFqmnPlusBci(METHOD_04, FRAME_04), 0, 1);
            gen.assertContains(getFqmnPlusBci(METHOD_05, FRAME_05), 0, 1);

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, BY_BCI);
            applyScenario03(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmnPlusBci(METHOD_01, FRAME_01), 2, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_02, FRAME_02), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_03, FRAME_03), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_04, FRAME_04), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_05, FRAME_05), 0, 2);

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, BY_BCI);
            applyScenario04(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmnPlusBci(METHOD_01, FRAME_01), 1, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_02, FRAME_02), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_03, FRAME_03), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_04, FRAME_04), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_05, FRAME_05), 1, 2);

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, BY_BCI);
            applyScenario05(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(getFqmnPlusBci(METHOD_01, FRAME_01), 2, 2);

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, BY_BCI);
            applyScenario06(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmnPlusBci(METHOD_01, FRAME_01), 2, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_02, FRAME_02), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_03, FRAME_03), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_04, FRAME_04), 0, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_05, FRAME_05), 0, 2);

            // Two threads, mixed line nrs & BCI

            gen = new FlatGenerator(threadGrouping, BY_BCI);
            applyScenario07(gen);

            gen.assertAggregationSizeEquals(9);
            gen.assertContains(getFqmnPlusBci(METHOD_01, FRAME_01), 3, 16);
            gen.assertContains(getFqmnPlusBci(METHOD_02, FRAME_02), 0, 10);
            gen.assertContains(getFqmnPlusBci(METHOD_03, FRAME_03), 0, 10);
            gen.assertContains(getFqmnPlusBci(METHOD_04, FRAME_04), 0, 10);
            gen.assertContains(getFqmnPlusBci(METHOD_05, FRAME_05), 10, 10);
            gen.assertContains(getFqmnPlusBci(METHOD_07, FRAME_07_1), 2, 4);
            gen.assertContains(getFqmnPlusBci(METHOD_07, FRAME_07_2), 3, 16);
            gen.assertContains(getFqmnPlusBci(METHOD_08, FRAME_08_1), 2, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_08, FRAME_08_2), 3, 5);
            gen.assertContains(getFqmnPlusBci(METHOD_08, FRAME_08_3), 3, 5);
            gen.assertContains(getFqmnPlusBci(METHOD_08, FRAME_08_4), 0, 3);

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, BY_BCI);
            applyScenario09(gen);

            gen.assertAggregationSizeEquals(13);
            gen.assertContains(getFqmnPlusBci(METHOD_01, FRAME_01), 45, 99);
            gen.assertContains(getFqmnPlusBci(METHOD_02, FRAME_02), 1, 97);
            gen.assertContains(getFqmnPlusBci(METHOD_03, FRAME_03), 19, 72);
            gen.assertContains(getFqmnPlusBci(METHOD_04, FRAME_04), 0, 44);
            gen.assertContains(getFqmnPlusBci(METHOD_05, FRAME_05), 10, 23);
            gen.assertContains(getFqmnPlusBci(METHOD_07, FRAME_07_1), 3, 3);
            gen.assertContains(getFqmnPlusBci(METHOD_07, FRAME_07_2), 45, 99);
            gen.assertContains(getFqmnPlusBci(METHOD_08, FRAME_08_1), 2, 2);
            gen.assertContains(getFqmnPlusBci(METHOD_08, FRAME_08_2), 7, 7);
            gen.assertContains(getFqmnPlusBci(METHOD_08, FRAME_08_3), 7, 7);
            gen.assertContains(getFqmnPlusBci(METHOD_08, FRAME_08_4), 5, 5);
            gen.assertContains(getFqmnPlusBci(METHOD_10, FRAME_10_1), 2, 4);
            gen.assertContains(getFqmnPlusBci(METHOD_10, FRAME_10_2), 3, 6);
            gen.assertContains(getFqmnPlusBci(METHOD_10, FRAME_10_3), 9, 18);
            gen.assertContains(getFqmnPlusBci(METHOD_10, FRAME_10_4), 9, 18);
            gen.assertContains(getFqmnPlusBci(METHOD_10, FRAME_10_5), 5, 10);
            gen.assertContains(getFqmnPlusBci(METHOD_10, FRAME_10_6), 5, 10);
        }
    }

    @Test
    public void checkFlatAggregationByFqmnPlusLineNr()
    {
        FlatGenerator gen;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, BY_FQMN_LINENR);
            applyScenario01(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(getFqmnPlusLineNr(METHOD_01, FRAME_01), 1, 1);

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, BY_FQMN_LINENR);
            applyScenario02(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmnPlusLineNr(METHOD_01, FRAME_01), 1, 1);
            gen.assertContains(getFqmnPlusLineNr(METHOD_02, FRAME_02), 0, 1);
            gen.assertContains(getFqmnPlusLineNr(METHOD_03, FRAME_03), 0, 1);
            gen.assertContains(getFqmnPlusLineNr(METHOD_04, FRAME_04), 0, 1);
            gen.assertContains(getFqmnPlusLineNr(METHOD_05, FRAME_05), 0, 1);

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, BY_FQMN_LINENR);
            applyScenario03(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmnPlusLineNr(METHOD_01, FRAME_01), 2, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_02, FRAME_02), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_03, FRAME_03), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_04, FRAME_04), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_05, FRAME_05), 0, 2);

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, BY_FQMN_LINENR);
            applyScenario04(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmnPlusLineNr(METHOD_01, FRAME_01), 1, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_02, FRAME_02), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_03, FRAME_03), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_04, FRAME_04), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_05, FRAME_05), 1, 2);

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, BY_FQMN_LINENR);
            applyScenario05(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(getFqmnPlusLineNr(METHOD_01, FRAME_01), 2, 2);

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, BY_FQMN_LINENR);
            applyScenario06(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getFqmnPlusLineNr(METHOD_01, FRAME_01), 2, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_02, FRAME_02), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_03, FRAME_03), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_04, FRAME_04), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_05, FRAME_05), 0, 2);

            // Two threads, mixed line nrs & BCI

            gen = new FlatGenerator(threadGrouping, BY_FQMN_LINENR);
            applyScenario07(gen);

            gen.assertAggregationSizeEquals(8);
            gen.assertContains(getFqmnPlusLineNr(METHOD_01, FRAME_01), 5, 20);
            gen.assertContains(getFqmnPlusLineNr(METHOD_02, FRAME_02), 0, 10);
            gen.assertContains(getFqmnPlusLineNr(METHOD_03, FRAME_03), 0, 10);
            gen.assertContains(getFqmnPlusLineNr(METHOD_04, FRAME_04), 0, 10);
            gen.assertContains(getFqmnPlusLineNr(METHOD_05, FRAME_05), 10, 10);
            gen.assertContains(getFqmnPlusLineNr(METHOD_07, FRAME_07_1), 5, 20);
            gen.assertContains(getFqmnPlusLineNr(METHOD_07, FRAME_07_2), 5, 20);
            gen.assertContains(getFqmnPlusLineNr(METHOD_08, FRAME_08_1), 2, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_08, FRAME_08_2), 3, 6);
            gen.assertContains(getFqmnPlusLineNr(METHOD_08, FRAME_08_3), 0, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_08, FRAME_08_4), 3, 6);

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, BY_FQMN_LINENR);
            applyScenario09(gen);

            gen.assertAggregationSizeEquals(11);
            gen.assertContains(getFqmnPlusLineNr(METHOD_01, FRAME_01), 48, 102);
            gen.assertContains(getFqmnPlusLineNr(METHOD_02, FRAME_02), 1, 97);
            gen.assertContains(getFqmnPlusLineNr(METHOD_03, FRAME_03), 19, 72);
            gen.assertContains(getFqmnPlusLineNr(METHOD_04, FRAME_04), 0, 44);
            gen.assertContains(getFqmnPlusLineNr(METHOD_05, FRAME_05), 10, 23);
            gen.assertContains(getFqmnPlusLineNr(METHOD_07, FRAME_07_1), 48, 102);
            gen.assertContains(getFqmnPlusLineNr(METHOD_07, FRAME_07_2), 48, 102);
            gen.assertContains(getFqmnPlusLineNr(METHOD_08, FRAME_08_1), 2, 2);
            gen.assertContains(getFqmnPlusLineNr(METHOD_08, FRAME_08_2), 8, 8);
            gen.assertContains(getFqmnPlusLineNr(METHOD_08, FRAME_08_3), 4, 4);
            gen.assertContains(getFqmnPlusLineNr(METHOD_08, FRAME_08_4), 8, 8);
            gen.assertContains(getFqmnPlusLineNr(METHOD_10, FRAME_10_1), 7, 14);
            gen.assertContains(getFqmnPlusLineNr(METHOD_10, FRAME_10_2), 7, 14);
            gen.assertContains(getFqmnPlusLineNr(METHOD_10, FRAME_10_3), 4, 8);
            gen.assertContains(getFqmnPlusLineNr(METHOD_10, FRAME_10_4), 8, 16);
            gen.assertContains(getFqmnPlusLineNr(METHOD_10, FRAME_10_5), 7, 14);
            gen.assertContains(getFqmnPlusLineNr(METHOD_10, FRAME_10_6), 8, 16);
        }
    }

    @Test
    public void checkFlatAggregationByMethodId()
    {
        FlatGenerator gen;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario01(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 1, 1);

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario02(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 1, 1);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 1);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 1);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 1);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 0, 1);

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario03(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 2, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 0, 2);

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario04(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 1, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 1, 2);

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario05(gen);

            gen.assertAggregationSizeEquals(1);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 2, 2, nano(2), nano(2));

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario06(gen);

            gen.assertAggregationSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 2, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 2);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 0, 2);

            // Two threads, mixed line nrs & BCI

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario07(gen);

            gen.assertAggregationSizeEquals(7);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 0, 10);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 10);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 10);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 10);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 10, 10);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_07), 5, 10);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_08), 5, 10);

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario09(gen);

            gen.assertAggregationSizeEquals(8);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 42, 96);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 1, 97);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 19, 72);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 44);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 10, 23);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_07), 6, 6);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_08), 14, 14);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_10), 19, 38);
        }
    }
}
