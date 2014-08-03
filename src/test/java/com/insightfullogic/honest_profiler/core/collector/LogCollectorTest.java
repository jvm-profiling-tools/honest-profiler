package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LogCollectorTest {
    private static final int LINE = 123;

    @Test
    public void logCollectorShouldCopeWithUnexpectedFrames() {
        final List<Profile> found = new ArrayList<>();
        final LogCollector collector = new LogCollector(found::add, true);

        for (int i = 0; i < 10; i++) {
            collector.handle(new Method(i, "a", "Bass", "c" + i));
        }

        assertEquals("methods don't cause profiles", emptyList(), withoutEmpty(found));

        int threadId = 0;
        final int expectedFrames = 2;
        collector.handle(new TraceStart(expectedFrames, ++threadId));

        assertEquals("nothing to profile still", emptyList(), withoutEmpty(found));

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

        assertArrayEquals(new long[] { 2, 7 }, idOfLastMethodInEachThread(mostRecentProfile(found)));
    }

    private long[] idOfLastMethodInEachThread(Profile profile) {
        return profile.getTrees().stream().mapToLong((x) -> x.getRootNode().getMethod().getMethodId()).toArray();
    }

    private Profile mostRecentProfile(List<Profile> found) {
        return found.get(found.size() - 1);
    }

    /** TODO why do we need to remove empty profiles? */
    private List<Profile> withoutEmpty(List<Profile> found) {
        return found.stream().filter((x) -> x.getTrees().size() != 0).collect(Collectors.toList());
    }

}
