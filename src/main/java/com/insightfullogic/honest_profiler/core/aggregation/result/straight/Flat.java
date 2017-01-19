package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

public class Flat<K> extends Aggregation<K, Entry<K>>
{
    public Flat(List<Entry<K>> data, LeanNode reference)
    {
        super(data, reference);
    }

    @Override
    public List<Entry<K>> getData()
    {
        return super.getData();
    }

    @Override
    public Flat<K> filter(FilterSpecification<Entry<K>> filterSpec)
    {
        return new Flat<>(
            getData().stream().filter(filterSpec.getFilter()).collect(toList()),
            getReference());
    }
}
