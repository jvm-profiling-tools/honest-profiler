package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FlatDiff<K> extends AbstractDiff<K, Entry<K>>
{
    private Map<K, DiffEntry<K>> data;

    public FlatDiff()
    {
        data = new HashMap<>();
    }

    public void setBase(Aggregation<K, Entry<K>> aggregation)
    {
        super.setBaseAggregation(aggregation);
        aggregation.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new DiffEntry<>(entry, null) : v.setBase(entry));
        });
    }

    public void setNew(Aggregation<K, Entry<K>> aggregation)
    {
        super.setNewAggregation(aggregation);
        aggregation.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new DiffEntry<>(null, entry) : v.setNew(entry));
        });
    }

    public Collection<DiffEntry<K>> getData()
    {
        return data.values();
    }
}
