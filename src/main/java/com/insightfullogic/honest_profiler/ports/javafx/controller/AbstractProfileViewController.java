package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static javafx.beans.binding.Bindings.createObjectBinding;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Superclass for all View Controllers in the application which provide a view on the "target", a data structure of type
 * T which is stored in the source object.
 *
 * This superclass also serves as a repository for the {@link ProfileContext}.
 *
 * It ensures that subclass refresh() implementations are called when a new instance of the target is available in the
 * context.
 *
 * This is achieved by keeping a local target {@link Property}, which can be bound or unbound to the target
 * {@link Property} in the {@link ProfileContext}.
 *
 * By binding or unbinding the local target, it is possible to star and stop all tracking of changes to the target in
 * the UI. This has been provided to make it possible to stop executing refresh() and other UI updates when the view
 * associated to the controller is hidden.
 *
 * @param <T> the data type of the target
 */
public abstract class AbstractProfileViewController<T, U> extends AbstractViewController<U>
{
    // Instance Properties

    private ProfileContext profileContext;

    private ObjectProperty<T> target;

    private ObjectBinding<T> sourceBinding;

    // FXML Implementation

    /**
     * This method must be called by subclasses in their FXML initialize(). It passes on the controller-local UI nodes
     * needed by the AbstractViewController superclass.
     *
     * @param filterButton the button used to trigger filter editing
     * @param quickFilterButton the button used to apply the quick filter
     * @param quickFilterText the TextField providing the value for the quick filter
     */
    @Override
    protected void initialize(Button filterButton, Button quickFilterButton,
        TextField quickFilterText, ItemType type)
    {
        super.initialize(filterButton, quickFilterButton, quickFilterText, type);

        target = new SimpleObjectProperty<>();
        target.addListener((property, oldValue, newValue) -> refresh());
    }

    // Instance Accessors

    /**
     * Returns the {@link ProfileContext}. The name has been shortened to unclutter code in subclasses.
     *
     * @return the {@link ProfileContext} encapsulating the target.
     */
    protected ProfileContext prfCtx()
    {
        return profileContext;
    }

    /**
     * Sets the {@link ProfileContext} encapsulating the target.
     *
     * @param profileContext the {@link ProfileContext} encapsulating the target.
     */
    public void setProfileContext(ProfileContext profileContext)
    {
        this.profileContext = profileContext;
    }

    /**
     * Returns the current target instance.
     *
     * @return the current target instance
     */
    protected T getTarget()
    {
        return target.get();
    }

    // Source-Target Binding

    /**
     * Set the source object the target data structure T will be extracted from, and the function which extracts the
     * target data structure T from the source.
     *
     * @param source the source providing the target data structure
     * @param targetExtractor a function which extracts the target from the source object
     */
    public void bind(ObservableObjectValue<? extends Object> source,
        Function<Object, T> targetExtractor)
    {
        sourceBinding = createObjectBinding(() -> targetExtractor.apply(source.get()), source);
    }

    // Activation

    /**
     * Binds the local target {@link Property} to the target {@link Property} in the {@link ProfileContext}, using the
     * target extractor function. The net effect is that the controller will start tracking changes to the target
     * instance in the {@link ProfileContext}.
     */
    public void activate()
    {
        target.bind(sourceBinding);
    }

    /**
     * Unbinds the local target {@link Property}. The controller no longer tracks changes to the target {@link Property}
     * in the {@link ProfileContext}.
     */
    public void deactivate()
    {
        target.unbind();
    }

    // AbstractViewController Implementation

    /**
     * Override doing nothing. The profile view controllers have fixed column headings defined in the FXML.
     */
    @Override
    protected <C> void setColumnHeader(C column, String title, ProfileContext context)
    {
        // NOOP
    }
}
