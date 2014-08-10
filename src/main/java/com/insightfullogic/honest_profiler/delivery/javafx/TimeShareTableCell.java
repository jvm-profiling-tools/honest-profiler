package com.insightfullogic.honest_profiler.delivery.javafx;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import javafx.scene.control.TableCell;

import static com.insightfullogic.honest_profiler.delivery.javafx.Rendering.renderTimeShare;

public class TimeShareTableCell extends TableCell<FlatProfileEntry, Double> {

    @Override
    protected void updateItem(Double item, boolean empty) {
        if (item == null) {
            setText("");
        } else {
            setText(renderTimeShare(item));
        }
    }
}
