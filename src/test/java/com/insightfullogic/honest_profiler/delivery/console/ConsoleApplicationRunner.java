package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.core.Util;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;

public class ConsoleApplicationRunner {

    private final ConsoleEntry ui;
    private final FakeConsole console;
    private final Logger logger = mock(Logger.class);

    public ConsoleApplicationRunner() {
        console = new FakeConsole();
        ui = new ConsoleEntry(logger, console);
    }

    public void loadLogFrom(String file) {
        ui.loadLogFrom(Util.logFile(file));
    }

    public void displayContainsMethodOrClass(String method) {
        console.outputContains(method);
    }

}
