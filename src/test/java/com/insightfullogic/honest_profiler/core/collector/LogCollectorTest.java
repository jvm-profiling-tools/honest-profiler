package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class LogCollectorTest {
    private static final int LINE = 123;

    @Test
    public void logCollectorShouldCopeWithUnexpectedFrames() {
        final Deque<Profile> found = new ArrayDeque<>();
        final LogCollector collector = new LogCollector(found::add, true);

        for (int i = 0; i < 10; i++) {
            collector.handle(new Method(i, "a", "Bass", "c" + i));
        }

        assertTrue("methods don't cause profiles", found.isEmpty());

        int threadId = 0;
        collector.handle(new TraceStart(2, ++threadId));

        assertTrue("nothing to profile still", found.isEmpty());

        collector.handle(new StackFrame(LINE, 0));
        collector.handle(new StackFrame(LINE, 1));
        // ..and one unexpected frame
        collector.handle(new StackFrame(LINE, 2));

        // normal method afterwards
        collector.handle(new TraceStart(2, ++threadId));
        collector.handle(new StackFrame(LINE, 6));
        collector.handle(new StackFrame(LINE, 7));

        // and continuation
        collector.handle(new TraceStart(20, ++threadId));

        assertArrayEquals(new long[] { 2, 7 }, idOfLastMethodInEachThread(found.getLast()));
    }

    private long[] idOfLastMethodInEachThread(Profile profile) {
        return profile.getTrees().stream().mapToLong(x -> x.getRootNode().getMethod().getMethodId()).toArray();
    }
}
