package com.insightfullogic.honest_profiler.console;

import com.insightfullogic.honest_profiler.FakeConsole;
import com.insightfullogic.honest_profiler.console.ConsoleEntry;
import com.insightfullogic.honest_profiler.log.Logs;

import static org.junit.Assert.assertTrue;

public class ConsoleApplicationRunner {

    private final ConsoleEntry ui;
    private final FakeConsole console;

    public ConsoleApplicationRunner() {
        console = new FakeConsole();
        ui = new ConsoleEntry(console);
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
