package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.log.Method;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;

import java.text.MessageFormat;

import static javafx.scene.control.TableColumn.CellDataFeatures;

public class Rendering {

    public static SimpleObjectProperty<String> timeShare(CellDataFeatures<FlatProfileEntry, String> features) {
        double timeShare = features.getValue().getTimeShare();
        String formattedTimeShare = renderTimeShare(timeShare);
        return new ReadOnlyObjectWrapper<>(formattedTimeShare);
    }

    public static String renderTimeShare(double timeShare) {
        return MessageFormat.format("{0,number,#.##%}", timeShare);
    }

    public static SimpleObjectProperty<String> method(CellDataFeatures<FlatProfileEntry, String> features) {
        Method method = features.getValue().getMethod();
        String representation = renderMethod(method);
        return new ReadOnlyObjectWrapper<>(representation);
    }

    public static String renderMethod(Method method) {
        return method.getClassName() + "." + method.getMethodName();
    }

}
