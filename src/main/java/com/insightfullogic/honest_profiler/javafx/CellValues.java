package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.log.Method;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;

import java.text.MessageFormat;

import static javafx.scene.control.TableColumn.CellDataFeatures;

public class CellValues {

    public static SimpleObjectProperty<String> timeShare(CellDataFeatures<FlatProfileEntry, String> features) {
        double timeShare = features.getValue().getTimeShare();
        String formattedTimeShare = MessageFormat.format("{0,number,#.##%}", timeShare);
        return new ReadOnlyObjectWrapper<>(formattedTimeShare);
    }

    public static SimpleObjectProperty<String> method(CellDataFeatures<FlatProfileEntry, String> features) {
        Method method = features.getValue().getMethod();
        String representation = method.getClassName() + "." + method.getMethodName();
        return new ReadOnlyObjectWrapper<>(representation);
    }

}
