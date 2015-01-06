package com.insightfullogic.honest_profiler.ports.console;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.insightfullogic.honest_profiler.core.Conductor;
import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;

public class ConsoleLogDumpApplication {

    private final Console output;
    private final Console error;

    private File logLocation;
    

    private boolean sfQuiet=false;
    private boolean mQuiet=true;

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
    
    @Option(name = "-qsf", usage = "set stack frame logging to quiet", required = false)
    public void setStackFramePrintQuiet(boolean sfQuiet) {
        this.sfQuiet = sfQuiet;
    }

    @Option(name = "-qmm", usage = "set method mapping logging to quiet", required = false)
    public void setMethodMappingPrintQuiet(boolean mQuiet) {
        this.mQuiet = mQuiet;
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
            out.println("Printing Profile for: " + logLocation.getAbsolutePath());

            Monitor.consumeFile(new FileLogSource(logLocation), new LogEventListener() {
                int indent;
                long traceidx;
                long errCount;
                Map<Long,ClassMethod> methodNames = new HashMap<>();
                private boolean shouldBreak;
                @Override
                public void handle(Method m) {
                    ClassMethod cm = new ClassMethod(m.getClassName(), m.getMethodName());
                    if (!mQuiet) 
                    out.printf("m: %d -> %s.%s\n",m.getMethodId(), m.getClassName(), m.getMethodName());
                    methodNames.put(m.getMethodId(), cm);
                }
                
                @Override
                public void handle(StackFrame sf) {
                    indent--;
                    if (!sfQuiet) {
                        long methodId = sf.getMethodId();
                        ClassMethod cm = methodNames.get(methodId);
                        if (methodId == 0) {
                            errCount++;
                            shouldBreak = true;
                            err.print("sf: ");
                            for (int i = 0; i < indent; i++)
                                err.print(" ");
                            err.printf("%d @ %s\n", methodId, sf.getLineNumber());
                        }
                        else if (cm == null) {
                            out.print("sf: ");
                            for (int i = 0; i < indent; i++)
                                out.print(" ");
                            out.printf("%d @ %s\n", methodId, sf.getLineNumber());
                        } else {
                            out.print("sf: ");
                            for (int i = 0; i < indent; i++)
                                out.print(" ");
                            out.printf("%s::%s @ %s\n", cm.cName, cm.mName, sf.getLineNumber());
                        }
                        breakPoint(out);
                    }
                }
                
                @Override
                public void handle(TraceStart ts) {
                    breakPoint(out);
                    int frames = ts.getNumberOfFrames();
                    if(frames <= 0) {
                        out.flush();
                        err.printf("ts[%d] tid=%d,frames=%d\n",traceidx,ts.getThreadId(),frames);
                        err.flush();
                        errCount++;
//                        shouldBreak=true;
                    }
                    else {
                      out.printf("ts[%d] tid=%d,frames=%d\n",traceidx,ts.getThreadId(),frames);
                    }
                    indent=frames;
                    traceidx++;
                    if (traceidx%1000 == 0) {
                        shouldBreak=true;
                    }
                    breakPoint(out);
                }

                
                private void breakPoint(final PrintStream out) {
                    if(shouldBreak) {
                        try {
                            out.flush();
                            err.flush();
                            System.in.read();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        shouldBreak=false;
                    }
                }
                
                @Override
                public void endOfLog() {
                    out.printf("Processed %d traces, %d faulty\n",traceidx,errCount);
                }
            });
        } catch (IOException e) {
            e.printStackTrace(err);
        }
    }

    static class ClassMethod{
        final String cName, mName;
        public ClassMethod(String className, String methodName) {
            this.cName = className;
            this.mName = methodName;
        }
    }


}
