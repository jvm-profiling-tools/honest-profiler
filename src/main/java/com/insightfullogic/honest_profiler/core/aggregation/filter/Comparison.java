package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static java.util.regex.Pattern.compile;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Enumeration of the comparisons supported by filtering. The comparisons each provide the {@link Predicate} though
 * their {@link #getPredicate(Object)} method, which applies the comparison to an item and specified value, and returns
 * whether the item fulfills the condition.
 * <p>
 * It would have been nice to be able to parametrize the Enum, it might have been possible to make the internal factory
 * methods lighter. May be revisited if <a href="http://openjdk.java.net/jeps/301">JEP 301</a> gets implemented.
 */
public enum Comparison
{
    // Numerical comparisons
    EQUALS_NR("="),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),
    // String comparisons
    EQUALS_STR("Equals"),
    STARTS_WITH("Starts With"),
    ENDS_WITH("Ends With"),
    CONTAINS("Contains"),
    NOT_STARTS_WITH("Doesn't Start With"),
    NOT_ENDS_WITH("Doesn't End With"),
    NOT_CONTAINS("Doesn't Contain"),
    MATCHES("Matches"); // Regexp Matching

    // Class Properties

    // A bit hacky : This is for use by ValueType, but it can't be defined there before it actually is needed.
    // Enums can be sooo tricky :( So it got placed here semi-arbitrarily...
    public static final Function<String, ?> PCT_INTERPRETER = str -> Double.parseDouble(str) / 100.;

    /**
     * List of Comparisons which can be applied to {@link Number}s which are available in the front-end.
     */
    public static final Comparison[] NUMBER_COMPARISONS = new Comparison[]
    { EQUALS_NR, GT, LT, GE, LE };

    /**
     * List of Comparisons which can be applied to {@link String}s which are available in the front-end.
     */
    public static final Comparison[] STRING_COMPARISONS = new Comparison[]
    { EQUALS_STR, STARTS_WITH, ENDS_WITH, CONTAINS, MATCHES };

    // Class Methods

    /**
     * Internal {@link Predicate} factory for applying comparisons to {@link Double}s.
     * <p>
     * @param comparison the comparison for which the {@link Predicate} is constructed
     * @param value the value the comparison will compare against
     * @return a {@link Predicate} which can compare {@link Double}s against the specified value
     */
    private static Predicate<Double> getPredicate(Comparison comparison, Double value)
    {
        switch (comparison)
        {
            case EQUALS_NR:
                return nr -> nr.doubleValue() == value.doubleValue();
            case GT:
                return nr -> nr > value;
            case LT:
                return nr -> nr < value;
            case GE:
                return nr -> nr >= value;
            case LE:
                return nr -> nr <= value;
            default:
                break;
        }
        throw new RuntimeException(
            "Comparison type " + comparison + " is not compatible with the value type Double.");
    }

    /**
     * Internal {@link Predicate} factory for applying comparisons to {@link Integer}s.
     * <p>
     * @param comparison the comparison for which the {@link Predicate} is constructed
     * @param value the value the comparison will compare against
     * @return a {@link Predicate} which can compare {@link Integer}s against the specified value
     */
    private static Predicate<Integer> getPredicate(Comparison comparison, Integer value)
    {
        switch (comparison)
        {
            case EQUALS_NR:
                return nr -> nr.intValue() == value.intValue();
            case GT:
                return nr -> nr > value;
            case LT:
                return nr -> nr < value;
            case GE:
                return nr -> nr >= value;
            case LE:
                return nr -> nr <= value;
            default:
                break;
        }
        throw new RuntimeException(
            "Comparison type " + comparison + " is not compatible with the value type Integer.");
    }

    /**
     * Internal {@link Predicate} factory for applying comparisons to {@link Long}s.
     * <p>
     * @param comparison the comparison for which the {@link Predicate} is constructed
     * @param value the value the comparison will compare against
     * @return a {@link Predicate} which can compare {@link Long}s against the specified value
     */
    private static Predicate<Long> getPredicate(Comparison comparison, Long value)
    {
        switch (comparison)
        {
            case EQUALS_NR:
                return nr -> nr.longValue() == value.longValue();
            case GT:
                return nr -> nr > value;
            case LT:
                return nr -> nr < value;
            case GE:
                return nr -> nr >= value;
            case LE:
                return nr -> nr <= value;
            default:
                break;
        }
        throw new RuntimeException(
            "Comparison type " + comparison + " is not compatible with the value type Long.");
    }

    /**
     * Internal {@link Predicate} factory for applying comparisons to {@link String}s.
     * <p>
     * @param comparison the comparison for which the {@link Predicate} is constructed
     * @param value the value the comparison will compare against
     * @return a {@link Predicate} which can compare {@link String}s against the specified value
     */
    private static Predicate<String> getPredicate(Comparison comparison, String value)
    {
        switch (comparison)
        {
            case EQUALS_STR:
                return str -> str.equals(value);
            case STARTS_WITH:
                return str -> str.startsWith(value);
            case ENDS_WITH:
                return str -> str.endsWith(value);
            case CONTAINS:
                return str -> str.contains(value);
            case NOT_STARTS_WITH:
                return str -> !str.startsWith(value);
            case NOT_ENDS_WITH:
                return str -> !str.endsWith(value);
            case NOT_CONTAINS:
                return str -> !str.contains(value);
            // For the MATCHES comparison the value is interpreted as a regular expression.
            case MATCHES:
                Pattern pattern = compile(value);
                return str -> pattern.matcher(str).matches();
            default:
                break;
        }

        throw new RuntimeException(
            "Comparison type " + comparison + " is not compatible with the value type String.");
    }

    // Instance Properties

    private String name;

    // Instance Constructors

    /**
     * Private constructor, setting the name which can be used for displaying the Comparison.
     * <p>
     * @param name the display name
     */
    private Comparison(String name)
    {
        this.name = name;
    }

    // Instance Methods

    /**
     * Returns a {@link Predicate} which will evaluate this comparison against the provided value.
     * <p>
     * @param <T> the type of the {@link Object} tested by the {@link Predicate}
     * @param value the value the {@link Predicate} will compare against
     * @return a {@link Predicate} which compares input to the provided value
     */
    @SuppressWarnings("unchecked")
    public <T> Predicate<T> getPredicate(T value)
    {
        if (value instanceof Double)
        {
            return (Predicate<T>)getPredicate(this, (Double)value);
        }
        if (value instanceof Integer)
        {
            return (Predicate<T>)getPredicate(this, (Integer)value);
        }
        if (value instanceof Long)
        {
            return (Predicate<T>)getPredicate(this, (Long)value);
        }
        if (value instanceof String)
        {
            return (Predicate<T>)getPredicate(this, (String)value);
        }
        throw new RuntimeException(
            "Comparison type " + this + " is not compatible with the value type String.");
    }

    @Override
    public String toString()
    {
        return name;
    }
}
