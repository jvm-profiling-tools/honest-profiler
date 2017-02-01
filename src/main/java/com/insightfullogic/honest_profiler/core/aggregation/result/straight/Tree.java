package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;

public class Tree<K> extends Aggregation<K, Node<K>>
{
    public Tree(AggregationProfile source, List<Node<K>> data)
    {
        super(source, data);
    }

    @Override
    public List<Node<K>> getData()
    {
        return super.getData();
    }

    @Override
    public Tree<K> filter(FilterSpecification<Node<K>> filterSpec)
    {
        return new Tree<>(
            getSource(),
            getData().stream().map(node -> node.copyWithFilter(filterSpec.getFilter()))
                .filter(node -> node != null).collect(toList()));
    }

    public Stream<Node<K>> flatten()
    {
        return getData().stream().flatMap(Node::flatten);
    }
}
