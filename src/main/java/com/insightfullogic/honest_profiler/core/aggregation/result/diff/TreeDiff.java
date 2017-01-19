package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

public class TreeDiff<K> extends AbstractDiff<K, Node<K>, DiffNode<K>>
{
    private Map<K, DiffNode<K>> data;

    public TreeDiff()
    {
        data = new HashMap<>();
    }

    private TreeDiff(List<DiffNode<K>> entries)
    {
        data = new HashMap<>();
        entries.forEach(entry -> data.put(entry.getKey(), entry));
    }

    public void set(Tree<K> aggregation, boolean isBase)
    {
        if (isBase)
        {
            setBase(aggregation);
        }
        else
        {
            setNew(aggregation);
        }
    }

    public void setBase(Tree<K> aggregation)
    {
        super.setBaseAggregation(aggregation);
        aggregation.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new DiffNode<>(node, null) : v.setBase(node)));
    }

    public void setNew(Tree<K> aggregation)
    {
        super.setNewAggregation(aggregation);
        aggregation.getData().forEach(node -> data.compute(
            node.getKey(),
            (k, v) -> v == null ? new DiffNode<>(null, node) : v.setNew(node)));
    }

    public Collection<DiffNode<K>> getData()
    {
        return data.values();
    }

    @Override
    public TreeDiff<K> filter(FilterSpecification<DiffNode<K>> filterSpec)
    {
        return new TreeDiff<>(
            getData().stream().filter(filterSpec.getFilter()).collect(toList()));
    }
}
