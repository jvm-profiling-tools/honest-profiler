package com.insightfullogic.honest_profiler.core.aggregation.filter;

import java.util.function.Predicate;

import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;

/**
 * Specification of a single filter, which can be instantiated as a {@link FilterPredicate}. A filter will retrieve a
 * {@link Target} value from the input, and compare it against the specified value using the specified
 * {@link Comparison}.
 * <p>
 * @param <T> the type of item which is being filtered
 * @param <U> the type of the values being compared by the filter
 */
public class FilterItem<T, U>
{
    // Instance Properties

    private Target target;
    private Comparison comparison;
    private U value;

    // Instance Constructors

    /**
     * Constructor, providing the {@link Target} value in the input which should be compared using the given
     * {@link Comparison} against the specified value.
     * <p>
     * @param target the {@link Target} for the filter
     * @param comparison the {@link Comparison} to be applied
     * @param value the value to be compared against
     */
    public FilterItem(Target target, Comparison comparison, U value)
    {
        super();

        this.target = target;
        this.comparison = comparison;
        this.value = value;
    }

    // Instance Accessors

    /**
     * Returns the {@link Target} for the filter, which describes which value of the input item to apply the filter to.
     * <p>
     * @return the {@link Target} for the filter
     */
    public Target getTarget()
    {
        return target;
    }

    /**
     * Returns the {@link Comparison} operator for the filter.
     * <p>
     * @return the {@link Comparison} to be applied
     */
    public Comparison getComparison()
    {
        return comparison;
    }

    /**
     * Returns the value the filter should compare against.
     * <p>
     * @return the value to be compared against
     */
    public U getValue()
    {
        return value;
    }

    // Filter Creation

    /**
     * Creates the {@link FilterPredicate} which can filter items of type T by extracting the value specified by
     * {@link Target} from them. The {@link ItemType} explicity describes the type T, and is needed for implementation
     * reasons.
     * <p>
     * @param type the type of the item the {@link FilterPredicate} can be used on
     * @return a {@link FilterPredicate} which filters according to the specifications in this FilterItem
     */
    public Predicate<T> toFilter(ItemType type)
    {
        return new FilterPredicate<>(type, target, comparison, value);
    }
}
