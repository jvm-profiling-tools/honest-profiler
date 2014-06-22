package com.insightfullogic.honest_profiler.core.model.parser;

public interface LogEvent {

    public void accept(EventListener listener);
    
}
