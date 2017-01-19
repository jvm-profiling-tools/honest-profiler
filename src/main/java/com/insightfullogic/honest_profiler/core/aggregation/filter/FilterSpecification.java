package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.CONTAINS;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.NOT_CONTAINS;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.FQMN;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.function.Predicate;

import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;

public class FilterSpecification<T>
{
    private ItemType type;
    private boolean hideErrors;
    private String quickFilter;

    private List<FilterItem<T, ?>> filters;

    public FilterSpecification(ItemType type)
    {
        super();
        this.type = type;
        filters = emptyList();
    }

    public FilterSpecification(ItemType type, boolean hideErrors, List<FilterItem<T, ?>> filters)
    {
        super();

        this.type = type;
        this.hideErrors = hideErrors;
        this.filters = filters;
    }

    public boolean isHideErrors()
    {
        return hideErrors;
    }

    public void setQuickFilter(String value)
    {
        this.quickFilter = value;
    }

    public Predicate<T> getFilter()
    {
        Predicate<T> result = hideErrors ? errorFilter() : null;
        if (quickFilter != null && !quickFilter.isEmpty())
        {
            result = result == null ? quickFilter() : result.and(quickFilter());
        }
        if (filters.size() > 0)
        {
            result = result == null ? filter() : result.and(filter());
        }
        return result == null ? str -> true : result;
    }

    public boolean isFiltering()
    {
        return hideErrors || filters.size() > 0;
    }

    private Predicate<T> filter()
    {
        return filters.stream().map(item -> item.toFilter(type)).reduce(Predicate::and).get();
    }

    private Predicate<T> quickFilter()
    {
        return new FilterItem.FilterPredicate<T, String>(type, FQMN, CONTAINS, quickFilter);
    }

    private final Predicate<T> errorFilter()
    {
        return new FilterItem.FilterPredicate<T, String>(type, FQMN, NOT_CONTAINS, "[ERR=");
    }
}
