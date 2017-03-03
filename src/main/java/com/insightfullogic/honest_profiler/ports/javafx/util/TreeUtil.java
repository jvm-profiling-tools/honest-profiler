package com.insightfullogic.honest_profiler.ports.javafx.util;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Utility class for working with {@link TreeView}s, {@link TreeItem}s and related {@link Object}s.
 */
public final class TreeUtil
{
    /**
     * Expands the specified {@link TreeItem} and all of its descendants.
     * <p>
     * @param treeItem the {@link TreeItem} to be expanded
     */
    public static void expandFully(TreeItem<?> treeItem)
    {
        treeItem.setExpanded(true);
        treeItem.getChildren().forEach(item -> expandFully(item));
    }

    /**
     * Expands the specified {@link TreeItem}, then recursively descends into the first child, expanding the first
     * grandchild and collapsing the other grandchildren, if any.
     * <p>
     * @param <T> the type of the {@link Object} contained in the {@link TreeItem}
     * @param treeItem the {@link TreeItem} to be expanded
     */
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

    /**
     * Expands the specified {@link TreeItem} and any descendants up to the specified depth. If the depth is 0 or
     * negative, nothing happens.
     * <p>
     * @param treeItem the {@link TreeItem} to be partially expanded
     * @param depth the depthe of the expansion
     */
    public static void expandPartial(TreeItem<?> treeItem, int depth)
    {
        if (depth > 0)
        {
            treeItem.setExpanded(true);
            treeItem.getChildren().forEach(item -> expandPartial(item, depth - 1));
        }
    }

    /**
     * Collapsed the specified {@link TreeItem} and all of its descendants.
     * <p>
     * @param treeItem the {@link TreeItem} to be collapsed
     */
    public static void collapseFully(TreeItem<?> treeItem)
    {
        treeItem.setExpanded(false);
        treeItem.getChildren().forEach(item -> collapseFully(item));
    }

    // Instance Constructors

    /**
     * Private Constructor for utility class.
     */
    private TreeUtil()
    {
        // Private Constructor for utility class
    }
}
