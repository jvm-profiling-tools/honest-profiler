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

public final class FlatProfileEntry
{
    private final Frame method;

    private final int totalCount;
    private final int selfCount;
    private final int traceCount;

    public FlatProfileEntry(final Frame method,
                            final int totalCount,
                            final int selfCount,
                            final int traceCount)
    {
        this.method = method;

        this.totalCount = totalCount;
        this.selfCount = selfCount;
        this.traceCount = traceCount;
    }

    public Frame getFrameInfo()
    {
        return method;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public int getSelfCount()
    {
        return selfCount;
    }

    public int getTraceCount()
    {
        return traceCount;
    }

    public double getTotalTimeShare()
    {
        return totalCount / (double) traceCount;
    }

    public double getSelfTimeShare()
    {
        return selfCount / (double) traceCount;
    }

    public FlatProfileEntry copy()
    {
        return new FlatProfileEntry(
            method.copy(),
            totalCount,
            selfCount,
            traceCount);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        FlatProfileEntry that = (FlatProfileEntry) o;
        if (that.selfCount != this.selfCount)
        {
            return false;
        }
        if (that.totalCount != this.totalCount)
        {
            return false;
        }
        if (that.traceCount != this.traceCount)
        {
            return false;
        }

        return (this.method != null ? !this.method.equals(that.method) : that.method == null);
    }

    @Override
    public int hashCode()
    {
        int result;

        result = method != null ? method.hashCode() : 0;
        result = 31 * result + selfCount;
        result = 31 * result + totalCount;
        result = 31 * result + traceCount;

        return result;
    }
}
