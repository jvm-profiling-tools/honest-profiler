package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ChangeListenerHandle<T>
    extends AbstractListenerHandle<ObservableValue<T>, ChangeListener<T>>
{

    public ChangeListenerHandle(ObservableValue<T> observableValue, ChangeListener<T> listener)
    {
        super(observableValue, listener);
    }

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
