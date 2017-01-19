package com.insightfullogic.honest_profiler.ports.javafx;

public enum ViewType
{
    FLAT("Flat View"), TREE("Tree View"), FLAME("Flame View");

    private String name;

    private ViewType(String displayName)
    {
        name = displayName;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
