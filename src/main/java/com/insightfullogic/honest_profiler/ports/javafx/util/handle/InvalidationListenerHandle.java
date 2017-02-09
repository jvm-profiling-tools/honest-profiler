package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * Trivial {@link AbstractListenerHandle} implementation for {@link InvalidationListener}s and {@link Observable}s..
 */
public class InvalidationListenerHandle
    extends AbstractListenerHandle<Observable, InvalidationListener>
{
    // Instance Constructors

    /**
     * @see AbstractListenerHandle#AbstractListenerHandle(javafx.beans.Observable, Object)
     * <p>
     * @param observable the {@link Observable} the listener will be added to
     * @param listener the listener
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
