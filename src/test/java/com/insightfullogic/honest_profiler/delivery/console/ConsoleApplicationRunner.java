package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.core.conductor.Logs;

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

    public void displayContainsMethodOrClass(String method) {
        console.outputContains(method);
    }

}
