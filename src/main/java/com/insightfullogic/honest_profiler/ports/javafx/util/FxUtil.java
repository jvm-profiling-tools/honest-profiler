package com.insightfullogic.honest_profiler.ports.javafx.util;

import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.getDefaultBundle;
import static java.lang.Math.max;
import static javafx.application.Platform.runLater;
import static javafx.geometry.Pos.CENTER;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.paint.Color.BEIGE;
import static javafx.scene.paint.Color.CHARTREUSE;
import static javafx.scene.paint.Color.CYAN;
import static javafx.scene.paint.Color.GOLD;
import static javafx.scene.paint.Color.LIGHTBLUE;
import static javafx.scene.paint.Color.LIGHTGREEN;
import static javafx.scene.paint.Color.LIGHTGREY;
import static javafx.scene.paint.Color.LIGHTPINK;
import static javafx.scene.paint.Color.LIGHTSTEELBLUE;
import static javafx.scene.paint.Color.ORANGE;

import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
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
    public static String FXML_PROFILE_DIFF_ROOT = FXML_DIR + "ProfileDiffRoot.fxml";

    private static Color[] LABEL_PALETTE = new Color[]
    { LIGHTSTEELBLUE, LIGHTGREEN, ORANGE, LIGHTBLUE, BEIGE, GOLD, LIGHTGREY, LIGHTPINK, CYAN,
        CHARTREUSE };

    public static FXMLLoader loaderFor(Object originator, String resource)
    {
        return new FXMLLoader(originator.getClass().getResource(resource), getDefaultBundle());
    }

    // Utility methods for adding "coloured labels".
    // Call this after the Pane has already been added to the scene graph !
    // The idea is that with the logic below, the text gets rendered inside its
    // proper place in the scene graph, so that CSS and other style settings are
    // automatically taken into account. The rectangle is then resized to be a
    // bit bigger than the text.

    public static Node addColouredLabel(Pane pane, String content, Color color)
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

        double width = image.getWidth() + 2;
        double height = image.getHeight() + 2;

        rectangle.setWidth(max(width, height));
        rectangle.setHeight(image.getHeight() + 2);

        return pane;
    }

    // Convenience method specifically for displaying Profile Nrs.
    // Call this after the Pane has already been added to the scene graph !

    public static void addProfileNr(Pane pane, ProfileContext profileContext)
    {

        addColouredLabel(
            pane,
            Integer.toString(profileContext.getId()),
            LABEL_PALETTE[profileContext.getId() % LABEL_PALETTE.length]);
    }

    // Create a typical HBox which can be used for displaying info, and which is
    // ready for adding coloured labels to (see above).

    public static HBox createColoredLabelContainer(Pos alignment)
    {
        HBox box = new HBox();
        box.setAlignment(alignment);
        box.setSpacing(5);
        return box;
    }

    public static Pane createColoredLabelContainer()
    {
        return createColoredLabelContainer(CENTER_LEFT);
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

    public static ProgressIndicator getProgressIndicator(double maxWidth, double maxHeight)
    {
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(maxWidth, maxHeight);
        return progress;
    }

    private FxUtil()
    {
        // Private constructor for Utility Class
    }
}
