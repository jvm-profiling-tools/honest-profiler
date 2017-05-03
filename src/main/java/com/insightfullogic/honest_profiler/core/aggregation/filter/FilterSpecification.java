package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.CONTAINS;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.NOT_STARTS_WITH;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.KEY;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.function.Predicate;

import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;

/**
 * This class describes a filter composed of several {@link FilterItem}s. The resulting filter will only accept inputs
 * which satisfy the conditions of all the {@link FilterItem}s.
 * <p>
 * Additionally it contains a boolean which generates an extra {@link Predicate} for filtering out "error frames"
 * (frames in the profile which do not correspond to proper Java methods), and a quick-filter String generates an extra
 * {@link Predicate} for filtering the key.
 * <p>
 * @param <T> the type of the input items which can be filtered
 */
public class FilterSpecification<T>
{
    // Instance Properties

    private ItemType type;
    private boolean hideErrors;
    private String quickFilter;

    private List<FilterItem<T, ?>> filters;

    // Instance Constructors

    /**
     * Constructor for an empty {@link FilterSpecification} which specifies a filter for filtering items of the
     * specified {@link ItemType}.
     * <p>
     * @param type the type of items the filter can filter
     */
    public FilterSpecification(ItemType type)
    {
        super();

        this.type = type;
        filters = emptyList();
    }

    /**
     * Constructor for an empty {@link FilterSpecification} which specifies a filter for filtering items of the
     * specified {@link ItemType}.
     * <p>
     * @param type the type of items the filter can filter
     * @param hideErrors a boolean specifying if error frames should be filtered out
     * @param filters a {@link List} of the contained {@link FilterItem}s
     */
    public FilterSpecification(ItemType type, boolean hideErrors, List<FilterItem<T, ?>> filters)
    {
        super();

        this.type = type;
        this.hideErrors = hideErrors;
        this.filters = filters;
    }

    // Instance Accessors

    /**
     * Returns a boolean indicating whether the resulting filter will filter out error frames.
     * <p>
     * @return a boolean indicating whether the resulting filter will filter out error frames.
     */
    public boolean isHideErrors()
    {
        return hideErrors;
    }

    /**
     * Returns a boolean indicating whether the FilterSpecification is non-trivial, i.e. whether it actually has any
     * filters defined. The quickfilter is not taken into account.
     * <p>
     * The reason is that this is used by the front-end to indicate to the user whether currently a filter has been
     * defined by the user. The filter is specified separately from the quickfilter condition.
     * <p>
     * @return a boolean indicating whether the FilterSpecification is non-trivial
     */
    public boolean isFiltering()
    {
        return hideErrors || filters.size() > 0;
    }

    /**
     * Sets the quickfilter String, which when not empty will generate an extra {@link Predicate} for filtering the key.
     * <p>
     * @param value the value used for filtering
     */
    public void setQuickFilter(String value)
    {
        this.quickFilter = value;
    }

    // Filter Construction Methods

    /**
     * Generates a {@link Predicate} which accepts items of type T if they are accepted by all of the filters from all
     * contained {@link FilterItem}s, and optionally if they do not contain errors and/or if the key contains the String
     * specified by the quickfilter String.
     * <p>
     * @return a {@link Predicate} implementing the {@link FilterSpecification}
     */
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

    // Internal Filter Factory Methods

    /**
     * Create a {@link Predicate} which filters as specified by the {@link FilterItem}s in this FilterSpecification.
     * <p>
     * @return a {@link Predicate} corresponding to the contained {@link FilterItem}s
     */
    private Predicate<T> filter()
    {
        return filters.stream().map(item -> item.toFilter(type)).reduce(Predicate::and).get();
    }

    /**
     * Create a {@link Predicate} for filtering the key using the quickfilter.
     * <p>
     * @return a {@link Predicate} for filtering the key
     */
    private Predicate<T> quickFilter()
    {
        return new FilterPredicate<T, String>(type, KEY, CONTAINS, quickFilter);
    }

    /**
     * Create a {@link Predicate} for filtering out error frames.
     * <p>
     * @return a {@link Predicate} for filtering out error frames
     */
    private final Predicate<T> errorFilter()
    {
        return new FilterPredicate<T, String>(type, KEY, NOT_STARTS_WITH, "AGCT.").and(
            new FilterPredicate<T, String>(type, KEY, NOT_STARTS_WITH, "Unknown <"));
    }
}
