package com.insightfullogic.honest_profiler.core.collector.lean;

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
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.START_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.START_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.START_03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.START_04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.START_05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.THREAD_01;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.THREAD_02;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.THREAD_03;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.THREAD_04;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.THREAD_05;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.method;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.start;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.thread;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.LeanProfileGenerator;

public class LeanLogCollectorTest
{

    // Some rules which should hold for LeanLogCollector :
    //
    // R01 - If no profile is requested through requestProfile(), a profile will only be emitted on an End-Of-Log event.
    // R02 - No profile will be emitted as long as not at least one non-empty stacktrace has been received.
    // R03 - No profile will be emitted when the generator processes a StackFrame.
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
        LeanProfileGenerator generator;

        // R01 - No Request

        generator = new LeanProfileGenerator();

        generator.handle(START_01, FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        generator.handle(START_02, THREAD_01, METHOD_01, START_03);
        generator.handle(FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        generator.handle(START_04, THREAD_02, METHOD_02, START_05);

        generator.assertNothingEmitted();

        // R02 - Single TraceStart, No EOL

        generator = new LeanProfileGenerator(true);
        generator.handle(START_01);
        generator.assertNothingEmitted();

        // R02 - Single TraceStart + StackFrames, No EOL

        generator = new LeanProfileGenerator(true);
        generator.handle(START_01, FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);

        generator.assertNothingEmitted();

        // R02 - Single TraceStart

        generator = new LeanProfileGenerator(true);
        generator.handle(START_01);
        generator.endOfLog();
        generator.assertNothingEmitted();

        // R02 - Multiple TraceStarts

        generator = new LeanProfileGenerator(true);
        generator.handle(START_01, START_02, START_01, START_04);
        generator.endOfLog();

        generator.assertNothingEmitted();

        // R02 - Single Method

        generator = new LeanProfileGenerator(true);
        generator.handle(METHOD_01);
        generator.endOfLog();

        generator.assertNothingEmitted();

        // R02 - Single ThreadMeta

        generator = new LeanProfileGenerator(true);
        generator.handle(thread(1, "Thread-1"));
        generator.endOfLog();

        generator.assertNothingEmitted();

        // R02 - Event mix without StackFrames + EOL

        generator = new LeanProfileGenerator(true);
        generator.handle(
            START_01,
            START_02,
            THREAD_01,
            METHOD_01,
            START_03,
            START_04,
            METHOD_02,
            THREAD_02);
        generator.endOfLog();

        generator.assertNothingEmitted();

        // R03

        generator = new LeanProfileGenerator(true);

        generator.handle(THREAD_01, METHOD_01, THREAD_02, METHOD_02, START_01);
        generator.handle(FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);

        generator.assertNothingEmitted();
    }

    @Test
    public void methodInfoHandling()
    {
        LeanProfileGenerator generator;

        // R07, R08

        generator = new LeanProfileGenerator(true);
        generator.handle(START_01, FRAME_01, THREAD_01, START_02, FRAME_02);
        generator.endOfLog();

        generator.assertSingleEmission();
        generator.assertMethodMapSizeEquals(0);

        // R06

        generator = getPrimedGenerator();
        generator.handle(METHOD_01);
        generator.assertSingleEmission();

        generator.assertMethodMapSizeEquals(1);
        generator.assertContains(METHOD_01);

        // R09

        generator = getPrimedGenerator();
        generator.handle(METHOD_01, method(1, "Bar.java", "com.bar", "bar()"));
        generator.endOfLog();

        generator.assertMethodMapSizeEquals(1);
        generator.assertContains(METHOD_01);

        // Multiple Methods

        generator = getPrimedGenerator();
        generator.handle(METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        generator.endOfLog();

        generator.assertMethodMapSizeEquals(5);
        generator.assertContains(METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
    }

    @Test
    public void threadInfoHandling()
    {
        LeanProfileGenerator generator;

        // R07, R10

        generator = new LeanProfileGenerator(true);
        generator.handle(START_01, FRAME_01, METHOD_01, START_02, FRAME_02);
        generator.endOfLog();
        generator.assertSingleEmission();

        generator.assertSingleEmission();
        generator.assertThreadMapSizeEquals(0);

        // R05

        generator = getPrimedGenerator();
        generator.handle(THREAD_01);
        generator.assertSingleEmission();

        generator.assertThreadMapSizeEquals(1);
        generator.assertContains(THREAD_01);

        // R11 - First ThreadMeta name non-null, Second ThreadMeta name null

        generator = getPrimedGenerator();
        generator.handle(THREAD_01, thread(1, null));
        generator.endOfLog();

        generator.assertThreadMapSizeEquals(1);
        generator.assertContains(THREAD_01);

        // R11 - First ThreadMeta name null, Second ThreadMeta name non-null

        generator = getPrimedGenerator();
        generator.handle(THREAD_01, thread(1, null));
        generator.endOfLog();

        generator.assertThreadMapSizeEquals(1);
        generator.assertContains(THREAD_01);

        // R11 - First ThreadMeta name non-null, Second ThreadMeta name non-null

        generator = getPrimedGenerator();
        generator.handle(thread(1, "A"), thread(1, "B"));
        generator.endOfLog();

        generator.assertThreadMapSizeEquals(1);
        generator.assertContains(thread(1, "B"));

        // R11 - First ThreadMeta name null, Second ThreadMeta name null

        generator = getPrimedGenerator();
        generator.handle(thread(1, null), thread(1, null));
        generator.endOfLog();

        generator.assertThreadMapSizeEquals(1);
        generator.assertContains(thread(1, null));

        // Multiple Methods

        generator = getPrimedGenerator();
        generator.handle(THREAD_01, THREAD_02, THREAD_03, THREAD_04, THREAD_05);
        generator.endOfLog();

        generator.assertThreadMapSizeEquals(5);
        generator.assertContains(THREAD_01, THREAD_02, THREAD_03, THREAD_04, THREAD_05);
    }

    @Test
    public void stackFrameHandling()
    {

        LeanProfileGenerator generator;

        // 1 Stack on 1 thread

        generator = new LeanProfileGenerator();
        generator.handle(THREAD_01);
        generator.handle(METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        generator.handle(start(1, 1, 1, 1));
        generator.handle(FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        generator.handle(start(1, 1, 2, 1));
        generator.endOfLog();

        generator.assertSingleEmission();
        generator.assertThreadMapSizeEquals(1);
        generator.assertMethodMapSizeEquals(5);
        generator.assertProfileThreadCountEquals(1);
        generator.assertProfileContainsStack(1, FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);

        // Stacks on 2 threads, 1 stack per thread

        generator = new LeanProfileGenerator();
        generator.handle(THREAD_01, THREAD_02);
        generator.handle(METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        generator.handle(start(1, 1, 1, 1));
        generator.handle(FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        generator.handle(start(1, 2, 2, 1));
        generator.handle(FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        generator.handle(start(1, 1, 3, 1));
        generator.endOfLog();

        generator.assertSingleEmission();
        generator.assertThreadMapSizeEquals(2);
        generator.assertMethodMapSizeEquals(5);
        generator.assertProfileThreadCountEquals(2);
        generator.assertProfileContainsStack(1, FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        generator.assertProfileContainsStack(2, FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);

        // 2 Stacks on 1 thread

        generator = new LeanProfileGenerator();
        generator.handle(THREAD_01);
        generator.handle(METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        generator.handle(start(1, 1, 1, 1));
        generator.handle(FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        generator.handle(start(1, 1, 2, 1));
        generator.handle(FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        generator.handle(start(1, 1, 3, 1));
        generator.endOfLog();

        generator.assertSingleEmission();
        generator.assertThreadMapSizeEquals(1);
        generator.assertMethodMapSizeEquals(5);
        generator.assertProfileThreadCountEquals(1);
        generator.assertProfileContainsStack(1, FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        generator.assertProfileContainsStack(1, FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
    }

    /**
     * Returns a new generator which has processed a trivial stacktrace and for which a profile has been requested. Use
     * this to declutter tests.
     *
     * @return a generator.
     */
    private LeanProfileGenerator getPrimedGenerator()
    {
        LeanProfileGenerator generator = new LeanProfileGenerator();
        generator.handle(START_01, FRAME_01, START_02);
        generator.requestProfile();
        return generator;
    }
}
