package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;

public class FlatDiff<K> extends AbstractDiff<K, Entry<K>, DiffEntry<K>>
{
    private Map<K, DiffEntry<K>> data;

    public FlatDiff()
    {
        data = new HashMap<>();
    }

    private FlatDiff(List<DiffEntry<K>> entries)
    {
        data = new HashMap<>();
        entries.forEach(entry -> data.put(entry.getKey(), entry));
    }

    public void set(Flat<K> aggregation, boolean isBase)
    {
        if (isBase)
        {
            setBase(aggregation);
        }
        else
        {
            setNew(aggregation);
        }
    }

    public void setBase(Flat<K> aggregation)
    {
        super.setBaseAggregation(aggregation);
        aggregation.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new DiffEntry<>(entry, null) : v.setBase(entry));
        });
    }

    public void setNew(Flat<K> aggregation)
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

    @Override
    public FlatDiff<K> filter(FilterSpecification<DiffEntry<K>> filterSpec)
    {
        return new FlatDiff<>(
            getData().stream().filter(filterSpec.getFilter()).collect(toList()));
    }
}
