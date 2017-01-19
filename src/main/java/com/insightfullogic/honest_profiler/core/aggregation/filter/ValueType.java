package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.NUMBER_COMPARISONS;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.PCT_INTERPRETER;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.STRING_COMPARISONS;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;;

public enum ValueType
{
    DOUBLE(validatorFor(Double::parseDouble), Double::parseDouble, NUMBER_COMPARISONS),
    SHARE(validatorFor(Double::parseDouble, 0, 100), PCT_INTERPRETER, NUMBER_COMPARISONS),
    PERCENT(validatorFor(Double::parseDouble), PCT_INTERPRETER, NUMBER_COMPARISONS),
    INTEGER(validatorFor(Integer::parseInt), Integer::parseInt, NUMBER_COMPARISONS),
    LONG(validatorFor(Long::parseLong), Long::parseLong, NUMBER_COMPARISONS),
    STRING(str -> true, str -> str, STRING_COMPARISONS);

    private Predicate<String> stringValidator;
    private Function<String, ?> stringInterpreter;
    private List<Comparison> allowedComparisons;

    private ValueType(Predicate<String> stringValidator,
                      Function<String, ?> stringInterpreter,
                      Comparison... allowedComparisons)
    {
        this.stringValidator = stringValidator;
        this.stringInterpreter = stringInterpreter;
        this.allowedComparisons = asList(allowedComparisons);
    }

    public List<Comparison> getAllowedComparisons()
    {
        return allowedComparisons;
    }

    public Predicate<String> getValidator()
    {
        return stringValidator;
    }

    @SuppressWarnings("unchecked")
    public <T> Function<String, T> getInterpreter()
    {
        return (Function<String, T>) stringInterpreter;
    }

    private static final <T> Predicate<String> validatorFor(Function<String, T> convertor)
    {
        return new Predicate<String>()
        {
            @Override
            public boolean test(String value)
            {
                try
                {
                    convertor.apply(value);
                    return true;
                }
                catch (Throwable t)
                {
                    return false;
                }
            }
        };
    }

    private static final Predicate<String> validatorFor(Function<String, Double> convertor,
        double lower, double upper)
    {
        return new Predicate<String>()
        {
            @Override
            public boolean test(String value)
            {
                try
                {
                    double converted = convertor.apply(value);
                    return converted >= lower && converted <= upper;
                }
                catch (Throwable t)
                {
                    return false;
                }
            }
        };
    }
}
