package com.insightfullogic.honest_profiler.ports.javafx.util;

import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExportDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.MenuUtil.addMenuItem;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_TREE_COLLAPSE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_TREE_EXPANDFIRSTONLY;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_TREE_EXPANDFULLY;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_TREE_EXPORTSUBTREE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.collapseFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFirstOnly;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeStack;

import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;

public final class ContextMenuUtil
{
    public static <T, U> void bindContextMenuForTreeTableCell(ApplicationContext appCtx,
        TreeTableCell<T, ?> cell)
    {
        bindContextMenuForTreeTableCell(
            appCtx,
            cell.contextMenuProperty(),
            cell.tableRowProperty()
        );
    }

    public static <T> void bindContextMenuForTreeCell(ApplicationContext appCtx, TreeCell<T> cell)
    {
        bindContextMenu(
            appCtx,
            cell.contextMenuProperty(),
            cell.treeItemProperty());
    }

    private static <T> void bindContextMenuForTreeTableCell(ApplicationContext appCtx,
        ObjectProperty<ContextMenu> ctxMenuProperty,
        ReadOnlyObjectProperty<TreeTableRow<T>> treeRowProperty)
    {
        treeRowProperty.addListener((property, oldValue, newValue) ->
        {
            if (newValue == null)
            {
                ctxMenuProperty.unbind();
                return;
            }

            bindContextMenu(appCtx, ctxMenuProperty, newValue.treeItemProperty());
        });
    }

    private static <T> void bindContextMenu(ApplicationContext appCtx,
        ObjectProperty<ContextMenu> ctxMenuProperty,
        ReadOnlyObjectProperty<TreeItem<T>> treeItemProperty)
    {
        ctxMenuProperty.bind(new ObjectBinding<ContextMenu>()
        {
            {
                super.bind(treeItemProperty);
            }

            @Override
            protected ContextMenu computeValue()
            {
                if (treeItemProperty == null)
                {
                    return null;
                }

                TreeItem<?> treeItem = treeItemProperty.get();

                return (treeItem == null || treeItem.getChildren().size() == 0) ? null
                    : getContextMenu(appCtx, treeItem);
            }
        });
    }

    private static <T> ContextMenu getContextMenu(ApplicationContext appCtx, TreeItem<T> treeItem)
    {
        ContextMenu menu = new ContextMenu();

        addMenuItem(
            menu.getItems(),
            appCtx.textFor(CTXMENU_TREE_EXPANDFULLY),
            info -> expandFully(treeItem));

        addMenuItem(
            menu.getItems(),
            appCtx.textFor(CTXMENU_TREE_EXPANDFIRSTONLY),
            info -> expandFirstOnly(treeItem));

        addMenuItem(
            menu.getItems(),
            appCtx.textFor(CTXMENU_TREE_COLLAPSE),
            info -> collapseFully(treeItem));

        if (treeItem.getValue() instanceof ProfileNode)
        {
            addMenuItem(
                menu.getItems(),
                appCtx.textFor(CTXMENU_TREE_EXPORTSUBTREE),
                info -> showExportDialog(
                    appCtx,
                    menu.getScene().getWindow(),
                    "stack_profile.txt",
                    out -> writeStack(out, (ProfileNode) treeItem.getValue())
                ));
        }
        return menu;
    }

    private ContextMenuUtil()
    {
        // Private Constructor for utility class
    }
}
