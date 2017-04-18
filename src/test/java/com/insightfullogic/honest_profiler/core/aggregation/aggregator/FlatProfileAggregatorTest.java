package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.getMethodIdPlusFqmn;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_METHOD_ID;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.FRAME_05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.METHOD_05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.THREAD_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.THREAD_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.start;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.FlatGenerator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;

public class FlatProfileAggregatorTest
{
    @Test
    public void checkFlatAggregation()
    {
        FlatGenerator gen;

        for (ThreadGrouping threadGrouping : ThreadGrouping.values())
        {
            // Single thread, single frame

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            gen.handle(THREAD_01, METHOD_01);
            gen.handle(start(1, 1, 1, 0), FRAME_01);
            gen.handle(start(1, 1, 2, 0));
            gen.endOfLog();

            gen.assertSizeEquals(1);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 1, 1, nano(1), nano(1));

            // Single thread, single stack

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            gen.handle(THREAD_01, METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
            gen.handle(start(1, 1, 1, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
            gen.handle(start(1, 1, 2, 0));
            gen.endOfLog();

            gen.assertSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 1, 1, nano(1), nano(1));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 1, 0, nano(1));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 1, 0, nano(1));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 1, 0, nano(1));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 0, 1, 0, nano(1));

            // Single thread, two identical stacks

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            gen.handle(THREAD_01, METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
            gen.handle(start(1, 1, 1, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
            gen.handle(start(1, 1, 2, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
            gen.handle(start(1, 1, 3, 0));
            gen.endOfLog();

            gen.assertSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 2, 2, nano(2), nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 0, 2, 0, nano(2));

            // Single thread, two opposite stacks

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            gen.handle(THREAD_01, METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
            gen.handle(start(1, 1, 1, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
            gen.handle(start(1, 1, 2, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
            gen.handle(start(1, 1, 3, 0));
            gen.endOfLog();

            gen.assertSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 1, 2, nano(1), nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 1, 2, nano(1), nano(2));

            // Two threads, single frame

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            gen.handle(THREAD_01, THREAD_02, METHOD_01);
            gen.handle(start(1, 1, 1, 0), FRAME_01);
            gen.handle(start(1, 2, 2, 0), FRAME_01);
            gen.handle(start(1, 1, 3, 0));
            gen.endOfLog();

            gen.assertSizeEquals(1);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 2, 2, nano(2), nano(2));

            // Two threads, single stack

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            gen.handle(THREAD_01, METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
            gen.handle(start(1, 1, 1, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
            gen.handle(start(1, 2, 2, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
            gen.handle(start(1, 1, 3, 0));
            gen.endOfLog();

            gen.assertSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 2, 2, nano(2), nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 2, 0, nano(2));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 0, 2, 0, nano(2));

            // Full Scenario

            gen = new FlatGenerator(threadGrouping, BY_METHOD_ID);
            applyScenario(gen);
            gen.assertSizeEquals(5);
            gen.assertContains(getMethodIdPlusFqmn(METHOD_01), 42, 58, nano(42), nano(58));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_02), 1, 59, nano(1), nano(59));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_03), 0, 53, 0, nano(53));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_04), 0, 44, 0, nano(44));
            gen.assertContains(getMethodIdPlusFqmn(METHOD_05), 10, 23, nano(10), nano(23));
        }
    }
}
