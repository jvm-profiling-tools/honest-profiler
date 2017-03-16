package com.insightfullogic.honest_profiler.ports.javafx.view.menu;

import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.text.Text;

/**
 * This {@link CheckMenuItem} subclass stores a {@link TableColumn} or {@link TreeTableColumn} along with the column
 * header (which can be text or a graphic {@link Node}). Checking the item renders the column visible, unchecking it
 * renders the column invisible.
 *
 * By default, the menu item is selected.
 *
 * When the item is displayed, the graphic will be displayed. If no graphic was specified, the text will be used.
 */
public class ColumnMenuItem extends CheckMenuItem
{
    // Instance Constructors

    /**
     * Simple Constructor specifying the column along with the header text and or graphic.
     *
     * @param column the column managed by the item
     * @param headerGraphic the header graphic
     */
    public ColumnMenuItem(TableColumnBase<?, ?> column, Node headerGraphic)
    {
        super();

        this.setSelected(column.isVisible());
        this.setGraphic(headerGraphic);

        // Columns may have a fixed header text defined in the FXML instead of a generated graphic.
        if (headerGraphic == null)
        {
            this.setGraphic(new Text(column.getText()));
        }

        // Originally a binding was used : column.visibleProperty().bind(selectedProperty());
        // However, this apparently conflicts with the internal JavaFX ColumnMenu (or TableMenu) rebuilding, which binds
        // bidirectionally to the property.
        // So both listeners below mimic a bidirectional binding.
        // See : https://bugs.openjdk.java.net/browse/JDK-8136468

        // Toggle column visibility. Double-check visibility to avoid infinite loops with the other listener below.
        selectedProperty().addListener((property, oldValue, newValue) ->
        {
            if (column.isVisible() != newValue)
            {
                column.setVisible(newValue);
            }
        });

        // Toggle menu selection based on column visibility. This is added to keep the selection status in sync with
        // column visibility when other mechanisms toggle the column visibility.
        column.visibleProperty().addListener((property, oldValue, newValue) ->
        {
            setSelected(newValue);
        });
    }
}
