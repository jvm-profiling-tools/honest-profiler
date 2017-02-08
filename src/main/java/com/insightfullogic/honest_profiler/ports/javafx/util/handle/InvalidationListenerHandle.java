package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

/**
 * Trivial {@link AbstractListenerHandle} implementation for {@link InvalidationListener}s and {@link ObservableValue}s
 * encapsulating {@link Object}s of type T.
 *
 * @param <T> the type of the {@link Object} encapsulated by the {@link ObservableValue}
 */
public class InvalidationListenerHandle
    extends AbstractListenerHandle<Observable, InvalidationListener>
{
    // Instance Constructors

    /**
     * @see AbstractListenerHandle#AbstractListenerHandle(javafx.beans.Observable, Object)
     */
    public InvalidationListenerHandle(Observable observable, InvalidationListener listener)
    {
        super(observable, listener);
    }

    // AbstractListenerHandle Implementation

    @Override
    protected void addListener(Observable observable, InvalidationListener listener)
    {
        observable.addListener(listener);
    }

    @Override
    protected void removeListener(Observable observable, InvalidationListener listener)
    {
        observable.removeListener(listener);
    }
}
