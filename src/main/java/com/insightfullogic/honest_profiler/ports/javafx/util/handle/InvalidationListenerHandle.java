package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class InvalidationListenerHandle
    extends AbstractListenerHandle<Observable, InvalidationListener>
{
    public InvalidationListenerHandle(Observable observable, InvalidationListener listener)
    {
        super(observable, listener);
    }

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
