package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;

/**
 * Interface which facilitates binding {@link ChangeListener}s or {@link InvalidationListener}s to and unbinding them
 * from an {@link Observable}.
 * <p>
 * Based on the pattern mentioned by <a href="http://blog.codefx.org/techniques/use-listenerhandles/">"Don’t Remove
 * Listeners – Use ListenerHandles"</a>. A short quote :
 * <p>
 * <code>
 * The ListenerHandle holds on to the references to the observable and the listener. Upon calls to attach() or detach()
 * it either adds the listener to the observable or removes it. For this to be embedded in the language, all methods
 * which currently add listeners to observables should return a handle to that combination.
 * </code>
 * <p>
 * This was extended to allow reattaching the handle to a different {@link Observable}.
 * <p>
 * @param T the type of Observable the handle can reattach the listener to
 */
public interface ListenerHandle<T extends Observable>
{
    /**
     * Add the contained listener to the contained {@link Observable}. If the listener has already been added to an
     * {@link Observable}, {@link #detach()} will be called first. If the contained {@link Observable} is null the
     * method returns without doing anything.
     */
    void attach();

    /**
     * Remove the contained listener from the contained {@link Observable} if the handle is attached.
     */
    void detach();

    /**
     * Remove the contained listener from the currently contained {@link Observable}, switch the contained
     * {@link Observable} to the new, specified {@link Observable} and add the contained listener to it.
     * <p>
     * @param observable the {@link Observable} the listener will be added to
     */
    void reattach(T observable);
}
