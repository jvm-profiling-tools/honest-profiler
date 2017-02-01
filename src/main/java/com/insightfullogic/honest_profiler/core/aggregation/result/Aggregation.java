package com.insightfullogic.honest_profiler.core.aggregation.result;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;

public class Aggregation<K, T extends Keyed<K>>
{
    private final AggregationProfile source;
    private final List<T> data;

    public Aggregation(AggregationProfile source, List<T> data)
    {
        super();
        this.source = source;
        this.data = data;
    }

    public AggregationProfile getSource()
    {
        return source;
    }

    public List<T> getData()
    {
        return data;
    }

    public Aggregation<K, T> filter(FilterSpecification<T> filterSpec)
    {
        return new Aggregation<>(
            source,
            data.stream().filter(filterSpec.getFilter()).collect(toList()));
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("Aggregation :\n");
        data.forEach(result::append);
        return result.toString();
    }
}
