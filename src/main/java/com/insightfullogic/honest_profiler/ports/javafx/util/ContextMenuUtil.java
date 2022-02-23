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

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

/**
 * Utility class for dynamically constructing and binding {@link ContextMenu}s.
 */
public final class ContextMenuUtil
{
    // Class Methods

    /**
     * Binds a dynamically generated {@link ContextMenu} to a {@link TreeTableCell}. The menu provides expand and
     * collapse options for a tree node.
     * <p>
     *
     * @param <T> the type of the items in the containing {@link TreeTableView}
     * @param <U> the type of the item contained in the {@link TreeTableCell}
     * @param appCtx the {@link ApplicationContext} of the application
     * @param cell the {@link TreeTableCell} for which the {@link ContextMenu} should be displayed when it is
     *            right-clicked
     */
    public static <T, U> void bindContextMenuForTreeTableCell(ApplicationContext appCtx,
        TreeTableCell<T, ?> cell)
    {
        bindContextMenuForTreeTableCell(
            appCtx,
            cell.contextMenuProperty(),
            cell.tableRowProperty()
        );
    }

    /**
     * Binds a dynamically generated {@link ContextMenu} to a {@link TreeCell}. The menu provides expand and collapse
     * options for a tree node.
     * <p>
     *
     * @param <T> the type of the item contained in the {@link TreeCell}
     * @param appCtx the {@link ApplicationContext} of the application
     * @param cell the {@link TreeCell} for which the {@link ContextMenu} should be displayed when it is right-clicked
     */
    public static <T> void bindContextMenuForTreeCell(ApplicationContext appCtx, TreeCell<T> cell)
    {
        bindContextMenu(
            appCtx,
            cell.contextMenuProperty(),
            cell.treeItemProperty());
    }

    /**
     * Helper method which provides extra logic for extracting the {@link TreeItem} {@link Property} from a
     * {@link TreeTableCell}, which itself has no {@link TreeItem} property. The {@link TreeItem} {@link Property} we
     * want to bind can be found in the containing {@link TreeTableRow} instead.
     * <p>
     *
     * @param <T> the type of the item contained in the {@link TreeTableRow}
     * @param appCtx the {@link ApplicationContext} of the application
     * @param ctxMenuProperty the {@link ContextMenu} {@link Property} of the {@link TreeTableCell}
     * @param tableRowProperty the {@link TreeTableRow} {@link Property} of the {@link TreeTableCell}
     */
    private static <T> void bindContextMenuForTreeTableCell(ApplicationContext appCtx,
        ObjectProperty<ContextMenu> ctxMenuProperty,
        ReadOnlyObjectProperty<TreeTableRow<T>> tableRowProperty)
    {
        tableRowProperty.addListener((property, oldValue, newValue) ->
        {
            // If the containing TreeTableRow disappears, unbind the context menu if any
            if (newValue == null)
            {
                ctxMenuProperty.unbind();
                return;
            }

            // Otherwise, bind the ContextMenu to the TreeItem Property of the containing TreeTable row.
            bindContextMenu(appCtx, ctxMenuProperty, newValue.treeItemProperty());
        });
    }

    /**
     * Helper method which binds the dynamical computation of a {@link ContextMenu} to a {@link TreeItem}
     * {@link Property}.
     * <p>
     *
     * @param <T> the type of the item contained in the {@link TreeItem}
     * @param appCtx the {@link ApplicationContext} of the application
     * @param ctxMenuProperty the {@link ContextMenu} {@link Property} of the {@link TreeTableCell} or {@link TreeCell}
     * @param treeItemProperty the {@link TreeItem} {@link Property}
     */
    private static <T> void bindContextMenu(ApplicationContext appCtx,
        ObjectProperty<ContextMenu> ctxMenuProperty,
        ReadOnlyObjectProperty<TreeItem<T>> treeItemProperty)
    {
        ctxMenuProperty.bind(new ObjectBinding<ContextMenu>()
        {
            {
                // Fire when the encapsulated TreeItem instance changes
                super.bind(treeItemProperty);
            }

            @Override
            protected ContextMenu computeValue()
            {
                // No TreeItemProperty == no ContextMenu
                if (treeItemProperty == null)
                {
                    return null;
                }

                TreeItem<?> treeItem = treeItemProperty.get();

                // No or Empty TreeItem == no ContextMenu, otherwise construct the menu.
                return (treeItem == null || treeItem.getChildren().size() == 0) ? null
                    : getContextMenu(appCtx, treeItem);
            }
        });
    }

    /**
     * Constructs the {@link ContextMenu} for a {@link TreeItem}.
     * <p>
     * Based on the type of the Object contained in the {@link TreeItem}, menu options can be added.
     * <p>
     * The following options are always provided : Expand All (selected node and all its descendants), Expand First Only
     * (recursively expand only the first child) and Collapse All (collapse the entire subtree).
     * <p>
     * For {@link Node}s, the menu will (when fixed) add an Export To File option.
     * <p>
     *
     * @param <T> the type of the item contained in the {@link TreeItem}
     * @param appCtx the {@link ApplicationContext} of the application
     * @param treeItem the {@link TreeItem} for which the {@link ContextMenu} is constructed
     * @return the constructed {@link ContextMenu}
     */
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

        // TODO FIX - ProfileNodes are no longer used.
        if (treeItem.getValue() instanceof Node)
        {
            addMenuItem(
                menu.getItems(),
                appCtx.textFor(CTXMENU_TREE_EXPORTSUBTREE),
                info -> showExportDialog(
                    appCtx,
                    menu.getScene().getWindow(),
                    "stack_profile.txt",
                    out -> writeStack(appCtx, out, (Node)treeItem.getValue())
                ));
        }
        return menu;
    }

    // Instance Constructors

    /**
     * Private Constructor for utility class.
     */
    private ContextMenuUtil()
    {
        // Private Constructor for utility class
    }
}
