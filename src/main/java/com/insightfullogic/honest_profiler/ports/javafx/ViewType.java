package com.insightfullogic.honest_profiler.ports.javafx;

import com.insightfullogic.honest_profiler.ports.javafx.model.DisplayableType;

public enum ViewType implements DisplayableType<ViewType>
{
    FLAT("Flat View"),
    TREE("Tree View"),
    FLAME("Flame View");

    private String displayName;

    private ViewType(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }
}
