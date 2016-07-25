/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.console;

import com.insightfullogic.honest_profiler.core.Util;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class ConsoleApplicationTest
{
    {

        describe("The console application", it -> {

            FakeConsole output = new FakeConsole();
            FakeConsole error = new FakeConsole();
            ConsoleApplication profiler = new ConsoleApplication(error, output);

            it.isSetupWith(() -> {
                output.eraseScreen();
                error.eraseScreen();
                profiler.setProfileFormat("all");
            });

            it.should("display a profile which is loaded into it", expect -> {

                when:
                profiler.setLogLocation(Util.log0());
                profiler.run();

                then:
                output.outputContains("PrintStream::printf");
                output.outputContains("100.0");

                output.outputContains("PrintStream::append");
                output.outputContains("100.0");

                output.outputContains("Printing Profile for:");
                output.outputContains("log0.hpl");
            });

            it.should("not display filtered out profile entries", expect -> {

                when:
                profiler.setLogLocation(Util.log0());
                profiler.setFilterDescription("class: foo;");
                profiler.run();

                then:
                output.outputDoesntContain("Flat Profile:\n  1.00 java.io.PrintStream.printf");

                output.outputContains("Printing Profile for:");
                output.outputContains("log0.hpl");
            });

            it.should("only display the tree profile when tree is selected", expect -> {

                when:
                profiler.setLogLocation(Util.log0());
                profiler.setProfileFormat("tree");
                profiler.run();

                then:
                output.outputDoesntContain("Flat Profile");
                output.outputContains("Tree Profile");
            });

            it.should("only display the flat profile when flat is selected", expect -> {

                when:
                profiler.setLogLocation(Util.log0());
                profiler.setProfileFormat("flat_by_method");
                profiler.run();

                then:
                output.outputDoesntContain("Tree Profile");
                output.outputContains("Flat Profile");
            });

            it.should("only display the flat line profile when flat is selected", expect -> {

                when:
                profiler.setLogLocation(Util.log0());
                profiler.setProfileFormat("flat_by_line");
                profiler.run();

                then:
                output.outputDoesntContain("Tree Profile");
                output.outputContains("Flat Profile");
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
