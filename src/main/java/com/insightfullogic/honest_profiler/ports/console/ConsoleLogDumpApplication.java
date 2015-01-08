package com.insightfullogic.honest_profiler.ports.console;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;

/**
 * Converts an hpl log to text, line by line. Only extra convenience offered is the translation on method
 * names where they have already been spelled out in the log.
 * 
 * @author nitsanw
 *
 */
public class ConsoleLogDumpApplication {

    private final Console output;
    private final Console error;

    private File logLocation;

    public static void main(String[] args) {
        ConsoleLogDumpApplication entry = new ConsoleLogDumpApplication(() -> System.err, () -> System.out);
        CmdLineParser parser = new CmdLineParser(entry);

        try {
            parser.parseArgument(args);
            entry.run();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }

    public ConsoleLogDumpApplication(final Console error, final Console output) {
        this.output = output;
        this.error = error;
    }

    @Option(name = "-log", usage = "set the log that you want to parser or use", required = true)
    public void setLogLocation(String logLocation) {
        setLogLocation(new File(logLocation));
    }

    public void setLogLocation(File logLocation) {
        this.logLocation = logLocation;
    }

    public void run() {

        final PrintStream err = error.stream();
        try {
            if (!logLocation.exists() || !logLocation.canRead()) {
                err.println("Unable to find log file at: " + logLocation);
                return;
            }

            final PrintStream out = output.stream();
            out.println("Printing text represantation for: " + logLocation.getAbsolutePath());

            Monitor.consumeFile(new FileLogSource(logLocation), new LogEventListener() {
                int indent;
                long traceidx;
                long errCount;
                Map<Long, ClassMethod> methodNames = new HashMap<>();

                @Override
                public void handle(Method m) {
                    ClassMethod cm = new ClassMethod(m.getClassName(), m.getMethodName());
                    out.printf("Method   : %d -> %s.%s\n", m.getMethodId(), m.getClassName(), m.getMethodName());
                    methodNames.put(m.getMethodId(), cm);
                }

                @Override
                public void handle(StackFrame sf) {
                    indent--;
                    long methodId = sf.getMethodId();
                    ClassMethod cm = methodNames.get(methodId);
                    if (methodId == 0) { // null method
                        errCount++;
                        out.print("StackFrame: ");
                        indent(out);
                        out.printf("%d @ %s\n", methodId, sf.getLineNumber());
                    } else if (cm == null) {
                        out.print("StackFrame: ");
                        indent(out);
                        out.printf("%d @ %s\n", methodId, sf.getLineNumber());
                    } else {
                        out.print("StackFrame: ");
                        indent(out);
                        out.printf("%s::%s @ %s\n", cm.cName, cm.mName, sf.getLineNumber());
                    }
                }

                private void indent(final PrintStream out) {
                    for (int i = 0; i < indent; i++)
                        out.print(" ");
                }

                @Override
                public void handle(TraceStart ts) {
                    int frames = ts.getNumberOfFrames();
                    if (frames <= 0) {
                        out.printf("TraceStart: [%d] tid=%d,frames=%d\n", traceidx, ts.getThreadId(), frames);
                        errCount++;
                    } else {
                        out.printf("TraceStartL [%d] tid=%d,frames=%d\n", traceidx, ts.getThreadId(), frames);
                    }
                    indent = frames;
                    traceidx++;
                }

                @Override
                public void endOfLog() {
                    out.printf("Processed %d traces, %d faulty\n", traceidx, errCount);
                }
            });
        } catch (IOException e) {
            e.printStackTrace(err);
        }
    }

    static class ClassMethod {
        final String cName, mName;

        public ClassMethod(String className, String methodName) {
            this.cName = className;
            this.mName = methodName;
        }
    }

}
