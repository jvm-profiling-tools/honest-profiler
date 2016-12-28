package com.insightfullogic.honest_profiler.ports.javafx.util;

import static javafx.application.Platform.runLater;
import static javafx.geometry.Pos.CENTER;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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

    // Utility method for adding "colored labels".
    // The pane passed as parameter should be part of the scene graph. The idea
    // is that with the logic below, the text gets rendered inside its proper
    // place in the scene graph, so that CSS and other style settings are
    // automatically taken into account. The rectangle is then resized to be a
    // bit bigger than the text.
    public static Node addColoredLabel(Pane pane, String content, Color color)
    {
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(CENTER);

        Text text = new Text(content);
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(color);

        stackPane.getChildren().addAll(rectangle, text);
        pane.getChildren().add(stackPane);

        // Render the text, and use the resulting image size to resize the
        // rectangle
        WritableImage image = text.snapshot(null, null);
        rectangle.setWidth(image.getWidth() + 2);
        rectangle.setHeight(image.getHeight() + 2);

        return pane;
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
