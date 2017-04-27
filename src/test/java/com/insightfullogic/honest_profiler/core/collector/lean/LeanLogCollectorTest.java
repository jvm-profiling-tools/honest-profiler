package com.insightfullogic.honest_profiler.core.collector.lean;

import static com.insightfullogic.honest_profiler.framework.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.S_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.S_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.S_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.S_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.S_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.frame;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.method;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.start;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.thread;

import org.junit.Test;

import com.insightfullogic.honest_profiler.framework.generator.LeanProfileGenerator;

public class LeanLogCollectorTest
{

    // Some rules which should hold for LeanLogCollector :
    //
    // R01 - If no profile is requested through requestProfile(), a profile will only be emitted on an End-Of-Log event.
    // R02 - No profile will be emitted as long as not at least one non-empty stacktrace has been received.
    // R03 - No profile will be emitted when the gen processes a StackFrame.
    // R04 - A ThreadStart event will trigger emission.
    // R05 - A ThreadMeta event will trigger emission.
    // R06 - A MethodInfo event will trigger emission.
    // R07 - The End-Of-Log event will trigger emission.
    // R08 - If no Method events have been processed, the Method Info map is empty.
    // R09 - When receiving many Method events for the same Id, only the first one counts.
    // R10 - If no ThreadMeta events have been processed, the Thread Info map is empty.
    // R11 - When receiving many ThreadMeta events for the same Id, only the last one with a non-null name counts.

    @Test
    public void noProfile()
    {
        LeanProfileGenerator gen;

        // R01 - No Request

        gen = new LeanProfileGenerator();

        gen.handle(S_01, F_01, F_02, F_03, F_04, F_05);
        gen.handle(S_02, T_01, M_01, S_03);
        gen.handle(F_01, F_02, F_03, F_04, F_05);
        gen.handle(S_04, T_02, M_02, S_05);

        gen.assertNothingEmitted();

        // R02 - Single TraceStart, No EOL

        gen = new LeanProfileGenerator(true);
        gen.handle(S_01);
        gen.assertNothingEmitted();

        // R02 - Single TraceStart + StackFrames, No EOL

        gen = new LeanProfileGenerator(true);
        gen.handle(S_01, F_01, F_02, F_03, F_04, F_05);

        gen.assertNothingEmitted();

        // R02 - Single TraceStart

        gen = new LeanProfileGenerator(true);
        gen.handle(S_01);
        gen.endOfLog();
        gen.assertNothingEmitted();

        // R02 - Multiple TraceStarts

        gen = new LeanProfileGenerator(true);
        gen.handle(S_01, S_02, S_01, S_04);
        gen.endOfLog();

        gen.assertNothingEmitted();

        // R02 - Single Method

        gen = new LeanProfileGenerator(true);
        gen.handle(M_01);
        gen.endOfLog();

        gen.assertNothingEmitted();

        // R02 - Single ThreadMeta

        gen = new LeanProfileGenerator(true);
        gen.handle(thread(1, "Thread-1"));
        gen.endOfLog();

        gen.assertNothingEmitted();

        // R02 - Event mix without StackFrames + EOL

        gen = new LeanProfileGenerator(true);
        gen.handle(S_01, S_02, T_01, M_01);
        gen.handle(S_03, S_04, M_02, T_02);
        gen.endOfLog();

        gen.assertNothingEmitted();

        // R03

        gen = new LeanProfileGenerator(true);

        gen.handle(T_01, M_01, T_02, M_02, S_01);
        gen.handle(F_01, F_02, F_03, F_04, F_05);

        gen.assertNothingEmitted();
    }

    @Test
    public void methodInfoHandling()
    {
        LeanProfileGenerator gen;

        // R07, R08

        gen = new LeanProfileGenerator(true);
        gen.handle(S_01, F_01, T_01, S_02, F_02);
        gen.endOfLog();

        gen.assertSingleEmission();
        gen.assertMethodMapSizeEquals(0);

        // R06

        gen = getPrimedGenerator();
        gen.handle(M_01);
        gen.assertSingleEmission();

        gen.assertMethodMapSizeEquals(1);
        gen.assertContains(M_01);

        // R09

        gen = getPrimedGenerator();
        gen.handle(M_01, method(1, "Bar.java", "com.bar", "bar()"));
        gen.endOfLog();

        gen.assertMethodMapSizeEquals(1);
        gen.assertContains(M_01);

        // Multiple Methods

        gen = getPrimedGenerator();
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.endOfLog();

        gen.assertMethodMapSizeEquals(5);
        gen.assertContains(M_01, M_02, M_03, M_04, M_05);
    }

