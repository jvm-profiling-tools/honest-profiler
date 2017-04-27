package com.insightfullogic.honest_profiler.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.parser.LogEvent;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.framework.scenario.Scenario01;
import com.insightfullogic.honest_profiler.framework.scenario.Scenario02;
import com.insightfullogic.honest_profiler.framework.scenario.Scenario03;
import com.insightfullogic.honest_profiler.framework.scenario.Scenario04;
import com.insightfullogic.honest_profiler.framework.scenario.Scenario05;
import com.insightfullogic.honest_profiler.framework.scenario.Scenario06;
import com.insightfullogic.honest_profiler.framework.scenario.Scenario07;
import com.insightfullogic.honest_profiler.framework.scenario.Scenario08;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;

/**
 * Utility class with methods for concisely generating {@link LogEvent}s, and containing predefined log events and
 * scenarios.
 */
public final class LogEventFactory
{
    private static final String FN_01 = "Foo.java";
    private static final String FN_02 = "Bar.java";
    private static final String FN_03 = "Baz.java";
    private static final String FN_04 = "Fnord.java";
    private static final String FN_05 = "Qux.java";
    private static final String FN_10 = "Quux.java";

    private static final String CN_01 = "/com/test/Foo/";
    private static final String CN_02 = "/com/test/Bar/";
    private static final String CN_03 = "/com/test/Baz/";
    private static final String CN_04 = "/com/test/Fnord/";
    private static final String CN_05 = "/com/test/Qux/";
    private static final String CN_10 = "/com/test/Quux/";

    private static final String MN_01 = "foo()";
    private static final String MN_02 = "bar()";
    private static final String MN_03 = "baz()";
    private static final String MN_04 = "fnord()";
    private static final String MN_05 = "qux()";
    private static final String MN_10 = "quux()";

    // Predefined log events

    public static final TraceStart S_01 = start(1, 1, 1, 0);
    public static final TraceStart S_02 = start(1, 2, 2, 0);
    public static final TraceStart S_03 = start(1, 3, 3, 0);
    public static final TraceStart S_04 = start(1, 4, 4, 0);
    public static final TraceStart S_05 = start(1, 5, 5, 0);

    public static final StackFrame F_01 = frame(1, 1);
    public static final StackFrame F_02 = frame(2, 2, 2);
    public static final StackFrame F_03 = frame(3, 3);
    public static final StackFrame F_04 = frame(4, 4, 4);
    public static final StackFrame F_05 = frame(5, 5);

    public static final StackFrame F_07_1 = frame(5, 7);
    public static final StackFrame F_07_2 = frame(1, 7);
    public static final StackFrame F_08_1 = frame(5, 5, 8);
    public static final StackFrame F_08_2 = frame(1, 1, 8);
    public static final StackFrame F_08_3 = frame(1, 7, 8);
    public static final StackFrame F_08_4 = frame(7, 1, 8);

    public static final StackFrame F_10_1 = frame(7, 10);
    public static final StackFrame F_10_2 = frame(11, 10);
    public static final StackFrame F_10_3 = frame(17, 19, 10);
    public static final StackFrame F_10_4 = frame(17, 23, 10);
    public static final StackFrame F_10_5 = frame(29, 10);
    public static final StackFrame F_10_6 = frame(29, 23, 10);

    public static final Method M_01 = method(1, FN_01, CN_01, MN_01);
    public static final Method M_02 = method(2, FN_02, CN_02, MN_02);
    public static final Method M_03 = method(3, FN_03, CN_03, MN_03);
    public static final Method M_04 = method(4, FN_04, CN_04, MN_04);
    public static final Method M_05 = method(5, FN_05, CN_05, MN_05);

    public static final Method M_07 = method(7, FN_01, CN_01, MN_01);
    public static final Method M_08 = method(8, FN_02, CN_02, MN_02);

    public static final Method M_10 = method(10, FN_10, CN_10, MN_10);

    public static final ThreadMeta T_01 = thread(1, "Thread-01");
    public static final ThreadMeta T_02 = thread(2, "Thread-02");
    public static final ThreadMeta T_03 = thread(3, "Thread-03");
    public static final ThreadMeta T_04 = thread(4, "Thread-04");
    public static final ThreadMeta T_05 = thread(5, T_04.getThreadName());
    public static final ThreadMeta T_07 = thread(7, "Thread-07");
    public static final ThreadMeta T_08 = thread(8, "Thread-08");
    public static final ThreadMeta T_10 = thread(10, "Thread-10");
    public static final ThreadMeta T_11 = thread(11, "Thread-11");

    public static final List<SimplifiedLogScenario> SCENARIOS = new ArrayList<>();

    static
    {
        SCENARIOS.add(new Scenario01());
        SCENARIOS.add(new Scenario02());
        SCENARIOS.add(new Scenario03());
        SCENARIOS.add(new Scenario04());
        SCENARIOS.add(new Scenario05());
        SCENARIOS.add(new Scenario06());
        SCENARIOS.add(new Scenario07());
        SCENARIOS.add(new Scenario08());
    }

    // Key generation assistance

    private static final Map<Long, Method> idToMethodMap = new HashMap<>();

    static
    {
        idToMethodMap.put(M_01.getMethodId(), M_01);
        idToMethodMap.put(M_02.getMethodId(), M_02);
        idToMethodMap.put(M_03.getMethodId(), M_03);
        idToMethodMap.put(M_04.getMethodId(), M_04);
        idToMethodMap.put(M_05.getMethodId(), M_05);
        idToMethodMap.put(M_07.getMethodId(), M_07);
        idToMethodMap.put(M_08.getMethodId(), M_08);
        idToMethodMap.put(M_10.getMethodId(), M_10);
    }

    public static final Method methodFor(StackFrame frame)
    {
        return idToMethodMap.get(frame.getMethodId());
    }

    // Log event creation

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

    // Instance Constructors

    private LogEventFactory()
    {
        // Private utility class constructor.
    }
}
