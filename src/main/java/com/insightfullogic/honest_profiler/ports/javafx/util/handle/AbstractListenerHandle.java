package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.Observable;

/**
 * Superclass implementing the {@link ListenerHandle} attach/detach logic regardless of the type of listener or
 * {@link Observable}.
 * <p>
 * @param <T> the type of the {@link Observable} the handle can be attached to
 * @param <U> the type of the contained listener
 */
public abstract class AbstractListenerHandle<T extends Observable, U> implements ListenerHandle<T>
{
    // Instance Properties

    private boolean attached;
    private T observable;
    private U listener;

    // Instance Constructors

    /**
     * Constructor specifying the {@link Observable} the listener will be added to or removed from, and the actual
     * listener.
     * <p>
     * This implementation is not threadsafe.
     * <p>
     * @param observable the {@link Observable} the listener will be added to
     * @param listener the listener
     */
    public AbstractListenerHandle(T observable, U listener)
    {
        super();

        this.attached = false;
        this.observable = observable;
        this.listener = listener;
    }

    @Override
    public void attach()
    {
        // Never add the same listener twice.
        if (this.attached)
        {
            detach();
        }

        // Do nothing of there is no contained Observable.
        if (this.observable == null)
        {
            return;
        }

        this.attached = true;
        addListener(this.observable, this.listener);
    }

    @Override
    public void detach()
    {
        if (this.attached)
        {
            removeListener(this.observable, this.listener);
            this.attached = false;
        }
    }

    @Override
    public void reattach(T observable)
    {
        detach();
        this.observable = observable;
        attach();
    }

    /**
     * Implements the actual addition of the listener of type U to an {@link Observable} of type T
     * <p>
     * @param observable the {@link Observable} the listener has to be added to
     * @param listener the listener to be added
     */
    protected abstract void addListener(T observable, U listener);

    /**
     * Implements the actual removal of the listener of type U from an {@link Observable} of type T
     * <p>
     * @param observable the {@link Observable} the listener has to be removed from
     * @param listener the listener to be removed
     */
    protected abstract void removeListener(T observable, U listener);
}
