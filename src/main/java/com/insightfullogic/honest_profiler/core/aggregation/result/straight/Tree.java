package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;

public class Tree extends Aggregation<Node>
{
    public Tree(AggregationProfile source)
    {
        super(source, new ArrayList<>());
    }

    public Tree(AggregationProfile source, List<Node> data)
    {
        super(source, data);
    }

    @Override
    public List<Node> getData()
    {
        return super.getData();
    }

    @Override
    public Tree filter(FilterSpecification<Node> filterSpec)
    {
        return new Tree(
            getSource(),
            getData().stream().map(node -> node.copyWithFilter(filterSpec.getFilter()))
                .filter(node -> node != null).collect(toList()));
    }

    public Stream<Node> flatten()
    {
        return getData().stream().flatMap(Node::flatten);
    }
}
