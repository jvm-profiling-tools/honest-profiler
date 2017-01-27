package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.addProfileNr;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.createColoredLabelContainer;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.geometry.Pos.CENTER;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Superclass for all Diff View Controllers in the application which provide a view on a comparison between two
 * "targets", data structures of type T which are each stored in their respective {@link ProfileContext}.
 *
 * This superclass stores the {@link ProfileContext}s encapsulating the two targets. It also ensures that subclass
 * refresh() implementations are called when a new instance of either target is available in the context.
 *
 * This is achieved by keeping two local target {@link Property}s, which can be bound or unbound to the target
 * {@link Property}s in the {@link ProfileContext}s.
 *
 * By binding or unbinding the local targets, it is possible to star and stop all tracking of changes to the targets in
 * the UI. This has been provided to make it possible to stop executing refresh() and other UI updates when the view
 * associated to the controller is hidden.
 *
 * The superclass also provides some common UI helper methods for column configuration.
 *
 * @param <T> the data type of the target
 */
public abstract class AbstractProfileDiffViewController<T, U> extends AbstractViewController<U>
{
    // Instance Properties

    private ProfileContext baseContext;
    private ProfileContext newContext;

    private ObjectProperty<T> baseTarget;
    private ObjectProperty<T> newTarget;
    private ObjectBinding<T> baseSourceBinding;
    private ObjectBinding<T> newSourceBinding;

    // FXML Implementation

    /**
     * This method must be called by subclasses in their FXML initialize(). It provides the extraction function which
     * specifies how to get the targets from their {@link ProfileContext}s. It also passes on the controller-local UI
     * nodes needed by the AbstractViewController superclass.
     *
     * @param targetExtractor function which extracts the target from the {@link ProfileContext}
     * @param filterButton the button used to trigger filter editing
     * @param quickFilterButton the button used to apply the quick filter
     * @param quickFilterText the TextField providing the value for the quick filter
     */
    @Override
    protected void initialize(Button filterButton, Button quickFilterButton,
        TextField quickFilterText, ItemType type)
    {
        super.initialize(filterButton, quickFilterButton, quickFilterText, type);

        baseTarget = new SimpleObjectProperty<>();
        newTarget = new SimpleObjectProperty<>();
        baseTarget.addListener((property, oldValue, newValue) -> refresh());
        newTarget.addListener((property, oldValue, newValue) -> refresh());
    }

    // Instance Accessors

    /**
     * Returns the {@link ProfileContext} for the baseline target. The name has been shortened to unclutter code in
     * subclasses.
     *
     * @return the {@link ProfileContext} encapsulating the baseline target.
     */
    protected ProfileContext baseCtx()
    {
        return baseContext;
    }

    /**
     * Returns the {@link ProfileContext} for the target which will be compared against the baseline. The name has been
     * shortened to unclutter code in subclasses.
     *
     * @return the {@link ProfileContext} encapsulating the target being compared against the baseline.
     */
    protected ProfileContext newCtx()
    {
        return newContext;
    }

    /**
     * Returns the current baseline target instance.
     *
     * @return the current baseline target instance
     */
    protected T getBaseTarget()
    {
        return baseTarget.get();
    }

    /**
     * Returns the current "new" target instance.
     *
     * @return the current "new" target instance
     */
    protected T getNewTarget()
    {
        return newTarget.get();
    }

    /**
     * Sets the {@link ProfileContext}s encapsulating the baseline target and the target being compared against it.
     *
     * @param profileContext the {@link ProfileContext}s encapsulating the baseline target and the target being compared
     *            against it
     */
    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        this.baseContext = baseContext;
        this.newContext = newContext;
    }

    // Source-Target Binding

    /**
     * Set the source object the target data structure T will be extracted from, and the function which extracts the
     * target data structure T from the source.
     *
     * @param source the source providing the target data structure
     * @param targetExtractor a function which extracts the target from the source object
     */
    public void bind(ObjectProperty<? extends Object> baseSource,
        ObjectProperty<? extends Object> newSource, Function<Object, T> targetExtractor)
    {
        baseSourceBinding = createObjectBinding(
            () -> targetExtractor.apply(baseSource.get()),
            baseSource);
        newSourceBinding = createObjectBinding(
            () -> targetExtractor.apply(newSource.get()),
            newSource);
    }

    // Activation

    /**
     * Binds the local target {@link Property}s to the target {@link Property}s in the {@link ProfileContext}s, using
     * the target extractor function. The net effect is that the controller will start tracking changes to the target
     * instances in the {@link ProfileContext}.
     */
    public void activate()
    {
        baseTarget.bind(baseSourceBinding);
        newTarget.bind(newSourceBinding);
    }

    /**
     * Unbinds the local target {@link Property}s. The controller no longer tracks changes to the target
     * {@link Property}s in the {@link ProfileContext}s.
     */
    public void deactivate()
    {
        baseTarget.unbind();
        newTarget.unbind();
    }

    // UI Helper Methods

    @Override
    protected <C> void setColumnHeader(C column, String title, ProfileContext profileContext)
    {
        HBox header = createColoredLabelContainer(CENTER);

        if (profileContext != null)
        {
            addProfileNr(header, profileContext);
        }

        header.getChildren().add(new Text(title));

        // Somehow it's hard to get a TableColumn to resize properly.
        // Therefore, we calculate a fair width ourselves.
        double width = calculateWidth(header);

        if (column instanceof TreeTableColumn<?, ?>)
        {
            reconfigure((TreeTableColumn<?, ?>)column, null, header, width, width + 5);
        }
        else
        {
            reconfigure((TableColumn<?, ?>)column, null, header, width, width + 5);
        }
    }

    private void reconfigure(TreeTableColumn<?, ?> column, String text, Node graphic,
        double minWidth, double prefWidth)
    {
        column.setText(text);
        column.setGraphic(graphic);
        column.setMinWidth(minWidth);
        column.setPrefWidth(prefWidth);
    }

    private void reconfigure(TableColumn<?, ?> column, String text, Node graphic, double minWidth,
        double prefWidth)
    {
        column.setText(text);
        column.setGraphic(graphic);
        column.setMinWidth(minWidth);
        column.setPrefWidth(prefWidth);
    }

    private double calculateWidth(HBox box)
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
}
