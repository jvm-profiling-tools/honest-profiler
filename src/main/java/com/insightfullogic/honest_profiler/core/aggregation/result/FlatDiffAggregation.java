package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FlatDiffAggregation<K> extends AbstractDiffAggregation<K, AggregatedEntry<K>>
{
    private Map<K, AggregatedDiffEntry<K>> data;

    public FlatDiffAggregation()
    {
        data = new HashMap<>();
    }

    public void setBase(Aggregation<K, AggregatedEntry<K>> aggregation)
    {
        super.setBaseAggregation(aggregation);
        aggregation.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new AggregatedDiffEntry<>(entry, null) : v.setBase(entry));
        });
    }

    public void setNew(Aggregation<K, AggregatedEntry<K>> aggregation)
    {
        super.setNewAggregation(aggregation);
        aggregation.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new AggregatedDiffEntry<>(null, entry) : v.setNew(entry));
        });
    }

    public Collection<AggregatedDiffEntry<K>> getData()
    {
        return data.values();
    }
}
