package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.ProfileFixtures;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlatProfileTest {

    private final FakeProfileListener listener = new FakeProfileListener();
    private final LogCollector collector = new LogCollector(listener, false);

    @Test
    public void looksUpMethodNames() {
        collector.handle(new TraceStart(1, 1));
        collector.handle(new StackFrame(20, 5));
        collector.handle(ProfileFixtures.println);
        collector.endOfLog();

        Profile profile = listener.getProfile();
        assertEquals(1, profile.getTraceCount());
        assertEquals(1L, profile.flatProfile().count());

        assertEntry(ProfileFixtures.println, 1.0, profile.flatProfile().findFirst());
    }

    private void assertEntry(Method method, double ratio, Optional<FlatProfileEntry> mbEntry) {
        assertTrue(mbEntry.isPresent());
        FlatProfileEntry entry = mbEntry.get();
        assertEquals(ratio, entry.getTimeShare(), 0.00001);
        assertEquals(method, entry.getMethod());
    }

    @Test
    public void calculateMajorityFlatProfiles() {
        TraceStart startTrace = new TraceStart(1, 1);
        collector.handle(startTrace);
        collector.handle(new StackFrame(20, 5));
        collector.handle(ProfileFixtures.println);
        collector.handle(startTrace);
        collector.handle(new StackFrame(20, 5));
        collector.handle(startTrace);
        collector.handle(new StackFrame(25, 6));
        collector.handle(ProfileFixtures.append);
        collector.endOfLog();

        Profile profile = listener.getProfile();
        assertEquals(3, profile.getTraceCount());
        assertEquals(2L, profile.flatProfile().count());

        assertEntry(ProfileFixtures.println, 2.0 / 3, profile.flatProfile()
                                             .findFirst());

        assertEntry(ProfileFixtures.append, 1.0 / 3, profile.flatProfile()
                .filter(e -> e.getTimeShare() < 0.5)
                .findFirst());
    }

}
