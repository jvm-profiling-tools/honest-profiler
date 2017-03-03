package com.insightfullogic.honest_profiler.core.aggregation;

import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;

/**
 * {@link Entry}s contain some percentage values, which are calculated by dividing one of the other contained values by
 * a reference value. Several references can make sense in various circumstances. This enumeration lists the various
 * supported "reference modes" for {@link Entry}s.
 */
public enum ReferenceMode
{
    /**
     * Mode which specifies that the aggregated total values of the entire profile are used as reference.
     */
    GLOBAL("Global"),
    /**
     * Mode which specifies that for a given {@link Entry}, the aggregated total values of the containing thread are
     * used as reference. WARNING : This can obviously only be used in aggregations where the {@link Entry}s are
     * guaranteed to aggregate data from a single thread.
     */
    THREAD("Thread"),
    /**
     * Mode which is applicable only to {@link Node}s or {@link DiffNode}s, and which specified that the aggregated
     * total values of the parent {@link Node} or {@link DiffNode} is used as reference.
     */
    PARENT("Parent");

    // Instance Properties

    private String name;

    // Instance Constructors

    /**
     * Constructor specifying a display name for the ReferenceMode.
     * <p>
     * @param name the display name for the ReferenceMode
     */
    private ReferenceMode(String name)
    {
        this.name = name;
    }

    // Object Implementation

    @Override
    public String toString()
    {
        return this.name;
    }
}
