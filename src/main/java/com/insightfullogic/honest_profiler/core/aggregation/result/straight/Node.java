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

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Wrapper for {@link Entry} which allows organizing them into a tree structure.
 */
public class Node<K> extends Entry<K>
{
    private Map<K, Node<K>> children;

    public <T extends Keyed<K>> Node(Aggregation<K, T> aggregation)
    {
        super(aggregation);
        this.children = new HashMap<>();
    }

    public <T extends Keyed<K>> Node(K key, NumericInfo data, Aggregation<K, T> aggregation)
    {
        super(key, data, aggregation);
        this.children = new HashMap<>();
    }

    /**
     * Constructor used for copying.
     *
     * @param entry
     * @param children
     */
    private Node(Node<K> entry, List<Node<K>> children)
    {
        this(entry.getAggregation());
        entry.copyInto(this);
        children.forEach(child -> this.children.put(child.getKey(), child));
    }

    public List<Node<K>> getChildren()
    {
        return new ArrayList<>(children.values());
    }

    @Override
    public int getRefCnt()
    {
        return getReference().getTotalCnt();
    }

    // Calculate deepest stack depth in descendants. Return 0 if there are no
    // children.
    public int getDescendantDepth()
    {
        if (children.isEmpty())
        {
            return 0;
        }

        int depth = 0;
        for (Node<K> child : children.values())
        {
            depth = max(depth, child.getDescendantDepth() + 1);
        }
        return depth;
    }

    public void addAll(Map<K, Node<K>> newChildren)
    {
        newChildren.values().forEach(
            newChild -> this.children
                .compute(newChild.getKey(), (k, v) -> v == null ? newChild : v.combine(newChild)));
    }

    public void add(K key, LeanNode node)
    {
        super.setKey(key);
        super.add(node);
    }

    public Node<K> combine(Node<K> other)
    {
        super.combine(other);
        other.children.values().forEach(
            child -> children
                .compute(child.getKey(), (k, v) -> v == null ? child.copy() : v.combine(child)));
        return this;
    }

    public Node<K> copy()
    {
        List<Node<K>> newChildren = children.values().stream().map(child -> child.copy())
            .filter(child -> child != null).collect(toList());
        return new Node<>(this, newChildren);
    }

    public Node<K> copyWithFilter(Predicate<Node<K>> filter)
    {
        List<Node<K>> newChildren = children.values().stream()
            .map(child -> child.copyWithFilter(filter)).filter(child -> child != null)
            .collect(toList());
        return newChildren.size() > 0 || filter.test(this) ? new Node<>(this, newChildren) : null;
    }

    public Stream<Node<K>> flatten()
    {
        return concat(of(this), children.values().stream().flatMap(Node::flatten));
    }
}
