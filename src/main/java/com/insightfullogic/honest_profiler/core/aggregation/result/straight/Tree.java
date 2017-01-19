package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import static java.util.stream.Collectors.toList;

import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

public class Tree<K> extends Aggregation<K, Node<K>>
{
    public Tree(List<Node<K>> data, LeanNode reference)
    {
        super(data, reference);
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
            getData().stream().map(node -> node.copyWithFilter(filterSpec.getFilter()))
                .filter(node -> node != null).collect(toList()),
            getReference());
    }
}
