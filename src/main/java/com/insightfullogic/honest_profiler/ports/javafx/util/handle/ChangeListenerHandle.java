package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Trivial {@link AbstractListenerHandle} implementation for {@link ChangeListener}s and {@link ObservableValue}s
 * encapsulating {@link Object}s of type T.
 * <p>
 * @param <T> the type of the {@link Object} encapsulated by the {@link ObservableValue}
 */
public class ChangeListenerHandle<T>
    extends AbstractListenerHandle<ObservableValue<T>, ChangeListener<T>>
{
    // Instance Constructors

    /**
     * @see AbstractListenerHandle#AbstractListenerHandle(javafx.beans.Observable, Object)
     * <p>
     * @param observable the {@link ObservableValue} the listener will be added to
     * @param listener the listener
     */
    public ChangeListenerHandle(ObservableValue<T> observable, ChangeListener<T> listener)
    {
        super(observable, listener);
    }

    // AbstractListenerHandle Implementation

    @Override
    protected void addListener(ObservableValue<T> observable, ChangeListener<T> listener)
    {
        observable.addListener(listener);
    }

    @Override
    protected void removeListener(ObservableValue<T> observable, ChangeListener<T> listener)
    {
        observable.removeListener(listener);
    }
}
