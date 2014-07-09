package com.insightfullogic.honest_profiler;

import com.insightfullogic.honest_profiler.model.parser.Method;

public class ProfileFixtures {

    public static final long printlnId = 5;
    public static final long appendId = 6;
    public static final long printfId = 7;

    public static final Method println = new Method(printlnId, "PrintStream.java", "Ljava/io/PrintStream;", "println");
    public static final Method append = new Method(appendId, "PrintStream.java", "Ljava/io/PrintStream;", "append");
    public static final Method printf = new Method(printfId, "PrintStream.java", "Ljava/io/PrintStream;", "printf");

}
