package com.insightfullogic.honest_profiler.ports.javafx.model.filter;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.insightfullogic.honest_profiler.core.filters.Filter;

public class FilterSpecification
{
    private boolean hideErrorThreads;

    private List<FilterItem> filters;

    public FilterSpecification()
    {
        super();
        filters = emptyList();
    }

    public FilterSpecification(boolean hideErrorThreads, List<FilterItem> filters)
    {
        super();

        this.hideErrorThreads = hideErrorThreads;
        this.filters = filters;
    }

    public boolean isHideErrorThreads()
    {
        return hideErrorThreads;
    }

    public List<Filter> getFilters()
    {
        return filters.stream().map(FilterItem::toFilter).collect(toList());
    }

    public boolean isFiltering()
    {
        return hideErrorThreads || filters.size() > 0;
    }
}
