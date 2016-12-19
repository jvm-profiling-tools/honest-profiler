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

import java.util.ArrayList;
import java.util.List;

public class LogEventPublisher implements LogEventListener
{
    private final List<LogEventListener> listeners = new ArrayList<>();

    public LogEventPublisher publishTo(final LogEventListener listener)
    {
        listeners.add(listener);
        return this;
    }

    public void handle(final TraceStart traceStart)
    {
        for (LogEventListener listener : listeners)
        {
            listener.handle(traceStart);
        }
    }

    public void handle(final StackFrame stackFrame)
    {
        for (LogEventListener listener : listeners)
        {
            listener.handle(stackFrame);
        }
    }

    public void handle(final Method newMethod)
    {
        for (LogEventListener listener : listeners)
        {
            listener.handle(newMethod);
        }
    }

    public void handle(final ThreadMeta newThreadMeta)
    {
        for (LogEventListener listener : listeners)
        {
            listener.handle(newThreadMeta);
        }
    }

    public void endOfLog()
    {
        for (LogEventListener listener : listeners)
        {
            listener.endOfLog();
        }
    }
}
