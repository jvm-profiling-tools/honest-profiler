package com.insightfullogic.honest_profiler.ports.javafx.util;

import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExportDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.MenuUtil.addMenuItem;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.collapseFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFirstOnly;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeStack;

import java.util.function.Supplier;

import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;

public final class ContextMenuUtil
{
    public static <T, U> void bindContextMenuForTreeCell(TreeTableCell<T, U> cell)
    {
        bindContextMenuForTreeCell(
            cell.contextMenuProperty(),
            cell.itemProperty(),
            () -> cell.getTreeTableRow() == null ? null
                : cell.getTreeTableRow().treeItemProperty().get());
    }

    public static <T> void bindContextMenuForTreeCell(TreeCell<T> cell)
    {
        bindContextMenuForTreeCell(
            cell.contextMenuProperty(),
            cell.itemProperty(),
            () -> cell.treeItemProperty().get() == null ? null : cell.treeItemProperty().get());
    }

    private static <T, U> void bindContextMenuForTreeCell(
        ObjectProperty<ContextMenu> ctxMenuProperty, ObjectProperty<U> itemProperty,
        Supplier<TreeItem<T>> supplier)
    {
        ctxMenuProperty.bind(new ObjectBinding<ContextMenu>()
        {
            {
                super.bind(itemProperty);
            }

            @Override
            protected ContextMenu computeValue()
            {
                if (itemProperty.get() == null)
                {
                    return null;
                }
                TreeItem<?> treeItem = supplier.get();
                if (treeItem == null || treeItem.getChildren().size() == 0)
                {
                    return null;
                }

                ContextMenu menu = new ContextMenu();

                addMenuItem(menu.getItems(), "Expand Fully", info -> expandFully(treeItem));
                addMenuItem(
                    menu.getItems(),
                    "Expand First Only",
                    info -> expandFirstOnly(treeItem));
                addMenuItem(menu.getItems(), "Collapse", info -> collapseFully(treeItem));

                if (treeItem.getValue() instanceof ProfileNode)
                {
                    addMenuItem(menu.getItems(), "Export Subtree To File", info -> showExportDialog(
                        menu.getScene().getWindow(),
                        "stack_profile.txt",
                        out -> writeStack(out, (ProfileNode) treeItem.getValue())
                    ));
                }
                return menu;
            }
        });
    }

    private ContextMenuUtil()
    {
        // Private Constructor for utility class
    }
}
