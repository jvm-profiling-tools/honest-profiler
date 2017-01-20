package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;

/**
 * Provides the difference between two {@link Entry}s.
 */
public class DiffNode<K> extends DiffEntry<K>
{
    private final Map<K, DiffNode<K>> children;

    public DiffNode(Node<K> baseNode, Node<K> newNode)
    {
        super(baseNode, newNode);

        this.children = new HashMap<>();
        addBaseChildren(baseNode);
        addNewChildren(newNode);
    }

    private DiffNode(DiffEntry<K> entry, List<DiffNode<K>> children)
    {
        super(entry.getBaseEntry(), entry.getNewEntry());

        this.children = new HashMap<>();
        children.forEach(child -> this.children.put(child.getKey(), child));
    }

    public DiffNode<K> setBase(Node<K> node)
    {
        super.setBase(node);

        addBaseChildren(node);
        return this;
    }

    public DiffNode<K> setNew(Node<K> node)
    {
        super.setNew(node);

        addNewChildren(node);
        return this;
    }

    public Collection<DiffNode<K>> getChildren()
    {
        return children.values();
    }

    private void addBaseChildren(Node<K> node)
    {
        if (node == null)
        {
            return;
        }
        node.getChildren().forEach(this::addBaseChild);
    }

    private void addNewChildren(Node<K> node)
    {
        if (node == null)
        {
            return;
        }
        node.getChildren().forEach(this::addNewChild);
    }

    private void addBaseChild(Node<K> child)
    {
        children.compute(
            child.getKey(),
            (k, v) -> v == null ? new DiffNode<>(child, null) : v.setBase(child));
    }

    private void addNewChild(Node<K> child)
    {
        children.compute(
            child.getKey(),
            (k, v) -> v == null ? new DiffNode<>(null, child) : v.setNew(child));
    }

    public DiffNode<K> copyWithFilter(Predicate<DiffNode<K>> filter)
    {
        List<DiffNode<K>> newChildren = children.values().stream()
            .map(child -> child.copyWithFilter(filter)).filter(child -> child != null)
            .collect(toList());
        return newChildren.size() > 0 || filter.test(this) ? new DiffNode<>(this, newChildren)
            : null;
    }
}
