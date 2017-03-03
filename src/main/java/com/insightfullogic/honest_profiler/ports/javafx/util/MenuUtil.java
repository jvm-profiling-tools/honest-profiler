package com.insightfullogic.honest_profiler.ports.javafx.util;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Utility class which offers convenience methods for working with {@link Menu}s and related objects.
 */
public final class MenuUtil
{
    // Class Methods

    /**
     * Add a {@link MenuItem} with the specified label and {@link EventHandler} to the specified {@link ObservableList}.
     * <p>
     * This is a convenience method for significantly condensing menu creation code.
     * <p>
     * @param menu an {@link ObservableList} of {@link MenuItem}s the new item will be added to
     * @param label the label for the new {@link MenuItem}
     * @param handler the {@link EventHandler} which is invoked when the {@link MenuItem} is selected
     */
    public static void addMenuItem(ObservableList<MenuItem> menu, String label,
        EventHandler<ActionEvent> handler)
    {
        MenuItem menuItem = new MenuItem(label);
        menuItem.setOnAction(handler);
        menu.add(menuItem);
    }

    // Instance Constructors

    /**
     * Empty Constructor for utility class.
     */
    private MenuUtil()
    {
        // Empty Constructor for utility class
    }
}
