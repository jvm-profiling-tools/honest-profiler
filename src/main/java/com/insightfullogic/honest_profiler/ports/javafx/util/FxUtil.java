package com.insightfullogic.honest_profiler.ports.javafx.util;

import static javafx.application.Platform.runLater;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;

public final class FxUtil
{
    private static String FXML_DIR = "/com/insightfullogic/honest_profiler/ports/javafx/fxml/";

    public static String FXML_ROOT = FXML_DIR + "Root.fxml";
    public static String FXML_PROFILE_ROOT = FXML_DIR + "ProfileRoot.fxml";
    public static String FXML_FLAT_DIFF_VIEW = FXML_DIR + "FlatDiffView.fxml";

    public static FXMLLoader loaderFor(Object originator, String resource)
    {
        return new FXMLLoader(originator.getClass().getResource(resource));
    }

    // Hack to work around defective TableView refresh. Tables do not properly
    // update when items are added or removed.
    // See :
    // http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
    public static void refreshTable(TableView<?> table)
    {
        runLater(() ->
        {
            table.getColumns().get(0).setVisible(false);
            table.getColumns().get(0).setVisible(true);
        });
    }

    private FxUtil()
    {
        // Private constructor for Utility Class
    }
}
