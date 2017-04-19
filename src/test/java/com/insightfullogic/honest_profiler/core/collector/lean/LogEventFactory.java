package com.insightfullogic.honest_profiler.core.collector.lean;

import com.insightfullogic.honest_profiler.core.LeanLogCollectorDriver;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;

public class LogEventFactory
{
    public static final TraceStart START_01 = start(1, 1, 1, 0);
    public static final TraceStart START_02 = start(1, 2, 2, 0);
    public static final TraceStart START_03 = start(1, 3, 3, 0);
    public static final TraceStart START_04 = start(1, 4, 4, 0);
    public static final TraceStart START_05 = start(1, 5, 5, 0);

    public static final StackFrame FRAME_01 = frame(1, 1);
    public static final StackFrame FRAME_02 = frame(2, 2, 2);
    public static final StackFrame FRAME_03 = frame(3, 3);
    public static final StackFrame FRAME_04 = frame(4, 4, 4);
    public static final StackFrame FRAME_05 = frame(5, 5);

    public static final StackFrame FRAME_07_1 = frame(5, 7);
    public static final StackFrame FRAME_07_2 = frame(1, 7);
    public static final StackFrame FRAME_08_1 = frame(5, 5, 8);
    public static final StackFrame FRAME_08_2 = frame(1, 1, 8);
    public static final StackFrame FRAME_08_3 = frame(1, 7, 8);
    public static final StackFrame FRAME_08_4 = frame(7, 1, 8);

    public static final StackFrame FRAME_10_1 = frame(7, 10);
    public static final StackFrame FRAME_10_2 = frame(11, 10);
    public static final StackFrame FRAME_10_3 = frame(17, 19, 10);
    public static final StackFrame FRAME_10_4 = frame(17, 23, 10);
    public static final StackFrame FRAME_10_5 = frame(29, 10);
    public static final StackFrame FRAME_10_6 = frame(29, 23, 10);

    public static final Method METHOD_01 = method(1, "Foo.java", "/com/test/Foo/", "foo()");
    public static final Method METHOD_02 = method(2, "Bar.java", "/com/test/Bar/", "bar()");
    public static final Method METHOD_03 = method(3, "Baz.java", "/com/test/Baz/", "baz()");
    public static final Method METHOD_04 = method(4, "Fnord.java", "/com/test/Fnord/", "fnord()");
    public static final Method METHOD_05 = method(5, "Qux.java", "/com/test/Qux/", "qux()");

    public static final Method METHOD_07 = method(7, "Foo.java", "/com/test/Foo/", "foo()");
    public static final Method METHOD_08 = method(8, "Bar.java", "/com/test/Bar/", "bar()");

    public static final Method METHOD_10 = method(10, "Quux.java", "/com/test/Quux/", "quux()");

    public static final ThreadMeta THREAD_01 = thread(1, "Thread-01");
    public static final ThreadMeta THREAD_02 = thread(2, "Thread-02");
    public static final ThreadMeta THREAD_03 = thread(3, "Thread-03");
    public static final ThreadMeta THREAD_04 = thread(4, "Thread-04");
    public static final ThreadMeta THREAD_05 = thread(5, "Thread-05");
    public static final ThreadMeta THREAD_07 = thread(5, "Thread-07");
    public static final ThreadMeta THREAD_08 = thread(5, "Thread-08");
    public static final ThreadMeta THREAD_10 = thread(5, "Thread-10");

    public static final void applyScenario01(LeanLogCollectorDriver gen)
    {
        gen.handle(THREAD_01, METHOD_01);
        gen.handle(start(1, 1, 1, 0), FRAME_01);
        gen.handle(start(1, 1, 2, 0));
        gen.endOfLog();
    }

