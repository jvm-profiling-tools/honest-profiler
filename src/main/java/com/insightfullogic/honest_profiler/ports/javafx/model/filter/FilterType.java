package com.insightfullogic.honest_profiler.ports.javafx.model.filter;

import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.CONTAINS;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.ENDS_WITH;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.EQUALS;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.GE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.GT;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.LE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.LT;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.MATCHES;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType.STARTS_WITH;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.TargetType.CLASS_NAME;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.TargetType.METHOD_NAME;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.TargetType.SELF_TIME;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.TargetType.THREAD;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.TargetType.TOTAL_TIME;
import static java.util.Arrays.asList;

import java.util.List;

import com.insightfullogic.honest_profiler.ports.javafx.model.DisplayableType;

public enum FilterType implements DisplayableType<FilterType>
{
    TIME_SHARE("Time Share", asList(GE, LE, GT, LT, EQUALS), asList(SELF_TIME, TOTAL_TIME)),
    THREAD_SAMPLE("Thread Sample Proportion", asList(GE, LE, GT, LT, EQUALS), asList(THREAD)),
    STRING("String", asList(CONTAINS, EQUALS, STARTS_WITH, ENDS_WITH, MATCHES),
        asList(CLASS_NAME, METHOD_NAME));

    private String displayName;
    private List<ComparisonType> allowedComparisons;
    private List<TargetType> allowedTargets;

    private FilterType(String displayName,
                       List<ComparisonType> allowedComparisons,
                       List<TargetType> allowedTargets)
    {
        this.displayName = displayName;
        this.allowedComparisons = allowedComparisons;
        this.allowedTargets = allowedTargets;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    public List<ComparisonType> getAllowedComparisons()
    {
        return allowedComparisons;
    }

    public List<TargetType> getAllowedTargets()
    {
        return allowedTargets;
    }
}
