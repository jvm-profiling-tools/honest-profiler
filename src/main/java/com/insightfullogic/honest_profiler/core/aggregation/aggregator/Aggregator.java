package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Aggregates input of type I into a result of type R.
 *
 * @param <I>
 * @param <R>
 */
public interface Aggregator<I, R>
{
    Aggregation<R> aggregate(I input, LeanNode reference);
}
