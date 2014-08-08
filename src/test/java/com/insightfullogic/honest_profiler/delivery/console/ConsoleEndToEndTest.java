package com.insightfullogic.honest_profiler.delivery.console;

import com.insightfullogic.honest_profiler.core.Util;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.mock;

@RunWith(JunitSuiteRunner.class)
public class ConsoleEndToEndTest {{

    describe("The console application", it -> {

        FakeConsole console = new FakeConsole();
        Logger logger = mock(Logger.class);
        ConsoleEntry ui = new ConsoleEntry(logger, console);

        it.should("display a profile which is loaded into it", expect -> {

            when:
            ui.setLogLocation(Util.logFile("log0.hpl"));
            ui.run();

            then:
            console.outputContains("PrintStream.printf");
            console.outputContains("1.00");

            console.outputContains("PrintStream.append");
            console.outputContains("1.00");
        });

    });

}}
