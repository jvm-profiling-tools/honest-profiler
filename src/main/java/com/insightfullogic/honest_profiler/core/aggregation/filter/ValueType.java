package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.NUMBER_COMPARISONS;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.PCT_INTERPRETER;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.STRING_COMPARISONS;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;;

/**
 * Enumeration for the various types of values in aggregation items. A ValueType also provides a validator, which tests
 * whether an input String can be converted to a value of the specified type, an interpreter which converts an input
 * String to a value of the specified type, and a list of {@link Comparison}s which are compatible with the ValueType.
 * <p>
 * Note the presence of the PCT_INTERPRETER, which can't be defined as a constant before the enum declarations, so it
 * was defined in the {@link Comparison} enumeration instead. Just the way the cookie crumbles, I guess.
 */
public enum ValueType
{
    /** ValueType for {@link Double}s */
    DOUBLE(validatorFor(Double::parseDouble), Double::parseDouble, NUMBER_COMPARISONS),
    /** SHARE represents a percentage representing a part/total relation, i.e. it is between 0 and 100. */
    SHARE(validatorFor(Double::parseDouble, 0, 100), PCT_INTERPRETER, NUMBER_COMPARISONS),
    /** PERCENT represents an arbitrary, unbounded percentage. */
    PERCENT(validatorFor(Double::parseDouble), PCT_INTERPRETER, NUMBER_COMPARISONS),
    /** ValueType for {@link Integer}s */
    INTEGER(validatorFor(Integer::parseInt), Integer::parseInt, NUMBER_COMPARISONS),
    /** ValueType for {@link Long}s */
    LONG(validatorFor(Long::parseLong), Long::parseLong, NUMBER_COMPARISONS),
    /** ValueType for {@link String}s */
    STRING(str -> true, str -> str, STRING_COMPARISONS);

    // Internal Validator Class Factory Methods

    /**
     * Returns a validator {@link Predicate} which tests the String by trying to apply corresponding the convertor. If
     * that operation throws an Exception, the String cannot be converted.
     * <p>
     * @param <T> the type of the result of the convertor {@link Function}
     * @param convertor the convertor {@link Function} which converts a String to a value of the specified type T
     * @return a validator {@link Predicate}
     */
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

    /**
     * Returns a validator {@link Predicate} for {@link Double}s as in {@link #validatorFor(Function)}, with an extra
     * boundary check.
     * <p>
     * @param convertor the convertor {@link Function} which converts a String to a Double
     * @param lower the lower bound the value has to be equal to or greater than in order to be accepted
     * @param upper the upper bound the value has to be equal to or less than in order to be accepted
     * @return a validator {@link Predicate} checking whether the String represents a Double within the specified range
     */
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

    // Instance Properties

    private Predicate<String> stringValidator;
    private Function<String, ?> stringInterpreter;
    private List<Comparison> allowedComparisons;

    // Instance Constructors

    private ValueType(Predicate<String> stringValidator,
                      Function<String, ?> stringInterpreter,
                      Comparison... allowedComparisons)
    {
        this.stringValidator = stringValidator;
        this.stringInterpreter = stringInterpreter;
        this.allowedComparisons = asList(allowedComparisons);
    }

    // Instance Accessors

    /**
     * Returns a list of {@link Comparison}s compatible with this ValueType.
     * <p>
     * @return a list of {@link Comparison}s compatible with this ValueType
     */
    public List<Comparison> getAllowedComparisons()
    {
        return allowedComparisons;
    }

    /**
     * Returns a {@link Predicate} for testing whether a String can be converted to a value of this type.
     * <p>
     * @return a {@link Predicate} for testing whether a String can be converted to a value of this type
     */
    public Predicate<String> getValidator()
    {
        return stringValidator;
    }

    /**
     * Returns a {@link Function} for converting a String to a value of this type.
     * <p>
     * @param <T> the type of the result of the conversion {@link Function}
     * @return a {@link Function} for converting a String to a value of this type
     */
    @SuppressWarnings("unchecked")
    public <T> Function<String, T> getInterpreter()
    {
        return (Function<String, T>)stringInterpreter;
    }
}
