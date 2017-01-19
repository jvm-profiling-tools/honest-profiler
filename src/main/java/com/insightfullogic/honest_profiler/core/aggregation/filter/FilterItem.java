package com.insightfullogic.honest_profiler.core.aggregation.filter;

import java.util.function.Function;
import java.util.function.Predicate;

import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;

public class FilterItem<T, U>
{
    private Target target;
    private Comparison comparison;
    private U value;

    public FilterItem(Target target, Comparison comparison, U value)
    {
        super();

        this.target = target;
        this.comparison = comparison;
        this.value = value;
    }

    public Comparison getComparison()
    {
        return comparison;
    }

    public Target getTarget()
    {
        return target;
    }

    public U getValue()
    {
        return value;
    }

    public Predicate<T> toFilter(ItemType type)
    {
        return new FilterPredicate<>(type, target, comparison, value);
    }

    static class FilterPredicate<T, U> implements Predicate<T>
    {

        private Function<T, U> extractor;
        private Predicate<U> comparer;

        FilterPredicate(ItemType type, Target target, Comparison comparison, U value)
        {
            extractor = target.<T, U>getExtractor(type);
            comparer = comparison.getPredicate(value);
        }

        @Override
        public boolean test(T t)
        {
            return comparer.test(extractor.apply(t));
        }
    }
}