    @Test
    public void threadInfoHandling()
    {
        LeanProfileGenerator gen;

        // R07, R10

        gen = new LeanProfileGenerator(true);
        gen.handle(S_01, F_01, M_01, S_02, F_02);
        gen.endOfLog();
        gen.assertSingleEmission();

        gen.assertSingleEmission();
        gen.assertThreadMapSizeEquals(0);

        // R05

        gen = getPrimedGenerator();
        gen.handle(T_01);
        gen.assertSingleEmission();

        gen.assertThreadMapSizeEquals(1);
        gen.assertContains(T_01);

        // R11 - First ThreadMeta name non-null, Second ThreadMeta name null

        gen = getPrimedGenerator();
        gen.handle(T_01, thread(1, null));
        gen.endOfLog();

        gen.assertThreadMapSizeEquals(1);
        gen.assertContains(T_01);

        // R11 - First ThreadMeta name null, Second ThreadMeta name non-null

        gen = getPrimedGenerator();
        gen.handle(T_01, thread(1, null));
        gen.endOfLog();

        gen.assertThreadMapSizeEquals(1);
        gen.assertContains(T_01);

        // R11 - First ThreadMeta name non-null, Second ThreadMeta name non-null

        gen = getPrimedGenerator();
        gen.handle(thread(1, "A"), thread(1, "B"));
        gen.endOfLog();

        gen.assertThreadMapSizeEquals(1);
        gen.assertContains(thread(1, "B"));

        // R11 - First ThreadMeta name null, Second ThreadMeta name null

        gen = getPrimedGenerator();
        gen.handle(thread(1, null), thread(1, null));
        gen.endOfLog();

        gen.assertThreadMapSizeEquals(1);
        gen.assertContains(thread(1, null));

        // Multiple Methods

        gen = getPrimedGenerator();
        gen.handle(T_01, T_02, T_03, T_04, T_05);
        gen.endOfLog();

        gen.assertThreadMapSizeEquals(5);
        gen.assertContains(T_01, T_02, T_03, T_04, T_05);
    }

    @Test
    public void stackFrameHandling()
    {
        LeanProfileGenerator gen;

        // 1 Stack on 1 thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 1, 2, 0));
        gen.endOfLog();

        gen.assertSingleEmission();
        gen.assertThreadMapSizeEquals(1);
        gen.assertMethodMapSizeEquals(5);
        gen.assertProfileThreadCountEquals(1);
        gen.assertProfileContainsStack(1, F_01, F_02, F_03, F_04, F_05);

        // Stacks on 2 threads, 1 stack per thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01, T_02);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 2, 2, 0), F_05, F_04, F_03, F_02, F_01);
        gen.handle(start(1, 1, 3, 0));
        gen.endOfLog();

        gen.assertSingleEmission();
        gen.assertThreadMapSizeEquals(2);
        gen.assertMethodMapSizeEquals(5);
        gen.assertProfileThreadCountEquals(2);
        gen.assertProfileContainsStack(1, F_01, F_02, F_03, F_04, F_05);
        gen.assertProfileContainsStack(2, F_05, F_04, F_03, F_02, F_01);

        // 2 Stacks on 1 thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 1, 2, 0), F_05, F_04, F_03, F_02, F_01);
        gen.handle(start(1, 1, 3, 0));
        gen.endOfLog();

        gen.assertSingleEmission();
        gen.assertThreadMapSizeEquals(1);
        gen.assertMethodMapSizeEquals(5);
        gen.assertProfileThreadCountEquals(1);
        gen.assertProfileContainsStack(1, F_01, F_02, F_03, F_04, F_05);
        gen.assertProfileContainsStack(1, F_05, F_04, F_03, F_02, F_01);

        // Same method Id, distinct line nrs and bci
        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01);
        gen.handle(start(1, 1, 1, 0), frame(5, 5, 1));
        gen.handle(start(1, 1, 2, 0), frame(10, 5, 1));
        gen.handle(start(1, 1, 3, 0), frame(5, 10, 1));
        gen.handle(start(1, 1, 4, 0), frame(5, 1));
        gen.handle(start(1, 1, 5, 0), frame(10, 1));
        gen.endOfLog();

