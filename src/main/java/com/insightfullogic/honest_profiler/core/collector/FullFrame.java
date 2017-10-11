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

/**
 * A full frame is a fully identified stack frame within a method.
 */
public class FullFrame implements Frame
{
    private final Method method;
    private final StackFrame frame;

    FullFrame(final Method method, final StackFrame frame)
    {
        if (method == null || frame == null)
        {
            throw new NullPointerException();
        }
        if (method.getMethodId() != frame.getMethodId())
        {
            throw new IllegalArgumentException();
        }
        this.method = method;
        this.frame = frame;
    }

    @Override
    public long getMethodId()
    {
        return method.getMethodId();
    }

    @Override
    public String getClassName()
    {
        return method.getClassName();
    }

    @Override
    public String getMethodName()
    {
        return method.getMethodName();
    }

    @Override
    public String getMethodSignature()
    {
        return method.getMethodSignature();
    }
    
    @Override
    public String getMethodReturnType()
    {
        return method.getMethodReturnType();
    }

    @Override
    public int getBci()
    {
        return frame.getBci();
    }

    @Override
    public int getLine()
    {
        return frame.getLineNumber();
    }

    @Override
    public int hashCode()
    {
        return frame.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof FullFrame && this.frame.equals(((FullFrame) obj).frame);
    }

    @Override
    public FullFrame copy()
    {
        return new FullFrame(method.copy(), frame.copy());
    }
}