    public static final void applyScenario02(LeanLogCollectorDriver gen)
    {
        gen.handle(THREAD_01, METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        gen.handle(start(1, 1, 1, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, 1, 2, 0));
        gen.endOfLog();
    }

    public static final void applyScenario03(LeanLogCollectorDriver gen)
    {
        gen.handle(THREAD_01, METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        gen.handle(start(1, 1, 1, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, 1, 2, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, 1, 3, 0));
        gen.endOfLog();
    }

    public static final void applyScenario04(LeanLogCollectorDriver gen)
    {
        gen.handle(THREAD_01, METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        gen.handle(start(1, 1, 1, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, 1, 2, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, 1, 3, 0));
        gen.endOfLog();
    }

    public static final void applyScenario05(LeanLogCollectorDriver gen)
    {
        gen.handle(THREAD_01, THREAD_02, METHOD_01);
        gen.handle(start(1, 1, 1, 0), FRAME_01);
        gen.handle(start(1, 2, 2, 0), FRAME_01);
        gen.handle(start(1, 1, 3, 0));
        gen.endOfLog();
    }

    public static final void applyScenario06(LeanLogCollectorDriver gen)
    {
        gen.handle(THREAD_01, METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        gen.handle(start(1, 1, 1, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, 2, 2, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, 1, 3, 0));
        gen.endOfLog();
    }

    public static final void applyScenario07(LeanLogCollectorDriver gen)
    {
        gen.handle(THREAD_07, THREAD_08);
        gen.handle(METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05, METHOD_07, METHOD_08);

        int sec = 0;
        int thr = 06;

        // Thread 7
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_1, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_1, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_07_1, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_07_1, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_07_2, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_07_2, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_07_2, FRAME_04, FRAME_03);

        // Thread 8
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_1, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_1, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_08_3, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_08_3, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_08_4, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_08_4, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_08_4, FRAME_04, FRAME_03);

        gen.handle(start(1, thr, ++sec, 0));
        gen.endOfLog();
    }

    public static final void applyScenario09(LeanLogCollectorDriver gen)
    {
        gen.handle(THREAD_01, THREAD_02, THREAD_03, THREAD_04, THREAD_05);
        gen.handle(THREAD_07, THREAD_08, THREAD_10);
        gen.handle(METHOD_01, METHOD_02, METHOD_03, METHOD_04, METHOD_05);
        gen.handle(METHOD_07, METHOD_08, METHOD_10);

        int sec = 0;
        int thr = 0;

        // Thread 1
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);

        // Thread 2
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);

        // Thread 3
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_05, FRAME_04, FRAME_03, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);

        // Thread 4
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_02);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_04);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);

        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);

        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_02);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02);

        // Thread 5
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_02);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_04);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);

        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);

        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_02);

        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02);

        // Thread 6 (No ThreadInfo)
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_02);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_04);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_05);

        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03);

        gen.handle(start(1, thr, ++sec, 0), FRAME_02);

        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_01, FRAME_02, FRAME_03, FRAME_04, FRAME_02);

        // Thread 7 ()
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_1);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_1);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_1);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_2);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_2);
        gen.handle(start(1, thr, ++sec, 0), FRAME_07_2);

        // Thread 8 ()
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_1);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_1);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_2);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_2);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_2);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_3);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_3);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_3);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_3);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_4);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_4);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_4);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_4);
        gen.handle(start(1, thr, ++sec, 0), FRAME_08_4);

        // Thread 10 (Bci/Line Nr differentiation, Self + Total)
        thr = 10;
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_1, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_1, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_10_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_2, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_10_3, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_3, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_3, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_3, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_10_4, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_4, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_4, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_4, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_4, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_10_5, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_5, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_10_6, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_6, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_10_6, FRAME_02, FRAME_01);

        // Thread 11 (Bci/Line Nr differentiation, Total only)
        thr++;
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_1, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_1, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_2, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_2, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_3, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_3, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_3, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_3, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_4, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_4, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_4, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_4, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_4, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_5, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_5, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_6, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_6, FRAME_02, FRAME_01);
        gen.handle(start(1, thr, ++sec, 0), FRAME_03, FRAME_10_6, FRAME_02, FRAME_01);

        gen.handle(start(1, thr, ++sec, 0));

        gen.endOfLog();
    }

    public static final TraceStart start(int numberOfFrames, long threadId, long sec, long nano)
    {
        return new TraceStart(numberOfFrames, threadId, sec, nano);
    }

    public static final Method method(long id, String fileName, String className, String methodName)
    {
        return new Method(id, fileName, className, methodName);
    }

    public static final ThreadMeta thread(long threadId, String name)
    {
        return new ThreadMeta(threadId, name);
    }

    public static final StackFrame frame(int bci, long methodId)
    {
        return new StackFrame(bci, methodId);
    }

    public static final StackFrame frame(int bci, int lineNumber, long methodId)
    {
        return new StackFrame(bci, lineNumber, methodId);
    }
}
