package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.addProfileNr;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.createColoredLabelContainer;
import static javafx.beans.binding.Bindings.createObjectBinding;
import static javafx.geometry.Pos.CENTER;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.AbstractDiff;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Superclass for all Diff View Controllers in the application which provide a view on a comparison between two
 * "targets", data structures of type T which are each stored in their respective target {@link ObjectProperty}s.The
 * targets are extracted from source {@link ObservableObjectValue}s using an extractor function.
 * <p>
 * This superclass also serves as a repository for the {@link ProfileContext}s associated to the profiles being compared
 * in the "Diff View".
 * <p>
 * * This superclass ensures that subclass refresh() implementations are called when the source
 * {@link ObservableObjectValue}s or the {@link CombinedGrouping} from the {@link AbstractViewController} superclass are
 * updated. The extractor function is then used to extract new targets from the sources, using the new
 * {@link CombinedGrouping} if available.
 * <p>
 * By binding or unbinding the local targets, it is possible to start and stop all tracking of changes to the targets in
 * the UI. This has been provided to make it possible to stop executing refresh() and other UI updates when the view
 * associated to the controller is hidden.
 * <p>
 * The superclass also provides some common UI helper methods for column configuration.
 * <p>
 * @see AbstractDiff class javadoc for an explanation of the "Base" and "New" terminology
 *      <p>
 * @param <T> the data type of the targets
 * @param <U> the type of the items contained in the View
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
     * Initialize method for subclasses which sets the basic properties needed by this superclass. This method must be
     * called by such subclasses in their FXML initialize().
     * <p>
     * @param type the {@link ItemType} specifying the type of items shown in the View
     */
    @Override
    protected void initialize(ItemType type)
    {
        super.initialize(type);

        baseTarget = new SimpleObjectProperty<>();
        newTarget = new SimpleObjectProperty<>();
        baseTarget.addListener((property, oldValue, newValue) -> refresh());
        newTarget.addListener((property, oldValue, newValue) -> refresh());
    }

    // Instance Accessors

    /**
     * Returns the {@link ProfileContext} for the baseline target. The name has been shortened to unclutter code in
     * subclasses.
     * <p>
     * @return the {@link ProfileContext} encapsulating the baseline target.
     */
    protected ProfileContext baseCtx()
    {
        return baseContext;
    }

    /**
     * Returns the {@link ProfileContext} for the "new" target which will be compared against the baseline. The name has
     * been shortened to unclutter code in subclasses.
     * <p>
     * @return the {@link ProfileContext} encapsulating the target being compared against the baseline.
     */
    protected ProfileContext newCtx()
    {
        return newContext;
    }

    /**
     * Returns the current baseline target instance.
     * <p>
     * @return the current baseline target instance
     */
    protected T getBaseTarget()
    {
        return baseTarget.get();
    }

    /**
     * Returns the current "new" target instance.
     * <p>
     * @return the current "new" target instance
     */
    protected T getNewTarget()
    {
        return newTarget.get();
    }

    /**
     * Sets the {@link ProfileContext}s encapsulating the baseline target and the target being compared against it.
     * <p>
     * @param baseContext the {@link ProfileContext}s encapsulating the baseline target
     * @param newContext the {@link ProfileContext}s encapsulating the target being compared
     */
    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        this.baseContext = baseContext;
        this.newContext = newContext;

        // Called here because for the Diff column headers, the profile contexts are needed to display the profile
        // number label.
        initializeTable();
    }

    // Source-Target Binding

    /**
     * Bind the supplied extractor function which extracts the target data structure T from the source to the source
     * {@link ObservableObjectValue}s, and optionally to the {@link CombinedGrouping} {@link ObservableObjectValue} from
     * the {@link AbstractViewController} superclass if present.
     * <p>
     * @param baseSource the {@link ObservableObjectValue} encapsulating the source from which the Base target data
     *            structure can be extracted
     * @param newSource the {@link ObservableObjectValue} encapsulating the source from which the New target data
     *            structure can be extracted
     * @param targetExtractor a function which extracts the target from the source Object
     */
    public void bind(ObjectProperty<? extends Object> baseSource,
        ObjectProperty<? extends Object> newSource, Function<Object, T> targetExtractor)
    {
        // The createObjectBinding() dependency varargs parameter specifies a number of Observables. If the value of any
        // of those changes, the Binding is triggered and the specified function is executed. This is IMHO not so
        // clearly documented in the createObjectBinding() javadoc.

        // The View does not support CombinedGrouping.
        if (getGrouping() == null)
        {
            baseSourceBinding = createObjectBinding(
                () -> targetExtractor.apply(baseSource.get()),
                baseSource);
            newSourceBinding = createObjectBinding(
                () -> targetExtractor.apply(newSource.get()),
                newSource);
        }
        // The View does supports CombinedGrouping.
        else
        {
            baseSourceBinding = createObjectBinding(
                () -> targetExtractor.apply(baseSource.get()),
                baseSource,
                getGrouping());
            newSourceBinding = createObjectBinding(
                () -> targetExtractor.apply(newSource.get()),
                newSource,
                getGrouping());
        }
    }

    // Activation Methods

    /**
     * Activate or deactivate the current view. When activated, the view tracks changes in the target.
     * <p>
     * @param active a boolean indicating whether to activate or deactivate the view.
     */
    public void setActive(boolean active)
    {
        if (active)
        {
            // Binds the local target ObjectProperties to the sourceBindings created with the bind() method. The net
            // effect is that the controller will start tracking changes to the target instances.
            baseTarget.bind(baseSourceBinding);
            newTarget.bind(newSourceBinding);
        }
        else
        {
            // Unbinds the local target ObjectProperties. The controller no longer tracks changes to the source
            // ObservableObjectvalues.
            baseTarget.unbind();
            newTarget.unbind();
        }
    }

    // AbstractViewController Implementation

    @Override
    protected HBox getColumnHeader(TableColumnBase<?, ?> column, String title,
        ProfileContext profileContext)
    {
        HBox header = createColoredLabelContainer(CENTER);

        if (profileContext != null)
        {
            addProfileNr(header, profileContext);
        }

        header.getChildren().add(new Text(title));

        return header;
    }
}
