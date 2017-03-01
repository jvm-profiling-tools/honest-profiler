package com.insightfullogic.honest_profiler.ports.javafx.model.filter;

import static java.lang.Double.parseDouble;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.filters.ClassNameFilter;
import com.insightfullogic.honest_profiler.core.filters.Filter;
import com.insightfullogic.honest_profiler.core.filters.Filter.Mode;
import com.insightfullogic.honest_profiler.core.filters.MethodNameFilter;
import com.insightfullogic.honest_profiler.core.filters.SelfTimeShareFilter;
import com.insightfullogic.honest_profiler.core.filters.ThreadSampleFilter;
import com.insightfullogic.honest_profiler.core.filters.TotalTimeShareFilter;

public class FilterItem
{
    private static final Map<ComparisonType, Filter.Mode> cmpConversion = new HashMap<>();

    static
    {
        cmpConversion.put(ComparisonType.CONTAINS, Mode.CONTAINS);
        cmpConversion.put(ComparisonType.STARTS_WITH, Mode.STARTS_WITH);
        cmpConversion.put(ComparisonType.ENDS_WITH, Mode.ENDS_WITH);
        cmpConversion.put(ComparisonType.MATCHES, Mode.MATCHES);
        cmpConversion.put(ComparisonType.EQUALS, Mode.EQUALS);
        cmpConversion.put(ComparisonType.GE, Mode.GE);
        cmpConversion.put(ComparisonType.GT, Mode.GE);
        cmpConversion.put(ComparisonType.LE, Mode.LE);
        cmpConversion.put(ComparisonType.LT, Mode.LT);
    }

    private FilterType filterType;
    private ComparisonType comparisonType;
    private TargetType targetType;
    private String value;

    public FilterItem(FilterType filterType,
                      ComparisonType comparisonType,
                      TargetType targetType,
                      String value)
    {
        super();

        this.filterType = filterType;
        this.comparisonType = comparisonType;
        this.targetType = targetType;
        this.value = value;
    }

    public FilterType getFilterType()
    {
        return filterType;
    }

    public void setFilterType(FilterType filterType)
    {
        this.filterType = filterType;
    }

    public ComparisonType getComparisonType()
    {
        return comparisonType;
    }

    public void setComparisonType(ComparisonType comparisonType)
    {
        this.comparisonType = comparisonType;
    }

    public TargetType getTargetType()
    {
        return targetType;
    }

    public void setTargetType(TargetType targetType)
    {
        this.targetType = targetType;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Filter toFilter()
    {
        switch (filterType)
        {
            case THREAD_SAMPLE:
                return new ThreadSampleFilter(
                    cmpConversion.get(comparisonType),
                    parseDouble(value) / 100);

            case TIME_SHARE:
                switch (targetType)
                {
                    case SELF_TIME:
                        return new SelfTimeShareFilter(
                            cmpConversion.get(comparisonType),
                            parseDouble(value) / 100);
                    case TOTAL_TIME:
                        return new TotalTimeShareFilter(
                            cmpConversion.get(comparisonType),
                            parseDouble(value) / 100);
                    default:
                        return null;
                }

            case STRING:
                switch (targetType)
                {
                    case CLASS_NAME:
                        return new ClassNameFilter(cmpConversion.get(comparisonType), value);
                    case METHOD_NAME:
                        return new MethodNameFilter(cmpConversion.get(comparisonType), value);
                    default:
                        return null;
                }

            default:
                return null;
        }
    }
}
