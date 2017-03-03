package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;

/**
 * Base class for Diffs, which are aggregations (but not {@link Aggregation}s !) comparing two {@link Aggregation}s
 * against one another.
 * <p>
 * One {@link Aggregation} is referred to as the "Base" (short for Baseline) and one as the "New" {@link Aggregation}.
 * The idea is that if two profiles are being compared against each other, they are probably two versions of the same
 * codebase. The "New" {@link Aggregation} is supposed to be the "newer" version, and in the UI, the differences are
 * marked as "good" and "bad" according to these semantics.
 * <p>
 * E.g. less time spent in the "New" {@link Aggregation} is supposed to be an improvement and will be marked as such,
 * more time spent is supposed to be bad.
 * <p>
 * @param <T> the type of aggregation items in the diffed {@link Aggregation}s
 * @param <U> the type of aggregation items inside this Diff
 */
public abstract class AbstractDiff<T extends Keyed<String>, U extends Keyed<String>, V extends Aggregation<T>>
{
    // Instance Properties

    private V baseAggregation;
    private V newAggregation;

    // Instance Accessors

    /**
     * Sets the {@link Aggregation}s which will be compared.
     * <p>
     * @param baseAggregation the Base Aggregation
     * @param newAggregation the New Aggregation
     */
    protected void setAggregations(V baseAggregation, V newAggregation)
    {
        this.baseAggregation = baseAggregation;
        this.newAggregation = newAggregation;
    }

    /**
     * Returns the Base {@link Aggregation}.
     * <p>
     * @return the Base {@link Aggregation}.
     */
    public V getBaseAggregation()
    {
        return baseAggregation;
    }

    /**
     * Returns the New {@link Aggregation}.
     * <p>
     * @return the New {@link Aggregation}.
     */
    public V getNewAggregation()
    {
        return newAggregation;
    }

    /**
     * Filter this Diff and return the result as a new Diff.
     * <p>
     * @param filterSpecification the {@link FilterSpecification} specifying the filter
     * @return a new Diff containing the filtered results
     */
    public abstract AbstractDiff<T, U, V> filter(FilterSpecification<U> filterSpecification);
}
