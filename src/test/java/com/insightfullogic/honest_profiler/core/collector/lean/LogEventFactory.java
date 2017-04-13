package com.insightfullogic.honest_profiler.core.collector.lean;

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

    public static final Method METHOD_01 = method(1, "Foo.java", "/com/test/foo/", "foo()");
    public static final Method METHOD_02 = method(2, "Bar.java", "/com/test/bar/", "bar()");
    public static final Method METHOD_03 = method(3, "Baz.java", "/com/test/baz/", "baz()");
    public static final Method METHOD_04 = method(4, "Fnord.java", "/com/test/fnord/", "fnord()");
    public static final Method METHOD_05 = method(5, "Qux.java", "/com/test/qux/", "qux()");

    public static final ThreadMeta THREAD_01 = thread(1, "Thread-1");
    public static final ThreadMeta THREAD_02 = thread(2, "Thread-2");
    public static final ThreadMeta THREAD_03 = thread(3, "Thread-3");
    public static final ThreadMeta THREAD_04 = thread(4, "Thread-4");
    public static final ThreadMeta THREAD_05 = thread(5, "Thread-5");

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
