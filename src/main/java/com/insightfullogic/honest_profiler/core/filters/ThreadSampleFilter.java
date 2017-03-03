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

import static com.insightfullogic.honest_profiler.core.filters.Filter.Mode.GE;
import static java.lang.Double.doubleToLongBits;

import java.util.function.BiPredicate;

import com.insightfullogic.honest_profiler.core.profiles.Profile;

public final class ThreadSampleFilter implements Filter
{
    private static final double DEFAULT_MIN_PROPORTION_OF_SAMPLES = 0.01;

    private final double minProportionOfSamples;

    private Mode mode;
    private BiPredicate<Integer, Double> filterMethod;

    public ThreadSampleFilter()
    {
        this(GE, DEFAULT_MIN_PROPORTION_OF_SAMPLES);
    }

    public ThreadSampleFilter(double minProportionOfSamples)
    {
        this(GE, minProportionOfSamples);
    }

    public ThreadSampleFilter(Mode mode, double minProportionOfSamples)
    {
        this.mode = mode;
        this.minProportionOfSamples = minProportionOfSamples;
        generateFilterMethod();
    }

    private void generateFilterMethod()
    {
        switch (mode)
        {
            case GE:
                filterMethod = (nrSamples, minNrSamples) -> nrSamples < minNrSamples;
                break;
            case GT:
                filterMethod = (nrSamples, minNrSamples) -> nrSamples <= minNrSamples;
                break;
            case LE:
                filterMethod = (nrSamples, minNrSamples) -> nrSamples > minNrSamples;
                break;
            case LT:
                filterMethod = (nrSamples, minNrSamples) -> nrSamples >= minNrSamples;
                break;
            case EQUALS:
                filterMethod = (nrSamples,
                    minNrSamples) -> Double.compare(nrSamples, minNrSamples) == 0;
                break;
            default:
                throw new RuntimeException(
                    "Filter Mode " + mode + " not supported in ThreadSampleFilter.");
        }
    }

    @Override
    public void filter(Profile profile)
    {
        final double minimumNumberOfSamples = getMinimumNumberOfSamples(profile);
        profile.getTrees().removeIf(tree -> filterMethod.test(tree.getNumberOfSamples(), minimumNumberOfSamples));
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

        return (mode == that.mode)
            && (Double.compare(that.minProportionOfSamples, minProportionOfSamples) == 0);
    }

    @Override
    public int hashCode()
    {
        long temp = doubleToLongBits(minProportionOfSamples);
        return (37 * mode.ordinal()) + (int) (temp ^ (temp >>> 32));
    }

}
