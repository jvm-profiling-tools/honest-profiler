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
package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.profiles.Profile;

final class ThreadSampleFilter implements Filter
{

    private static final double DEFAULT_MIN_PROPORTION_OF_SAMPLES = 0.01;

    private final double minProportionOfSamples;

    public ThreadSampleFilter()
    {
        this(DEFAULT_MIN_PROPORTION_OF_SAMPLES);
    }

    public ThreadSampleFilter(double minProportionOfSamples)
    {
        this.minProportionOfSamples = minProportionOfSamples;
    }

    @Override
    public void filter(Profile profile)
    {
        final double minimumNumberOfSamples = getMinimumNumberOfSamples(profile);
        profile.getTrees()
            .removeIf(tree -> tree.getNumberOfSamples() < minimumNumberOfSamples);
    }

    private double getMinimumNumberOfSamples(Profile profile)
    {
        return profile.getTraceCount() * minProportionOfSamples;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadSampleFilter that = (ThreadSampleFilter) o;

        if (Double.compare(that.minProportionOfSamples, minProportionOfSamples) != 0) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        long temp = Double.doubleToLongBits(minProportionOfSamples);
        return (int) (temp ^ (temp >>> 32));
    }

}
