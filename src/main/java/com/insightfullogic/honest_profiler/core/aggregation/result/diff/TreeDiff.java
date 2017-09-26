package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

/**
 * Diff which wraps provides the difference between two tree {@link Aggregation}s (containing {@link Node}s) as a tree
 * of {@link DiffNode}s, which each wrap and provide the difference between corresponding {@link Node}s.
 */
public class TreeDiff extends AbstractDiff<Node, DiffNode, Tree>
{
    // Instance Properties

    private Map<String, DiffNode> data;

    // Instance Constructors

    /**
     * Empty constructor.
     */
    public TreeDiff()
    {
        data = new HashMap<>();
    }

    /**
     * Internal Copy constructor.
     * <p>
     * @param entries the {@link List} of {@link DiffNode}s to be copied into this Diff
     */
    private TreeDiff(List<DiffNode> entries)
    {
        data = new HashMap<>();
        entries.forEach(entry -> data.put(entry.getKey(), entry));
    }

    // Instance Accessors

    /**
     * Sets the Base and New {@link Tree}s, and calculates the diff contents.
     * <p>
     * @param baseTree the Base {@link Tree}
     * @param newTree the New {@link Tree}
     */
    public void set(Tree baseTree, Tree newTree)
    {
        super.setAggregations(baseTree, newTree);
        data.clear();

        baseTree.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new DiffNode(node, null) : v.setBase(node)));

        newTree.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new DiffNode(null, node) : v.setNew(node)));

    }

    /**
     * Returns a {@link Stream} of all {@link DiffNode}s contained in this Tree.
     * <p>
     * @return a {@link Stream} of all {@link DiffNode}s contained in this Tree
     */
    public Stream<DiffNode> flatten()
    {
        return getData().stream().flatMap(DiffNode::flatten);
    }

    /**
     * Returns the {@link DiffNode}s from this Diff.
     * <p>
     * @return a {@link List} containing the {@link DiffNode}s from this Diff
     */
    public List<DiffNode> getData()
    {
        return new ArrayList<>(data.values());
    }

    // AbstractDiff Implementation

    @Override
    public TreeDiff filter(FilterSpecification<DiffNode> filterSpec)
    {
        return new TreeDiff(
            getData().stream().map(node -> node.copyWithFilter(filterSpec.getFilter()))
                .filter(node -> node != null).collect(toList()));
    }
}