        gen.assertSingleEmission();
        gen.assertMethodMapSizeEquals(1);
        gen.assertProfileThreadCountEquals(1);
        gen.assertProfileContainsStack(1, frame(5, 5, 1));
        gen.assertProfileContainsStack(1, frame(10, 5, 1));
        gen.assertProfileContainsStack(1, frame(5, 10, 1));
        gen.assertProfileContainsStack(1, frame(5, 1));
        gen.assertProfileContainsStack(1, frame(10, 1));
    }

    @Test
    public void countAggregation()
    {
        LeanProfileGenerator gen;

        // Single stack, 1 Thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.endOfLog();

        gen.assertCountsEqual(0, 1, 1, F_05);
        gen.assertCountsEqual(0, 1, 1, F_04, F_05);
        gen.assertCountsEqual(0, 1, 1, F_03, F_04, F_05);
        gen.assertCountsEqual(0, 1, 1, F_02, F_03, F_04, F_05);
        gen.assertCountsEqual(1, 1, 1, F_01, F_02, F_03, F_04, F_05);

        // Two identical stacks, 1 Thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 1, 2, 0), F_01, F_02, F_03, F_04, F_05);
        gen.endOfLog();

        gen.assertCountsEqual(0, 2, 1, F_05);
        gen.assertCountsEqual(0, 2, 1, F_04, F_05);
        gen.assertCountsEqual(0, 2, 1, F_03, F_04, F_05);
        gen.assertCountsEqual(0, 2, 1, F_02, F_03, F_04, F_05);
        gen.assertCountsEqual(2, 2, 1, F_01, F_02, F_03, F_04, F_05);

        // Two different stacks, 1 Thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 1, 2, 0), F_05, F_04, F_03, F_02, F_01);
        gen.endOfLog();

        gen.assertCountsEqual(0, 1, 1, F_05);
        gen.assertCountsEqual(0, 1, 1, F_04, F_05);
        gen.assertCountsEqual(0, 1, 1, F_03, F_04, F_05);
        gen.assertCountsEqual(0, 1, 1, F_02, F_03, F_04, F_05);
        gen.assertCountsEqual(1, 1, 1, F_01, F_02, F_03, F_04, F_05);
        gen.assertCountsEqual(0, 1, 1, F_01);
        gen.assertCountsEqual(0, 1, 1, F_02, F_01);
        gen.assertCountsEqual(0, 1, 1, F_03, F_02, F_01);
        gen.assertCountsEqual(0, 1, 1, F_04, F_03, F_02, F_01);
        gen.assertCountsEqual(1, 1, 1, F_05, F_04, F_03, F_02, F_01);

        // Three stacks, 2 being substacks of the third, 1 Thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_03, F_02, F_01);
        gen.handle(start(1, 1, 2, 0), F_05, F_04, F_03, F_02, F_01);
        gen.handle(start(1, 1, 3, 0), F_03, F_02, F_01);
        gen.endOfLog();

        gen.assertCountsEqual(0, 3, 1, F_01);
        gen.assertCountsEqual(0, 3, 1, F_02, F_01);
        gen.assertCountsEqual(2, 3, 1, F_03, F_02, F_01);
        gen.assertCountsEqual(0, 1, 1, F_04, F_03, F_02, F_01);
        gen.assertCountsEqual(1, 1, 1, F_05, F_04, F_03, F_02, F_01);

        // Single stack, 2 Threads

        gen = new LeanProfileGenerator();
        gen.handle(T_01, T_02);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 2, 2, 0), F_01, F_02, F_03, F_04, F_05);
        gen.endOfLog();

        gen.assertCountsEqual(0, 1, 1, F_05);
        gen.assertCountsEqual(0, 1, 1, F_04, F_05);
        gen.assertCountsEqual(0, 1, 1, F_03, F_04, F_05);
        gen.assertCountsEqual(0, 1, 1, F_02, F_03, F_04, F_05);
        gen.assertCountsEqual(1, 1, 1, F_01, F_02, F_03, F_04, F_05);
        gen.assertCountsEqual(0, 1, 2, F_05);
        gen.assertCountsEqual(0, 1, 2, F_04, F_05);
        gen.assertCountsEqual(0, 1, 2, F_03, F_04, F_05);
        gen.assertCountsEqual(0, 1, 2, F_02, F_03, F_04, F_05);
        gen.assertCountsEqual(1, 1, 2, F_01, F_02, F_03, F_04, F_05);
    }

    @Test
    public void timeAggregation()
    {
        LeanProfileGenerator gen;

        // Single stack, 1 Thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 1, 2, 0));
        gen.endOfLog();

        gen.assertTimesEqual(0, nano(1), 1, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_03, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_02, F_03, F_04, F_05);
        gen.assertTimesEqual(nano(1), nano(1), 1, F_01, F_02, F_03, F_04, F_05);

        // Two identical stacks, 1 Thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 1, 2, 0), F_01, F_02, F_03, F_04, F_05);
        gen.endOfLog();

        gen.assertTimesEqual(0, nano(2), 1, F_05);
        gen.assertTimesEqual(0, nano(2), 1, F_04, F_05);
        gen.assertTimesEqual(0, nano(2), 1, F_03, F_04, F_05);
        gen.assertTimesEqual(0, nano(2), 1, F_02, F_03, F_04, F_05);
        gen.assertTimesEqual(nano(2), nano(2), 1, F_01, F_02, F_03, F_04, F_05);

        // Two different stacks, 1 Thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 1, 2, 0), F_05, F_04, F_03, F_02, F_01);
        gen.endOfLog();

        gen.assertTimesEqual(0, nano(1), 1, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_03, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_02, F_03, F_04, F_05);
        gen.assertTimesEqual(nano(1), nano(1), 1, F_01, F_02, F_03, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_01);
        gen.assertTimesEqual(0, nano(1), 1, F_02, F_01);
        gen.assertTimesEqual(0, nano(1), 1, F_03, F_02, F_01);
        gen.assertTimesEqual(0, nano(1), 1, F_04, F_03, F_02, F_01);
        gen.assertTimesEqual(nano(1), nano(1), 1, F_05, F_04, F_03, F_02, F_01);

        // Three stacks, 2 being substacks of the third, 1 Thread

        gen = new LeanProfileGenerator();
        gen.handle(T_01);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_03, F_02, F_01);
        gen.handle(start(1, 1, 2, 0), F_05, F_04, F_03, F_02, F_01);
        gen.handle(start(1, 1, 3, 0), F_03, F_02, F_01);
        gen.endOfLog();

        gen.assertTimesEqual(0, nano(3), 1, F_01);
        gen.assertTimesEqual(0, nano(3), 1, F_02, F_01);
        gen.assertTimesEqual(nano(2), nano(3), 1, F_03, F_02, F_01);
        gen.assertTimesEqual(0, nano(1), 1, F_04, F_03, F_02, F_01);
        gen.assertTimesEqual(nano(1), nano(1), 1, F_05, F_04, F_03, F_02, F_01);

        // Single stack, 2 Threads

        gen = new LeanProfileGenerator();
        gen.handle(T_01, T_02);
        gen.handle(M_01, M_02, M_03, M_04, M_05);
        gen.handle(start(1, 1, 1, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 2, 2, 0), F_01, F_02, F_03, F_04, F_05);
        gen.handle(start(1, 2, 3, 0));
        gen.endOfLog();

        gen.assertTimesEqual(0, nano(1), 1, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_03, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 1, F_02, F_03, F_04, F_05);
        gen.assertTimesEqual(nano(1), nano(1), 1, F_01, F_02, F_03, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 2, F_05);
        gen.assertTimesEqual(0, nano(1), 2, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 2, F_03, F_04, F_05);
        gen.assertTimesEqual(0, nano(1), 2, F_02, F_03, F_04, F_05);
        gen.assertTimesEqual(nano(1), nano(1), 2, F_01, F_02, F_03, F_04, F_05);

        // Seconds + Nanos handling

        // 1s + 500n diff

        gen = new LeanProfileGenerator();
        gen.handle(T_01, M_01);
        gen.handle(start(1, 1, 1, 0), F_01, start(1, 1, 2, 500));
        gen.endOfLog();

        gen.assertTimesEqual(nano(1) + 500, nano(1) + 500, 1, F_01);

        // 0s + 250n diff

        gen = new LeanProfileGenerator();
        gen.handle(T_01, M_01);
        gen.handle(start(1, 1, 2, 250), F_01, start(1, 1, 2, 500));
        gen.endOfLog();

        gen.assertTimesEqual(250, 250, 1, F_01);

        // 500n diff across second boundary

        gen = new LeanProfileGenerator();
        gen.handle(T_01, M_01);
        gen.handle(start(1, 1, 1, 999999750), F_01, start(1, 1, 2, 250));
        gen.endOfLog();

        gen.assertTimesEqual(500, 500, 1, F_01);

        // 1 s + 500n diff across second boundary

        gen = new LeanProfileGenerator();
        gen.handle(T_01, M_01);
        gen.handle(start(1, 1, 1, 999999750), F_01, start(1, 1, 3, 250));
        gen.endOfLog();

        gen.assertTimesEqual(nano(1) + 500, nano(1) + 500, 1, F_01);

        // 2 s across second boundary

        gen = new LeanProfileGenerator();
        gen.handle(T_01, M_01);
        gen.handle(start(1, 1, 1, 500), F_01, start(1, 1, 3, 500));
        gen.endOfLog();

        gen.assertTimesEqual(nano(2), nano(2), 1, F_01);
    }

    /**
     * Returns a new gen which has processed a trivial stacktrace and for which a profile has been requested. Use this
     * to declutter tests.
     *
     * @return a gen.
     */
    private LeanProfileGenerator getPrimedGenerator()
    {
        LeanProfileGenerator gen = new LeanProfileGenerator();
        gen.handle(S_01, F_01, S_02);
        gen.requestProfile();
        return gen;
    }
}
