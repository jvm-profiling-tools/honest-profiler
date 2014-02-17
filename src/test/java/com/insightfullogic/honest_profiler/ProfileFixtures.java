package com.insightfullogic.honest_profiler;

import com.insightfullogic.honest_profiler.log.Method;

public class ProfileFixtures {

    public static final Method println = new Method(5, "PrintStream.java", "Ljava/io/PrintStream;", "println");

    public static final Method append = new Method(6, "PrintStream.java", "Ljava/io/PrintStream;", "append");

    public static final Method printf = new Method(7, "PrintStream.java", "Ljava/io/PrintStream;", "printf");

}
