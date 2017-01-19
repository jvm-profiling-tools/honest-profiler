package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.List;

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
}
