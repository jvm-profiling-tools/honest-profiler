package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Base class for Diffs, which are aggregations (but not {@link Aggregation}s !) comparing two {@link Aggregation}s
 * against one another.
 *
 * One {@link Aggregation} is referred to as the "Base" (short for Baseline) and one as the "New" {@link Aggregation}.
 * The idea is that if two profiles are being compared against each other, they are probably two versions of the same
 * codebase. The "New" {@link Aggregation} is supposed to be the "newer" version, and in the UI, the differences are
 * marked as "good" and "bad" according to these semantics.
 *
 * E.g. less time spent in the "New" {@link Aggregation} is supposed to be an improvement and will be marked as such,
 * more time spent is supposed to be bad.
 *
 * @param <K> the type of the key in the aggregation items
 * @param <T> the type of aggregation items in the diffed {@link Aggregation}s
 * @param <U> the type of aggregation items inside this Diff
 */
public abstract class AbstractDiff<K, T extends Keyed<K>, U extends Keyed<K>>
{
    // Instance Properties

    private Aggregation<K, T> baseAggregation;
    private Aggregation<K, T> newAggregation;

    // Instance Accessors

    /**
     * Sets the Base {@link Aggregation}.
     *
     * @param baseAggregation the Base Aggregation
     */
    protected void setBaseAggregation(Aggregation<K, T> baseAggregation)
    {
        this.baseAggregation = baseAggregation;
    }

    /**
     * Sets the New {@link Aggregation}.
     *
     * @param newAggregation the New Aggregation
     */
    protected void setNewAggregation(Aggregation<K, T> newAggregation)
    {
        this.newAggregation = newAggregation;
    }

    /**
     * @see Aggregation#getReference()
     */
    public LeanNode getBaseReference()
    {
        return baseAggregation.getReference();
    }

    /**
     * @see Aggregation#getReferenceData()
     */
    public NumericInfo getBaseReferenceData()
    {
        return baseAggregation.getReferenceData();
    }

    /**
     * @see Aggregation#setReference()
     */
    public void setBaseReference(LeanNode reference)
    {
        baseAggregation.setReference(reference);
    }

    /**
     * @see Aggregation#getReference()
     */
    public LeanNode getNewReference()
    {
        return newAggregation.getReference();
    }

    /**
     * @see Aggregation#getReferenceData()
     */
    public NumericInfo getNewReferenceData()
    {
        return newAggregation.getReferenceData();
    }

    /**
     * @see Aggregation#setReference()
     */
    public void setNewReference(LeanNode reference)
    {
        newAggregation.setReference(reference);
    }

    /**
     * Filter this Diff and return the result as a new Diff.
     *
     * @param filterSpecification the {@link FilterSpecification} specifying the filter
     * @return a new Diff containing the filtered results
     */
    public abstract AbstractDiff<K, T, U> filter(FilterSpecification<U> filterSpecification);
}
