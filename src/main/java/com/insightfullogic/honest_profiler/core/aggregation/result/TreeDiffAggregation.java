package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TreeDiffAggregation<K> extends AbstractDiffAggregation<K, AggregatedNode<K>>
{
    private Map<K, AggregatedDiffNode<K>> data;

    public TreeDiffAggregation()
    {
        data = new HashMap<>();
    }

    public void addBase(Aggregation<K, AggregatedNode<K>> aggregation)
    {
        super.setBaseAggregation(aggregation);
        aggregation.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new AggregatedDiffNode<>(node, null) : v.setBase(node)));
    }

    public void addNew(Aggregation<K, AggregatedNode<K>> aggregation)
    {
        super.setNewAggregation(aggregation);
        aggregation.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new AggregatedDiffNode<>(null, node) : v.setNew(node)));
    }

    public Collection<AggregatedDiffNode<K>> getData()
    {
        return data.values();
    }
}
