package com.insightfullogic.honest_profiler.test_client;

import com.insightfullogic.honest_profiler.FakeConsole;
import com.insightfullogic.honest_profiler.HonestProfilerEntry;
import com.insightfullogic.honest_profiler.log.Logs;

import static org.junit.Assert.assertTrue;

public class ApplicationRunner {

    private final HonestProfilerEntry ui;
    private final FakeConsole console;

    public ApplicationRunner() {
        console = new FakeConsole();
        ui = new HonestProfilerEntry(console);
    }

    public void loadLogFrom(String file) {
        ui.loadLogFrom(Logs.logFile(file));
    }

    public void hasLoadedLog() {
        assertTrue("hasn't Loaded the log", ui.hasLoadedLog());
    }

    public void displayContainsMethodOrClass(String method) {
        console.outputContains(method);
    }

}
