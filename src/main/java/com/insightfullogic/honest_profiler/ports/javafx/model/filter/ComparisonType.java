package com.insightfullogic.honest_profiler.ports.javafx.model.filter;

import com.insightfullogic.honest_profiler.ports.javafx.model.DisplayableType;

public enum ComparisonType implements DisplayableType<ComparisonType>
{
    EQUALS("=="),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),
    ENDS_WITH("Ends With"),
    STARTS_WITH("Starts With"),
    CONTAINS("Contains"),
    MATCHES("Matches");

    private String displayName;

    private ComparisonType(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }
}
