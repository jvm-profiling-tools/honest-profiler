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
package com.insightfullogic.honest_profiler.core;

import com.insightfullogic.honest_profiler.core.parser.*;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JunitSuiteRunner.class)
public class ConductorTest
{
    {

        describe("Parsing and Consuming a log", it -> {
            final String PRINTSTREAM = "Ljava/io/PrintStream;";
            final String PRINT_STREAM_JAVA = "PrintStream.java";

            it.should("parse a basic log", expect -> {
                LogEventListener listener = mock(LogEventListener.class);
                Logger logger = mock(Logger.class);
                LogParser parser = new LogParser(logger, listener);
                Conductor consumer = new Conductor(logger, Util.log0Source(), parser, false);

                expect.that(consumer.poll()).is(true);
                verify(listener).handle(new TraceStart(2, 5, 0, 0));

                expect.that(consumer.poll()).is(true);
                verify(listener).handle(new StackFrame(52, 1));

                expect.that(consumer.poll()).is(true);
                verify(listener).handle(new Method(1, PRINT_STREAM_JAVA, PRINTSTREAM, "printf"));

                expect.that(consumer.poll()).is(true);
                verify(listener).handle(new StackFrame(42, 2));

                expect.that(consumer.poll()).is(true);
                verify(listener).handle(new Method(2, PRINT_STREAM_JAVA, PRINTSTREAM, "append"));

                // Next record
                expect.that(consumer.poll()).is(true);
                expect.that(consumer.poll()).is(true);
                expect.that(consumer.poll()).is(true);
                expect.that(consumer.poll()).is(false);

                verify(listener).endOfLog();
            });
        });

    }}
