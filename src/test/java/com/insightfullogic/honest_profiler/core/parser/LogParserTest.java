package com.insightfullogic.honest_profiler.core.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LogParserTest {

    public static final String PRINTSTREAM = "Ljava/io/PrintStream;";
    public static final String PRINT_STREAM_JAVA = "PrintStream.java";
    private FakeEventListener events = new FakeEventListener();
    private LogParser parser = new LogParser(events);

    @Before
    public void parseLog1() {
        parser.parse(Logs.log0());
    }

    @After
    public void checkLogComplete() {
        assertTrue("finished log parsing", events.isLogParsingComplete());
    }

    @Test
    public void canParseTraceStart() {
        events.hasSeenEvent(new TraceStart(2, 5));
    }

    @Test
    public void canParseStackFrame() {
        events.hasSeenEvent(new StackFrame(52, 1));
    }

    @Test
    public void canParseNames() {
        events.hasSeenEvent(new Method(1, PRINT_STREAM_JAVA, PRINTSTREAM, "printf"));
        events.hasSeenEvent(new Method(2, PRINT_STREAM_JAVA, PRINTSTREAM, "append"));
    }

    @Test
    public void readsAllEvents() {
        events.seenEventCount(8);
    }


}
