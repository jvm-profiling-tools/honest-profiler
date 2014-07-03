package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.core.model.parser.Logs;

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
