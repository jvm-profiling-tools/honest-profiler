package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.collector.Profile;

final class ThreadSampleFilter implements Filter {

    private static final double DEFAULT_MIN_PROPORTION_OF_SAMPLES = 0.01;

    private final double minProportionOfSamples;

    public ThreadSampleFilter() {
        this(DEFAULT_MIN_PROPORTION_OF_SAMPLES);
    }

    public ThreadSampleFilter(double minProportionOfSamples) {
        this.minProportionOfSamples = minProportionOfSamples;
    }

    @Override
    public void filter(Profile profile) {
        final double minimumNumberOfSamples = getMinimumNumberOfSamples(profile);
        profile.getTrees()
               .removeIf(tree -> tree.getNumberOfSamples() < minimumNumberOfSamples);
    }

    private double getMinimumNumberOfSamples(Profile profile) {
        return profile.getTraceCount() * minProportionOfSamples;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadSampleFilter that = (ThreadSampleFilter) o;

        if (Double.compare(that.minProportionOfSamples, minProportionOfSamples) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(minProportionOfSamples);
        return (int) (temp ^ (temp >>> 32));
    }

}
