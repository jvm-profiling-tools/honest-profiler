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
    private Map<String, Node> children;

    public <T extends Keyed<String>> Node(Aggregation<T> aggregation)
    {
        super(aggregation);
        this.children = new HashMap<>();
    }

    public Node(Entry entry)
    {
        this(entry.getAggregation());
        entry.copyInto(this);
    }

    /**
     * Copy Constructor.
     *
     * @param entry
     * @param children
     */
    public Node(Node entry, List<Node> children)
    {
        this(entry.getAggregation());
        entry.copyInto(this);
        children.forEach(child -> this.children.put(child.getKey(), child));
    }

    public List<Node> getChildren()
    {
        return new ArrayList<>(children.values());
    }

    // Calculate deepest stack depth in descendants. Return 0 if there are no children.
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

    public void addAll(Map<String, Node> newChildren)
    {
        newChildren.values().forEach(
            newChild -> this.children
                .compute(newChild.getKey(), (k, v) -> v == null ? newChild : v.combine(newChild)));
    }

    public void add(String key, LeanNode node)
    {
        super.setKey(key);
        super.add(node);
    }

    public Node addChild(Node child)
    {
        return children.compute(child.getKey(), (k, v) -> v == null ? child : v.combine(child));
    }

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

    public Node combine(Node other)
    {
        super.combine(other);
        other.children.values().forEach(
            child -> children
                .compute(child.getKey(), (k, v) -> v == null ? child.copy() : v.combine(child)));
        return this;
    }

    public Node copy()
    {
        List<Node> newChildren = children.values().stream().map(child -> child.copy())
            .filter(child -> child != null).collect(toList());
        return new Node(this, newChildren);
    }

    public Node copyWithFilter(Predicate<Node> filter)
    {
        List<Node> newChildren = children.values().stream()
            .map(child -> child.copyWithFilter(filter)).filter(child -> child != null)
            .collect(toList());
        return newChildren.size() > 0 || filter.test(this) ? new Node(this, newChildren) : null;
    }

    public Stream<Node> flatten()
    {
        return concat(of(this), children.values().stream().flatMap(Node::flatten));
    }

    public Stream<Node> flattenDescendants()
    {
        return children.values().stream().flatMap(Node::flatten);
    }
}
