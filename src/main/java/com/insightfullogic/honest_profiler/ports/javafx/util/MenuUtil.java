package com.insightfullogic.honest_profiler.ports.javafx.util;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

public final class MenuUtil
{
    public static void addMenuItem(ObservableList<MenuItem> menu, String label,
        EventHandler<ActionEvent> handler)
    {
        MenuItem menuItem = new MenuItem(label);
        menuItem.setOnAction(handler);
        menu.add(menuItem);
    }

    private MenuUtil()
    {
        // Empty Constructor for utility class
    }
}
