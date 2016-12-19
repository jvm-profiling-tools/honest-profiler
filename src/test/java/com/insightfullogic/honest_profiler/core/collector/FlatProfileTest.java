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
package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.testing_utilities.ProfileFixtures;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlatProfileTest
{

    private final FakeProfileListener listener = new FakeProfileListener();
    private final LogCollector collector = new LogCollector(listener, false);

    @Test
    public void looksUpMethodNames()
    {
        collector.handle(new TraceStart(1, 1, 1, 1));
        collector.handle(new StackFrame(20, 5));
        collector.handle(ProfileFixtures.println);
        collector.endOfLog();

        Profile profile = listener.getProfile();
        assertEquals(1, profile.getTraceCount());
        assertEquals(1L, profile.flatByMethodProfile().count());

        assertEntry(ProfileFixtures.println, 1.0, profile.flatByMethodProfile().findFirst());
    }

    private void assertEntry(Method method, double ratio, Optional<FlatProfileEntry> mbEntry)
    {
        assertTrue(mbEntry.isPresent());
        FlatProfileEntry entry = mbEntry.get();
        assertEquals(ratio, entry.getTotalTimeShare(), 0.00001);
        assertEquals(ratio, entry.getSelfTimeShare(), 0.00001);
        assertEquals(method, entry.getFrameInfo());
    }

    @Test
    public void calculateMajorityFlatProfiles()
    {
        TraceStart startTrace = new TraceStart(1, 1, 1, 1);
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
        assertEquals(2L, profile.flatByMethodProfile().count());

        assertEntry(ProfileFixtures.println, 2.0 / 3, profile.flatByMethodProfile()
            .findFirst());

        assertEntry(ProfileFixtures.append, 1.0 / 3, profile.flatByMethodProfile()
            .filter(e -> e.getTotalTimeShare() < 0.5)
            .findFirst());
    }

}
