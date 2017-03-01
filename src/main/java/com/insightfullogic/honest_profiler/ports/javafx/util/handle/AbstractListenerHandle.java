package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.Observable;

public abstract class AbstractListenerHandle<T extends Observable, U> implements ListenerHandle<T>
{
    private boolean attached;
    private T observable;
    private U listener;

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
        if (this.attached)
        {
            detach();
        }

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

    protected abstract void addListener(T observable, U listener);

    protected abstract void removeListener(T observable, U listener);
}
