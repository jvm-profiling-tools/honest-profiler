package com.insightfullogic.honest_profiler.ports.javafx.model.filter;

import com.insightfullogic.honest_profiler.ports.javafx.model.DisplayableType;

public enum TargetType implements DisplayableType<TargetType>
{

    CLASS_NAME("Class Name"),
    METHOD_NAME("Method Name"),
    THREAD("Thread"),
    SELF_TIME("Self Time"),
    TOTAL_TIME("Total Time");

    private String displayName;

    private TargetType(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }
}
