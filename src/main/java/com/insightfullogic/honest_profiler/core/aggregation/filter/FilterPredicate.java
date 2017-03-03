package com.insightfullogic.honest_profiler.core.aggregation.filter;

import java.util.function.Function;
import java.util.function.Predicate;

import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;

/**
 * This {@link Predicate} extracts the value described by {@link Target} from its input, and compares it using the
 * specified {@link Comparison} against a specified value.
 * <p>
 * The filter is composed of 2 parts :
 * <ul>
 * <li>an extractor {@link Function} which extracts the value to be compared from the input</li>
 * <li>a comparer {@link Predicate} which will compare the extracted value against the value specified by the
 * filter</li>
 * </ul>
 * <p>
 * @param <T> the type of the input items which will be tested
 * @param <U> the type of the values which will be compared
 */
public class FilterPredicate<T, U> implements Predicate<T>
{
    // Instance Properties

    private Function<T, U> extractor;
    private Predicate<U> comparer;

    // Instance COnstructors

    /**
     * Basic constructor which takes the type of the item being filtered, the {@link Target} describing the value to be
     * extracted from the input, the type of {@link Comparison} and the value to be compared against.
     * <p>
     * @param type the type of the item this FilterPredicate can be used on
     * @param target the {@link Target} for the filter
     * @param comparison the {@link Comparison} to be applied
     * @param value the value to be compared against
     */
    public FilterPredicate(ItemType type, Target target, Comparison comparison, U value)
    {
        extractor = target.<T, U>getExtractor(type);
        comparer = comparison.getPredicate(value);
    }

    // Predicate Implementation

    @Override
    public boolean test(T t)
    {
        return comparer.test(extractor.apply(t));
    }
}
