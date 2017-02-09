package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * A Tree is an {@link Aggregation} containing {@link Node}s arranged in a tree with multiple roots. {@link LeanNode}s
 * are aggregated by using the {@link CombinedGrouping} to extract a String key, and each {@link Node} in the Tree is
 * the aggregation of all {@link LeanNode}s with the same key.
 */
public class Tree extends Aggregation<Node>
{
    // Instance Constructors

    /**
     * Create an empty Tree for the specified {@link AggregationProfile} and {@link CombinedGrouping}.
     * <p>
     * @param source the {@link AggregationProfile} whose {@link LeanNode}s are aggregated into this Tree
     * @param grouping the {@link CombinedGrouping} used for aggregation
     */
    public Tree(AggregationProfile source, CombinedGrouping grouping)
    {
        super(source, grouping, new ArrayList<>());
    }

    /**
     * Internal constructor used by the {@link #filter(FilterSpecification)} method.
     * <p>
     * @param source the {@link AggregationProfile} whose {@link LeanNode}s are aggregated into this Flat
     * @param grouping the {@link CombinedGrouping} used for aggregation
     * @param data the list of root {@link Node}s in the Tree
     */
    private Tree(AggregationProfile source, CombinedGrouping grouping, List<Node> data)
    {
        super(source, grouping, data);
    }

    // Instance Accessors

    @Override
    public List<Node> getData()
    {
        return super.getData();
    }

    /**
     * Returns a {@link Stream} of all {@link Node}s contained in this Tree.
     * <p>
     * @return a {@link Stream} of all {@link Node}s contained in this Tree
     */
    public Stream<Node> flatten()
    {
        return getData().stream().flatMap(Node::flatten);
    }

    // Aggregation Implementation

    /**
     * @see Aggregation#filter(FilterSpecification) for the general description of this method.
     * @see Node#copyWithFilter(java.util.function.Predicate) for more details about the tree filtering semantics.
     */
    @Override
    public Tree filter(FilterSpecification<Node> filterSpec)
    {
        return new Tree(
            getSource(),
            getGrouping(),
            getData().stream().map(node -> node.copyWithFilter(filterSpec.getFilter()))
                .filter(node -> node != null).collect(toList()));
    }
}
