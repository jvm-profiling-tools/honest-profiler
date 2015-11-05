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
package com.insightfullogic.honest_profiler.core.parser;


import java.util.Objects;

public final class TraceStart implements LogEvent
{

    private final int numberOfFrames;
    private final long threadId;

    public TraceStart(int numberOfFrames, long threadId)
    {
        this.numberOfFrames = numberOfFrames;
        this.threadId = threadId;
    }

    public int getNumberOfFrames()
    {
        return numberOfFrames;
    }

    public long getThreadId()
    {
        return threadId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraceStart that = (TraceStart) o;
        return Objects.equals(numberOfFrames, that.numberOfFrames)
            && Objects.equals(threadId, that.threadId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(numberOfFrames, threadId);
    }

    @Override
    public void accept(LogEventListener listener)
    {
        listener.handle(this);
    }

    @Override
    public String toString()
    {
        return "TraceStart{" +
            "numberOfFrames=" + numberOfFrames +
            ", threadId=" + threadId +
            '}';
    }
}
