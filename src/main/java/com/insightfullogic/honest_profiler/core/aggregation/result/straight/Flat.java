package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;

public class Flat<K> extends Aggregation<K, Entry<K>>
{
    public Flat(AggregationProfile source, List<Entry<K>> data)
    {
        super(source, data);
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
            getSource(),
            getData().stream().filter(filterSpec.getFilter()).collect(toList()));
    }
}
