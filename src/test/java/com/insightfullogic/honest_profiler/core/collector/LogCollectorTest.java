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
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class LogCollectorTest
{
    private static final int LINE = 123;

    @Test
    public void logCollectorShouldCopeWithUnexpectedFrames()
    {
        final Deque<Profile> found = new ArrayDeque<>();
        final LogCollector collector = new LogCollector(found::add, true);

        for (int i = 0; i < 10; i++)
        {
            collector.handle(new Method(i, "a", "Bass", "c" + i));
        }

        assertTrue("methods don't cause profiles", found.isEmpty());

        int threadId = 0;
        collector.handle(new TraceStart(2, ++threadId, 1, 1));

        assertTrue("nothing to profile still", found.isEmpty());

        collector.handle(new StackFrame(LINE, 0));
        collector.handle(new StackFrame(LINE, 1));
        // ..and one unexpected frame
        collector.handle(new StackFrame(LINE, 2));

        // normal method afterwards
        collector.handle(new TraceStart(2, ++threadId, 1, 1));
        collector.handle(new StackFrame(LINE, 6));
        collector.handle(new StackFrame(LINE, 7));

        // and continuation
        collector.handle(new TraceStart(20, ++threadId, 1, 1));

        assertArrayEquals(new long[]{2, 7}, idOfLastMethodInEachThread(found.getLast()));
    }

    private long[] idOfLastMethodInEachThread(Profile profile)
    {
        return profile.getTrees().stream().mapToLong(x -> x.getRootNode().getFrameInfo().getMethodId()).toArray();
    }
}
