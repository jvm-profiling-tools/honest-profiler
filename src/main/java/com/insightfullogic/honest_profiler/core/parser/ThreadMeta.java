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

public final class ThreadMeta implements LogEvent
{
    private final long threadId;
    private final String threadName;

    public ThreadMeta(long threadId, String name)
    {
        this.threadId = threadId;
        this.threadName = name;
    }

    public long getThreadId()
    {
        return threadId;
    }

    public String getThreadName()
    {
        return threadName;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadMeta that = (ThreadMeta) o;
        return Objects.equals(threadName, that.threadName)
            && Objects.equals(threadId, that.threadId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(threadId, threadName);
    }

    @Override
    public void accept(LogEventListener listener)
    {
        listener.handle(this);
    }

    public ThreadMeta update(ThreadMeta newMeta)
    {
        if (newMeta.threadName != null && !newMeta.threadName.isEmpty())
        {
            return new ThreadMeta(newMeta.threadId, newMeta.threadName);
        }

        return this;
    }

    @Override
    public String toString()
    {
        return "ThreadMeta{" +
            "threadId=" + threadId +
            ", threadName=" + threadName +
            '}';
    }

    public ThreadMeta copy()
    {
        return new ThreadMeta(threadId, threadName);
    }
}
