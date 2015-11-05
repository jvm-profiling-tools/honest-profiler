/**
 * Copyright (c) 2014-2015 Richard Warburton (richard.warburton@gmail.com)
 * Copyright (c) 2014-2015 Nitsan Wakart (nitsanw@yahoo.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.console;

import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts an hpl log to text, line by line. Only extra convenience offered is the translation on method
 * names where they have already been spelled out in the log.
 */
class Counter
{
    int i;

    void inc()
    {
        i++;
    }
}

public class ConsoleLogDumpApplication
{
// These name match the names reported by the forte quality kit
//    enum {
//      ticks_no_Java_frame         =  0,
//      ticks_no_class_load         = -1,
//      ticks_GC_active             = -2,
//      ticks_unknown_not_Java      = -3,
//      ticks_not_walkable_not_Java = -4,
//      ticks_unknown_Java          = -5,
//      ticks_not_walkable_Java     = -6,
//      ticks_unknown_state         = -7,
//      ticks_thread_exit           = -8,
//      ticks_deopt                 = -9,
//      ticks_safepoint             = -10
//    };

    public static String[] AGCT_ERRORS = {"No Java Frames",
        "No class load",
        "GC Active",
        "Unknown not Java",
        "Not walkable not Java",
        "Unknown Java",
        "Not walkable Java",
        "Unknown state",
        "Thread exit",
        "Deopt",
        "Safepoint"};

    private final Console output;
    private final Console error;

    private File logLocation;

    public static void main(String[] args)
    {
        ConsoleLogDumpApplication entry = new ConsoleLogDumpApplication(() -> System.err, () -> System.out);
        CmdLineParser parser = new CmdLineParser(entry);

        try
        {
            parser.parseArgument(args);
            entry.run();
        }
        catch (CmdLineException e)
        {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    public ConsoleLogDumpApplication(final Console error, final Console output)
    {
        this.output = output;
        this.error = error;
    }

    @Option(name = "-log", usage = "set the log that you want to parse or use", required = true)
    public void setLogLocation(String logLocation)
    {
        setLogLocation(new File(logLocation));
    }

    public void setLogLocation(File logLocation)
    {
        this.logLocation = logLocation;
    }

    public void run()
    {
        final PrintStream err = error.stream();
        if (!logLocation.exists() || !logLocation.canRead())
        {
            err.println("Unable to find log file at: " + logLocation);
            return;
        }

        final PrintStream out = output.stream();
        out.println("Printing text representation for: " + logLocation.getAbsolutePath());

        Monitor.consumeFile(new FileLogSource(logLocation), new LogEventListener()
        {
            int indent;
            long traceidx;
            long errCount;

            Map<Integer, Counter> errHistogram = new HashMap<>();
            Map<Long, BoundMethod> methodNames = new HashMap<>();

            @Override
            public void handle(Method method)
            {
                BoundMethod boundMethod = new BoundMethod(method.getClassName(), method.getMethodName());
                out.printf("Method    : %d -> %s.%s\n", method.getMethodId(), method.getClassName(), method.getMethodName());
                methodNames.put(method.getMethodId(), boundMethod);
            }

            @Override
            public void handle(StackFrame stackFrame)
            {
                indent--;
                long methodId = stackFrame.getMethodId();
                BoundMethod boundMethod = methodNames.get(methodId);
                if (methodId == 0)
                { // null method
                    errCount++;
                    out.print("StackFrame: ");
                    indent(out);
                    out.printf("%d @ %s (bci=%s)\n", methodId, stackFrame.getLineNumber(), stackFrame.getBci());
                }
                else if (boundMethod == null)
                {
                    out.print("StackFrame: ");
                    indent(out);
                    out.printf("%d @ %s (bci=%s)\n", methodId, stackFrame.getLineNumber(), stackFrame.getBci());
                }
                else
                {
                    out.print("StackFrame: ");
                    indent(out);
                    out.printf("%s::%s @ %s (bci=%s)\n", boundMethod.className, boundMethod.methodName, stackFrame.getLineNumber(), stackFrame.getBci());
                }
            }

            private void indent(final PrintStream out)
            {
                for (int i = 0; i < indent; i++)
                    out.print(' ');
            }

            @Override
            public void handle(TraceStart traceStart)
            {
                int frames = traceStart.getNumberOfFrames();
                if (frames <= 0)
                {
                    out.printf("TraceStart: [%d] tid=%d,err=%d\n", traceidx, traceStart.getThreadId(), frames);
                    errCount++;
                    Counter counter = errHistogram.computeIfAbsent(-frames, k -> new Counter());
                    counter.inc();
                }
                else
                {
                    out.printf("TraceStart: [%d] tid=%d,frames=%d\n", traceidx, traceStart.getThreadId(), frames);
                }
                indent = frames;
                traceidx++;
            }

            @Override
            public void endOfLog()
            {
                out.printf("Processed %d traces, %d faulty\n", traceidx, errCount);
                for (Map.Entry<Integer, Counter> e : errHistogram.entrySet())
                {
                    final Integer errCode = e.getKey();
                    final int errCodeCount = e.getValue().i;
                    String errName;
                    if (errCode < AGCT_ERRORS.length)
                    {
                        errName = AGCT_ERRORS[errCode];
                    }
                    else
                    {
                        errName = "Unknown err code";
                    }
                    out.printf("%-20s (-%d): %d \n", errName, errCode, errCodeCount);
                }
            }
        });
    }

    private static class BoundMethod
    {
        private final String className, methodName;

        public BoundMethod(String className, String methodName)
        {
            this.className = className;
            this.methodName = methodName;
        }
    }

}
