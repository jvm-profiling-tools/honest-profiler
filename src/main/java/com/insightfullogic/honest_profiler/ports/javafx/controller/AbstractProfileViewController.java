package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static javafx.beans.binding.Bindings.createObjectBinding;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.control.TableColumnBase;
import javafx.scene.layout.HBox;

/**
 * Superclass for all View Controllers in the application which provide a view on a "target", a data structure of type T
 * which is stored in the target property. This target is extracted from a source {@link ObservableObjectValue} using an
 * extractor function.
 * <p>
 * This superclass also serves as a repository for the {@link ProfileContext} associated to the profile for which the
 * View is shown.
 * <p>
 * This superclass ensures that subclass refresh() implementations are called when the source
 * {@link ObservableObjectValue} or the {@link CombinedGrouping} from the {@link AbstractViewController} superclass are
 * updated. The extractor function is then used to extract a new target from the source, using the new
 * {@link CombinedGrouping} if available.
 * <p>
 * By binding or unbinding the local target, it is possible to start and stop all tracking of changes to the target in
 * the UI. This has been provided to make it possible to stop executing refresh() and other UI updates when the view
 * associated to the controller is hidden.
 * <p>
 * @param <T> the data type of the target
 * @param <U> the type of the items contained in the View
 */
public abstract class AbstractProfileViewController<T, U> extends AbstractViewController<U>
{
    // Instance Properties

    private ProfileContext profileContext;

    private ObjectProperty<T> target;

    private ObjectBinding<T> sourceBinding;

    // FXML Implementation

    /**
     * Initialize method for subclasses which sets the basic properties needed by this superclass. This method must be
     * called by such subclasses in their FXML initialize().
     * <p>
     * @param type the {@link ItemType} of the items shown in the view
     */
    @Override
    protected void initialize(ItemType type)
    {
        super.initialize(type);

        target = new SimpleObjectProperty<>();
        target.addListener((property, oldValue, newValue) -> refresh());
    }

    // Instance Accessors

    /**
     * Returns the {@link ProfileContext}. The name has been shortened to unclutter code in subclasses.
     * <p>
     * @return the {@link ProfileContext} encapsulating the target.
     */
    protected ProfileContext prfCtx()
    {
        return profileContext;
    }

    /**
     * Sets the {@link ProfileContext} encapsulating the target.
     * <p>
     * @param profileContext the {@link ProfileContext} encapsulating the target.
     */
    public void setProfileContext(ProfileContext profileContext)
    {
        this.profileContext = profileContext;

        initializeTable();
    }

    /**
     * Returns the current target instance.
     * <p>
     * @return the current target instance
     */
    protected T getTarget()
    {
        return target.get();
    }

    // Source-Target Binding

    /**
     * Bind the supplied extractor function which extracts the target data structure T from the source to the source
     * {@link ObservableObjectValue}, and optionally to the {@link CombinedGrouping} {@link ObservableObjectValue} from
     * the {@link AbstractViewController} superclass if present.
     * <p>
     * @param source the {@link ObservableObjectValue} encapsulating the source from which the target data structure can
     *            be extracted
     * @param targetExtractor a function which extracts the target from the source Object
     */
    public void bind(ObservableObjectValue<? extends Object> source,
        Function<Object, T> targetExtractor)
    {
        // The createObjectBinding() dependency varargs parameter specifies a number of Observables. If the value of any
        // of those changes, the Binding is triggered and the specified function is executed. This is IMHO not so
        // clearly documented in the createObjectBinding() javadoc.

        // The View does not support CombinedGrouping.
        if (getGrouping() == null)
        {
            sourceBinding = createObjectBinding(() -> targetExtractor.apply(source.get()), source);
        }
        // The View supports CombinedGrouping.
        else
        {
            sourceBinding = createObjectBinding(
                () -> targetExtractor.apply(source.get()),
                source,
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
            // Binds the local target ObjectProperty to the sourceBinding created with the bind() method. The net effect
            // is that the controller will start tracking changes to the target instance.
            target.bind(sourceBinding);
        }
        else
        {
            // Unbinds the local target ObjectProperty. The controller no longer tracks changes to the source
            // ObservableObjectvalue.
            target.unbind();
        }
    }

    // AbstractViewController Implementation

    /**
     * Override doing nothing. The {@link AbstractProfileViewController} implementations have fixed column headings
     * defined in the FXML.
     */
    @Override
    protected HBox getColumnHeader(TableColumnBase<?, ?> column, String title,
        ProfileContext context)
    {
        return null;
    }
}
