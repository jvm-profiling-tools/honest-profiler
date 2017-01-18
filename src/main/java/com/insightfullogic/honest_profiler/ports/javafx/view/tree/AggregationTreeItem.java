package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;

import javafx.scene.control.TreeItem;

public class AggregationTreeItem extends TreeItem<AggregatedNode<String>>
{
    public AggregationTreeItem(Aggregation<String, AggregatedNode<String>> profile)
    {
        super(null);
        for (AggregatedNode<String> child : profile.getData())
        {
            getChildren().add(new AggregationTreeItem(child));
        }
    }

    public AggregationTreeItem(AggregatedNode<String> node)
    {
        super(node);
        for (AggregatedNode<String> child : node.getChildren())
        {
            getChildren().add(new AggregationTreeItem(child));
        }
    }
}