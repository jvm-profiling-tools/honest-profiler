package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import com.insightfullogic.honest_profiler.core.aggregation.result.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.TreeDiff;

import javafx.scene.control.TreeItem;

public class DiffAggregationTreeItem extends TreeItem<DiffNode<String>>
{
    public DiffAggregationTreeItem(TreeDiff<String> profileDiff)
    {
        super(null);
        for (DiffNode<String> child : profileDiff.getData())
        {
            getChildren().add(new DiffAggregationTreeItem(child));
        }
    }

    public DiffAggregationTreeItem(DiffNode<String> diff)
    {
        super(diff);
        for (DiffNode<String> child : diff.getChildren())
        {
            getChildren().add(new DiffAggregationTreeItem(child));
        }
    }
}