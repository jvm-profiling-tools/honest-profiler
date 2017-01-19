package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import com.insightfullogic.honest_profiler.core.aggregation.result.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;

import javafx.scene.control.TreeItem;

public class AggregationTreeItem extends TreeItem<Node<String>>
{
    public AggregationTreeItem(Aggregation<String, Node<String>> profile)
    {
        super(null);
        for (Node<String> child : profile.getData())
        {
            getChildren().add(new AggregationTreeItem(child));
        }
    }

    public AggregationTreeItem(Node<String> node)
    {
        super(node);
        for (Node<String> child : node.getChildren())
        {
            getChildren().add(new AggregationTreeItem(child));
        }
    }
}