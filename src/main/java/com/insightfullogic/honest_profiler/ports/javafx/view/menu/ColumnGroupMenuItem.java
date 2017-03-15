package com.insightfullogic.honest_profiler.ports.javafx.view.menu;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.text.Text;

/**
 * This {@link MenuItem} subclass changes the visibility of a set of {@link TableColumn}s or {@link TreeTableColumn}s.
 * Selecting the item renders all columns visible or invisible.
 */
public class ColumnGroupMenuItem extends MenuItem
{
    // Instance Constructors

    /**
     * Simple Constructor specifying the menu item text, the visibility and the columns.
     *
     * @param title the menu item text
     * @param visible the visibility of the columns after the menu item has been selected
     * @param columns the columns managed by the item
     */
    public ColumnGroupMenuItem(String title, boolean visible, TableColumnBase<?, ?>... columns)
    {
        super();

        this.setGraphic(new Text(title));

        List<TableColumnBase<?, ?>> columnList = asList(columns);
        setOnAction(event -> columnList.forEach(column -> column.setVisible(visible)));
    }

    /**
     * Simple Constructor specifying the menu item text, the visibility and collection of columns.
     *
     * @param title the menu item text
     * @param visible the visibility of the columns after the menu item has been selected
     * @param columns the columns managed by the item
     */
    public ColumnGroupMenuItem(String title,
                               boolean visible,
                               Collection<TableColumnBase<?, ?>> columns)
    {
        super();

        this.setGraphic(new Text(title));

        setOnAction(event -> columns.forEach(column -> column.setVisible(visible)));
    }
}
