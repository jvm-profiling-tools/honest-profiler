package com.insightfullogic.honest_profiler.log;

public interface LogEvent {

    public void accept(EventListener listener);
    
}
