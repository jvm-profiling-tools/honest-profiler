package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TreeDiff<K> extends AbstractDiff<K, Node<K>>
{
    private Map<K, DiffNode<K>> data;

    public TreeDiff()
    {
        data = new HashMap<>();
    }

    public void addBase(Aggregation<K, Node<K>> aggregation)
    {
        super.setBaseAggregation(aggregation);
        aggregation.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new DiffNode<>(node, null) : v.setBase(node)));
    }

    public void addNew(Aggregation<K, Node<K>> aggregation)
    {
        super.setNewAggregation(aggregation);
        aggregation.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new DiffNode<>(null, node) : v.setNew(node)));
    }

    public Collection<DiffNode<K>> getData()
    {
        return data.values();
    }
}
