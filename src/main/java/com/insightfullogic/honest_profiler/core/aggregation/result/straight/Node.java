package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.lang.Math.max;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Wrapper for {@link Entry} which allows organizing them into a tree structure.
 */
public class Node<K> extends Entry<K>
{
    private List<Node<K>> children;

    public <T extends Keyed<K>> Node(Aggregation<K, T> aggregation)
    {
        super(aggregation);
        this.children = new ArrayList<>();
    }

    public <T extends Keyed<K>> Node(K key, NumericInfo data, Aggregation<K, T> aggregation)
    {
        super(key, data, aggregation);
        this.children = new ArrayList<>();
    }

    /**
     * Copy constructor.
     *
     * @param entry
     * @param children
     */
    private Node(Node<K> entry, List<Node<K>> children)
    {
        this(entry.getAggregation());
        entry.copyInto(this);
        this.children = children;
    }

    public List<Node<K>> getChildren()
    {
        return children;
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
        for (Node<K> child : children)
        {
            depth = max(depth, child.getDescendantDepth() + 1);
        }
        return depth;
    }

    public void add(K key, LeanNode node)
    {
        super.setKey(key);
        super.add(node);
    }

    public Node<K> combine(Node<K> other)
    {
        super.combine(other);
        children.addAll(other.children);
        return this;
    }

    public Node<K> copyWithFilter(Predicate<Node<K>> filter)
    {
        List<Node<K>> newChildren = children.stream().map(child -> child.copyWithFilter(filter))
            .filter(child -> child != null).collect(toList());
        return newChildren.size() > 0 || filter.test(this) ? new Node<>(this, newChildren) : null;
    }
}
