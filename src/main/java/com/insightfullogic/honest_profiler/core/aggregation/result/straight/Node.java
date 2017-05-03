package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.lang.Math.max;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Wrapper for {@link Entry} which allows organizing them into a tree structure.
 */
public class Node extends Entry
{
    // Instance Properties

    private Map<String, Node> children;

    // Instance Constructors

    /**
     * Create an empty Node for the specified {@link Aggregation}.
     * <p>
     * @param <T> the type of the data items contained in the {@link Aggregation}
     * @param aggregation the {@link Aggregation} the created Node belongs to
     */
    public <T extends Keyed<String>> Node(Aggregation<T> aggregation)
    {
        super(aggregation);
        children = new HashMap<>();
    }

    /**
     * Create a Node based on an {@link Entry}. The information of the {@link Entry} is copied into this Node.
     * <p>
     * @param entry the {@link Entry} the Node is based on
     */
    public Node(Entry entry)
    {
        this(entry.getAggregation());
        entry.copyInto(this);
    }

    /**
     * Copy Constructor.
     * <p>
     * @param node the Node being copied
     * @param children the (new) children of the Node
     */
    private Node(Node node, List<Node> children)
    {
        this(node.getAggregation());
        node.copyInto(this);
        children.forEach(child -> this.children.put(child.getKey(), child));
    }

    /**
     * Returns the children of the Node.
     * <p>
     * @return the children of the Node
     */
    public List<Node> getChildren()
    {
        return new ArrayList<>(children.values());
    }

    /**
     * Calculate the depth of the (sub)tree with this Node as root. Returns 0 if there are no children.
     * <p>
     * @return the depth of the (sub)tree with this Node as root, whereby an empty tree has depth 0
     */
    public int getDescendantDepth()
    {
        if (children.isEmpty())
        {
            return 0;
        }

        int depth = 0;
        for (Node child : children.values())
        {
            depth = max(depth, child.getDescendantDepth() + 1);
        }
        return depth;
    }

    /**
     * Adggregates a Node into the children of this Node.
     * <p>
     * @param child the Node to be aggregated as child
     * @return the Node resulting from the aggregation
     */
    public Node addChild(Node child)
    {
        return children.compute(child.getKey(), (k, v) -> v == null ? child : v.combine(child));
    }

    /**
     * Aggregates a {@link LeanNode} into the children of this Node, using the specified {@link CombinedGrouping} to
     * determine the aggregation key, and recursively aggregating the {@link LeanNode} descendants as well if specified.
     * <p>
     * @param child the {@link LeanNode} to be aggregated into the children of this Node
     * @param grouping the {@link CombinedGrouping} used for determining the aggregation key
     * @param recurse a boolean specifying whether the {@link LeanNode} descendants should be aggregated recursively
     */
    public void addChild(LeanNode child, CombinedGrouping grouping, boolean recurse)
    {
        // Construct intermediate Node
        Node childNode = new Node(getAggregation());
        childNode.add(child);
        childNode.setKey(grouping.apply(getAggregation().getSource(), child));

        // Aggregate it into existing children
        Node newChild = addChild(childNode);

        if (recurse)
        {
            child.getChildren()
                .forEach(grandChild -> newChild.addChild(grandChild, grouping, true));
        }
    }

    /**
     * Combines another Node into this one. The descendants will also be combined recursively.
     * <p>
     * @param other he Node to be combined into this Node
     * @return this Node
     */
    public Node combine(Node other)
    {
        super.combine(other);
        other.children.values().forEach(
            child -> children
                .compute(child.getKey(), (k, v) -> v == null ? child.copy() : v.combine(child)));
        return this;
    }

    /**
     * Returns a copy of this Node.
     * <p>
     * @return a copy of this Node
     */
    public Node copy()
    {
        List<Node> newChildren = children.values().stream().map(child -> child.copy())
            .filter(child -> child != null).collect(toList());
        return new Node(this, newChildren);
    }

    /**
     * Returns a copy of this Node applying a filter to itself and any descendants. The method returns null if no
     * children are accepted by the filter and the node itself isn't accepted either.
     * <p>
     * @param filter a {@link Predicate} for accepting Nodes
     * @return the filtered Node or null if the Node and none of its descendants are accepted
     */
    public Node copyWithFilter(Predicate<Node> filter)
    {
        List<Node> newChildren = children.values().stream()
            .map(child -> child.copyWithFilter(filter)).filter(child -> child != null)
            .collect(toList());
        return newChildren.size() > 0 || filter.test(this) ? new Node(this, newChildren) : null;
    }

    /**
     * Return a {@link Stream} of Nodes consisting of this Node and all its descendants.
     * <p>
     * @return a {@link Stream} of Nodes consisting of this Node and all its descendants
     */
    public Stream<Node> flatten()
    {
        return concat(of(this), children.values().stream().flatMap(Node::flatten));
    }

    /**
     * Return a {@link Stream} of Nodes consisting of all the descendants of this Node. This Node is not included.
     * <p>
     * @return a {@link Stream} of Nodes consisting of all the descendants of this Node
     */
    public Stream<Node> flattenDescendants()
    {
        return children.values().stream().flatMap(Node::flatten);
    }

    @Override
    public String toString()
    {
        return toString(0);
    }

    public String toString(int level)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < level; i++)
        {
            result.append("  ");
        }
        result.append(super.toString());
        getChildren()
            .forEach((Node child) -> result.append("\n").append(child.toString(level + 1)));
        return result.toString();
    }
}
