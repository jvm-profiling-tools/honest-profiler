package com.insightfullogic.honest_profiler.core.parser;

public interface LogEvent {

    public void accept(EventListener listener);
    
}
