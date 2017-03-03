package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;

import javafx.scene.control.TreeItem;

public class DiffNodeTreeItem extends TreeItem<DiffNode>
{
    public DiffNodeTreeItem(TreeDiff profileDiff)
    {
        super(null);
        for (DiffNode child : profileDiff.getData())
        {
            getChildren().add(new DiffNodeTreeItem(child));
        }
    }

    public DiffNodeTreeItem(DiffNode diff)
    {
        super(diff);
        for (DiffNode child : diff.getChildren())
        {
            getChildren().add(new DiffNodeTreeItem(child));
        }
    }
}