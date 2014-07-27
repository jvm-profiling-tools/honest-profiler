package com.insightfullogic.honest_profiler.core;

import com.insightfullogic.honest_profiler.core.parser.EventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.store.LogSaver;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.*;

@RunWith(JunitSuiteRunner.class)
public class LogConsumerTest {{

    describe("Parsing and Consuming a log", it -> {
        final String PRINTSTREAM = "Ljava/io/PrintStream;";
        final String PRINT_STREAM_JAVA = "PrintStream.java";

        it.should("parse a basic log", expect -> {
            EventListener listener = mock(EventListener.class);
            LogSaver saver = mock(LogSaver.class);
            LogConsumer consumer = new LogConsumer(Logs.log0(), new DataConsumer(null, saver, listener), false);

            expect.that(consumer.run()).is(true);
            verify(listener).handle(new TraceStart(2, 5));

            expect.that(consumer.run()).is(true);
            verify(listener).handle(new StackFrame(52, 1));

            expect.that(consumer.run()).is(true);
            verify(listener).handle(new Method(1, PRINT_STREAM_JAVA, PRINTSTREAM, "printf"));

            expect.that(consumer.run()).is(true);
            verify(listener).handle(new StackFrame(42, 2));

            expect.that(consumer.run()).is(true);
            verify(listener).handle(new Method(2, PRINT_STREAM_JAVA, PRINTSTREAM, "append"));

            // Next record
            expect.that(consumer.run()).is(true);
            expect.that(consumer.run()).is(true);
            expect.that(consumer.run()).is(true);
            expect.that(consumer.run()).is(false);

            verify(listener).endOfLog();
        });
    });

}}
