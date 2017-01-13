package com.insightfullogic.honest_profiler.core.aggregation;

import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Aggregates input of type I into a result of type R.
 *
 * @param <T>
 * @param <U>
 */
public interface Aggregator<I, R>
{
    R aggregate(I input, NumericInfo reference);
}
