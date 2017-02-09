package com.insightfullogic.honest_profiler.core.aggregation.result;

/**
 * Interface for aggregation data structure classes which have an aggregation key.
 * <p>
 * @param <T> the type of the key
 */
public interface Keyed<T>
{
    /**
     * Returns the aggregation key which was used to aggregate data into this data structure.
     * <p>
     * @return the aggregation key which was used to aggregate data into this data structure
     */
    T getKey();
}
