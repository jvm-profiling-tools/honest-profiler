package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.List;

/**
 * Interface for data structure classes which have children ot type T.
 * <p>
 * @param <T> the type of the children
 */
public interface Parent<T>
{
    /**
     * Returns the list of children.
     * <p>
     * @return the list of children
     */
    List<T> getChildren();
}
