package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;

import javafx.scene.control.TreeItem;

public class NodeTreeItem extends TreeItem<Node>
{
    public NodeTreeItem(Aggregation<Node> profile)
    {
        super(null);
        profile.getData().forEach(child -> getChildren().add(new NodeTreeItem(child)));
    }

    public NodeTreeItem(Node node)
    {
        super(node);
        node.getChildren().forEach(child -> getChildren().add(new NodeTreeItem(child)));
    }
}