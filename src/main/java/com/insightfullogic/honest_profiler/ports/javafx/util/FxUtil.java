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
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Utility class which offers various JavaFX-specific convenience methods.
 */
public final class FxUtil
{
    // Class Properties

    private static String FXML_DIR = "/com/insightfullogic/honest_profiler/ports/javafx/fxml/";

    public static String FXML_ROOT = FXML_DIR + "Root.fxml";
    public static String FXML_PROFILE_ROOT = FXML_DIR + "ProfileRoot.fxml";
    public static String FXML_PROFILE_DIFF_ROOT = FXML_DIR + "ProfileDiffRoot.fxml";

    private static Color[] LABEL_PALETTE = new Color[]
    { LIGHTSTEELBLUE, LIGHTGREEN, ORANGE, LIGHTBLUE, BEIGE, GOLD, LIGHTGREY, LIGHTPINK, CYAN,
        CHARTREUSE };

    // Class Methods

    /**
     * Returns an {@link FXMLLoader} for the specified resource.
     * <p>
     * @param originator the calling {@link Object} used for resource resolution
     * @param resource the String representation of the resource URL
     * @return an {@link FXMLLoader} for the specified resource
     */
    public static FXMLLoader loaderFor(Object originator, String resource)
    {
        return new FXMLLoader(originator.getClass().getResource(resource), getDefaultBundle());
    }

    // Utility methods for adding "coloured labels".

    /**
     * Generates a "colored label", which is a rectangular graphic filled with the specified color, and containing the
     * specified text, and adds it to the provided {@link Pane}.
     * <p>
     * This method should be called after the provided {@link Pane} has already been added to the scene graph. The idea
     * is that with the logic below, the text gets rendered inside its proper place in the scene graph, so that CSS and
     * other style settings are automatically taken into account. The rectangle is then resized to be a bit bigger than
     * the text.
     * <p>
     * @param pane the {@link Pane} into which the graphic will be added
     * @param content the text content to be rendered in the colored label
     * @param color the background color of the label
     * @return the provided {@link Pane}
     */
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

    /**
     * Generates a colored label for the "profile number" of the provided {@link ProfileContext}, as per
     * {@link #addColouredLabel(Pane, String, Color)}, and adds it to the provided pane.
     * <p>
     * This method should be called after the provided {@link Pane} has already been added to the scene graph.
     * <p>
     * @param pane the {@link Pane} into which the graphic will be added
     * @param profileContext the {@link ProfileContext} whose number will be used as text in the colored label
     */
    public static void addProfileNr(Pane pane, ProfileContext profileContext)
    {

        addColouredLabel(
            pane,
            Integer.toString(profileContext.getId()),
            LABEL_PALETTE[profileContext.getId() % LABEL_PALETTE.length]);
    }

    /**
     * Create a typical HBox which can be used for displaying info, and which is ready for adding coloured labels to
     * (see above). The contents will be aligned using the specified alignemnt.
     * <p>
     * @param alignment a {@link Pos} specifying how the {@link HBox} content should be aligned
     * @return the created {@link HBox}
     */
    public static HBox createColoredLabelContainer(Pos alignment)
    {
        HBox box = new HBox();
        box.setAlignment(alignment);
        box.setSpacing(5);
        return box;
    }

    /**
     * Create a typical HBox which can be used for displaying info, and which is ready for adding coloured labels to
     * (see above), with contents aligned using {@link Pos#CENTER_LEFT}.
     * <p>
     * @return the created {@link HBox}
     */
    public static Pane createColoredLabelContainer()
    {
        return createColoredLabelContainer(CENTER_LEFT);
    }

    /**
     * Calculate a column width for a column with the specified box as header graphic.
     * <p>
     *
     * @param box the header graphic for the column
     * @return the calculated width
     */
    public static double calculateHeaderWidth(HBox box)
    {
        double width = 0;
        for (Node node : box.getChildren())
        {
            width += node.getBoundsInLocal().getWidth();
        }
        width += box.getSpacing() * (box.getChildren().size() - 1);
        width += box.getPadding().getLeft() + box.getPadding().getRight();
        return width;
    }

    /**
     * Set various {@link TableColumnBase} properties. Somehow it's hard to get a TableColumn to resize properly.
     * Therefore, we calculate a fair width ourselves.
     * <p>
     *
     * @param column the {@link TreeTableColumn} to be reconfigured
     * @param graphic the graphic to be displayed in the column header
     */
    public static final void reconfigureColumn(TableColumnBase<?, ?> column, HBox graphic)
    {
        if (graphic == null)
        {
            return;
        }

        double width = calculateHeaderWidth(graphic);

        column.setGraphic(graphic);
        column.setMinWidth(width);
        column.setPrefWidth(width + 5);
    }

    /**
     * Hack to work around defective TableView refresh. Tables do not always properly update when items are added or
     * removed.
     * <p>
     * See : <a href="http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items">this StackOverflow
     * topic</a>.
     * <p>
     * @param table the {@link TableView} to be refreshed
     */
    public static void refreshTable(TableView<?> table)
    {
        runLater(() ->
        {
            table.getColumns().get(0).setVisible(false);
            table.getColumns().get(0).setVisible(true);
        });
    }

    /**
     * Returns an indeterminate {@link ProgressIndicator} with the specified dimensions.
     * <p>
     * @param maxWidth the maximum width of the {@link ProgressIndicator}
     * @param maxHeight the maximum height of the {@link ProgressIndicator}
     * @return the created {@link ProgressIndicator}
     */
    public static ProgressIndicator getProgressIndicator(double maxWidth, double maxHeight)
    {
        ProgressIndicator progress = new ProgressIndicator();
        progress.setMaxSize(maxWidth, maxHeight);
        return progress;
    }

    // Instance Constructors

    /**
     * Private constructor for Utility Class
     */
    private FxUtil()
    {
        // Private constructor for Utility Class
    }
}
