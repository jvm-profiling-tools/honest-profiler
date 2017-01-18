package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedDiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.TreeDiffAggregation;

import javafx.scene.control.TreeItem;

public class DiffAggregationTreeItem extends TreeItem<AggregatedDiffNode<String>>
{
    public DiffAggregationTreeItem(TreeDiffAggregation<String> profileDiff)
    {
        super(null);
        for (AggregatedDiffNode<String> child : profileDiff.getData())
        {
            getChildren().add(new DiffAggregationTreeItem(child));
        }
    }

    public DiffAggregationTreeItem(AggregatedDiffNode<String> diff)
    {
        super(diff);
        for (AggregatedDiffNode<String> child : diff.getChildren())
        {
            getChildren().add(new DiffAggregationTreeItem(child));
        }
    }
}