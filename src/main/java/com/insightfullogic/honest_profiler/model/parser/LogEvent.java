package com.insightfullogic.honest_profiler.model.parser;

public interface LogEvent {

    public void accept(EventListener listener);
    
}
