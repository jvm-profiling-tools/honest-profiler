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

public final class StackFrame implements LogEvent
{
    public static final int ERR_NO_LINE_INFO = -100;
    public static final int ERR_NO_LINE_FOUND = -101;

    private final int bci;
    private final int lineNumber;
    private final long methodId;

    public StackFrame(int bci, long methodId)
    {
        this(bci, ERR_NO_LINE_INFO, methodId);
    }

    public StackFrame(int bci, int lineNumber, long methodId)
    {
        this.bci = bci;
        this.lineNumber = lineNumber;
        this.methodId = methodId;
    }

    public int getBci()
    {
        return bci;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public long getMethodId()
    {
        return methodId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StackFrame that = (StackFrame) o;
        return Objects.equals(bci, that.bci)
            && Objects.equals(lineNumber, that.lineNumber)
            && Objects.equals(methodId, that.methodId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(bci, lineNumber, methodId);
    }

    @Override
    public void accept(LogEventListener listener)
    {
        listener.handle(this);
    }

    @Override
    public String toString()
    {
        return "StackFrame{" +
            "bci=" + bci +
            ", lineNumber=" + lineNumber +
            ", methodId=" + methodId +
            '}';
    }

    public StackFrame copy()
    {
        return new StackFrame(bci, lineNumber, methodId);
    }
}
