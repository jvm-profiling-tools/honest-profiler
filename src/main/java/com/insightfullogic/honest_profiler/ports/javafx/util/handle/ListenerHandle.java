package com.insightfullogic.honest_profiler.ports.javafx.util.handle;

import javafx.beans.Observable;

public interface ListenerHandle<T extends Observable>
{
    void attach();

    void detach();

    void reattach(T observable);
}
