package com.insightfullogic.honest_profiler.log;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class FakeEventListener implements IEventListener {

    private final List<LogEvent> events;
    private boolean isLogParsingComplete;

    public FakeEventListener() {
        events = new ArrayList<>();
        isLogParsingComplete = false;
    }

    public void hasSeenEvent(LogEvent event) {
        System.out.println(events);
        assertThat(events, hasItem(equalTo(event)));
    }

    public void seenEventCount(int expectedNumberOfEvents) {
        assertEquals(expectedNumberOfEvents, events.size());
    }

    @Override
    public void handle(TraceStart traceStart) {
        events.add(traceStart);
    }

    @Override
    public void handle(StackFrame stackFrame) {
        events.add(stackFrame);
    }

    @Override
    public void handle(Method newMethod) {
        events.add(newMethod);
    }

    @Override
    public void endOfLog() {
        isLogParsingComplete = true;
    }

    public boolean isLogParsingComplete() {
        return isLogParsingComplete;
    }

}
