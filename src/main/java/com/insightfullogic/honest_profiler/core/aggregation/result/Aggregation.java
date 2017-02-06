package com.insightfullogic.honest_profiler.core.aggregation.result;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;

public class Aggregation<T extends Keyed<String>>
{
    private final AggregationProfile source;
    private final CombinedGrouping grouping;
    private final List<T> data;

    public Aggregation(AggregationProfile source, CombinedGrouping grouping, List<T> data)
    {
        super();
        this.source = source;
        this.grouping = grouping;
        this.data = data;
    }

    public AggregationProfile getSource()
    {
        return source;
    }

    public CombinedGrouping getGrouping()
    {
        return grouping;
    }

    public List<T> getData()
    {
        return data;
    }

    public Aggregation<T> filter(FilterSpecification<T> filterSpec)
    {
        return new Aggregation<>(
            source,
            grouping,
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
