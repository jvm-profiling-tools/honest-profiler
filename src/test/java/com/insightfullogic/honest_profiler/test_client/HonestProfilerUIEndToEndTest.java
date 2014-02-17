package com.insightfullogic.honest_profiler.test_client;

import org.junit.Test;

public class HonestProfilerUIEndToEndTest {

    private final ApplicationRunner runner = new ApplicationRunner();

    @Test
    public void displayProfile() {
        runner.loadLogFrom("log0.hpl");
        runner.hasLoadedLog();
        runner.displayContainsMethodOrClass("PrintStream.printf");
        runner.displayContainsMethodOrClass("PrintStream.append");
    }

}
