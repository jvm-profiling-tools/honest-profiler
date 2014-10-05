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

        FakeConsole output = new FakeConsole();
        FakeConsole error = new FakeConsole();
        ConsoleEntry profiler = new ConsoleEntry(error, output);

        it.isSetupWith(output::clear);
        it.isSetupWith(error::clear);

        it.should("display a profile which is loaded into it", expect -> {

            when:
            profiler.setLogLocation(Util.log0());
            profiler.run();

            then:
            output.outputContains("PrintStream.printf");
            output.outputContains("1.00");

            output.outputContains("PrintStream.append");
            output.outputContains("1.00");

            output.outputContains("Printing Profile for:");
            output.outputContains("log0.hpl");
        });

        it.should("not display filtered out profile entries", expect -> {
            when:
            profiler.setLogLocation(Util.log0());
            profiler.setFilterDescription("class: foo;");
            profiler.run();

            then:
            output.outputDoesntContain("Flat Profile:\n\t1.00 java.io.PrintStream.printf");

            output.outputContains("Printing Profile for:");
            output.outputContains("log0.hpl");
        });

        it.should("display an error when the file doesn't exist", expect -> {
            when:
            profiler.setLogLocation("sadsadsa");
            profiler.run();

            then:
            output.outputDoesntContain("Number of stack traces");
            error.outputContains("Unable to find log file at: sadsadsa");
        });

    });

}}
