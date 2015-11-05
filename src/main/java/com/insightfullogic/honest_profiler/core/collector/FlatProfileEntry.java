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
    private final double totalTimeShare;
    private final double selfTimeShare;

    public FlatProfileEntry(
        final Frame method, final double totalTimeShare, final double selfTimeShare)
    {
        this.method = method;
        this.totalTimeShare = totalTimeShare;
        this.selfTimeShare = selfTimeShare;
    }

    public Frame getFrameInfo()
    {
        return method;
    }

    public double getTotalTimeShare()
    {
        return totalTimeShare;
    }

    public double getSelfTimeShare()
    {
        return selfTimeShare;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlatProfileEntry that = (FlatProfileEntry) o;

        if (Double.compare(that.selfTimeShare, selfTimeShare) != 0) return false;
        if (Double.compare(that.totalTimeShare, totalTimeShare) != 0) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = method != null ? method.hashCode() : 0;
        temp = Double.doubleToLongBits(totalTimeShare);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(selfTimeShare);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
