package com.insightfullogic.honest_profiler.ports.javafx.util;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public final class TreeUtil
{
    public static void expandFully(TreeItem<?> treeItem)
    {
        treeItem.setExpanded(true);
        treeItem.getChildren().forEach(item -> expandFully(item));
    }

    public static <T> void expandFirstOnly(TreeItem<T> treeItem)
    {
        treeItem.setExpanded(true);

        ObservableList<TreeItem<T>> children = treeItem.getChildren();
        if (children != null && children.size() >= 1)
        {
            expandFirstOnly(children.get(0));
            for (int i = 1; i < children.size(); i++)
            {
                collapseFully(children.get(i));
            }
        }
    }

    public static void expandPartial(TreeItem<?> treeItem, int depth)
    {
        if (depth > 0)
        {
            treeItem.setExpanded(true);
            treeItem.getChildren().forEach(item -> expandPartial(item, depth - 1));
        }
    }

    public static void collapseFully(TreeItem<?> treeItem)
    {
        treeItem.setExpanded(false);
        treeItem.getChildren().forEach(item -> collapseFully(item));
    }

    private TreeUtil()
    {
        // Private Constructor for utility class
    }
}
