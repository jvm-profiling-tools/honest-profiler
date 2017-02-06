package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;

public class Flat extends Aggregation<Entry>
{
    public Flat(AggregationProfile source, CombinedGrouping grouping)
    {
        super(source, grouping, new ArrayList<>());
    }

    public Flat(AggregationProfile source, CombinedGrouping grouping, List<Entry> data)
    {
        super(source, grouping, data);
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
            getGrouping(),
            getData().stream().filter(filterSpec.getFilter()).collect(toList()));
    }
}
