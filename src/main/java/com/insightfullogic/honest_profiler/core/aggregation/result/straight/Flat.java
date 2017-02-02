package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;

public class Flat extends Aggregation<Entry>
{
    public Flat(AggregationProfile source)
    {
        super(source, new ArrayList<>());
    }

    public Flat(AggregationProfile source, List<Entry> data)
    {
        super(source, data);
    }

    @Override
    public List<Entry> getData()
    {
        return super.getData();
    }

    @Override
    public Flat filter(FilterSpecification<Entry> filterSpec)
    {
        return new Flat(
            getSource(),
            getData().stream().filter(filterSpec.getFilter()).collect(toList()));
    }
}
