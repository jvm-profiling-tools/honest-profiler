package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static java.util.regex.Pattern.compile;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public enum Comparison
{
    EQUALS_NR("="),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),
    EQUALS_STR("Equals"),
    STARTS_WITH("Starts With"),
    ENDS_WITH("Ends With"),
    CONTAINS("Contains"),
    NOT_CONTAINS("Doesn't Contain"),
    MATCHES("Matches");

    // A bit hacky : can't define this AND use it in ValueType, since it needs to be used there before it can be
    // defined (Enums can be sooo tricky :( ). So it got placed here semi-arbitrarily...
    public static final Function<String, ?> PCT_INTERPRETER = str -> Double.parseDouble(str) / 100.;

    public static final Comparison[] NUMBER_COMPARISONS = new Comparison[]
    { EQUALS_NR, GT, LT, GE, LE };

    public static final Comparison[] STRING_COMPARISONS = new Comparison[]
    { EQUALS_STR, STARTS_WITH, ENDS_WITH, CONTAINS, MATCHES };

    public static Predicate<Double> getPredicate(Comparison comparison, Double value)
    {
        switch (comparison)
        {
            case EQUALS_NR:
                return nr -> nr == value;
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

    public static Predicate<Integer> getPredicate(Comparison comparison, Integer value)
    {
        switch (comparison)
        {
            case EQUALS_NR:
                return nr -> nr == value;
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

    public static Predicate<Long> getPredicate(Comparison comparison, Long value)
    {
        switch (comparison)
        {
            case EQUALS_NR:
                return nr -> nr == value;
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

    public static Predicate<String> getPredicate(Comparison comparison, String value)
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
            case NOT_CONTAINS:
                return str -> !str.contains(value);
            case MATCHES:
                Pattern pattern = compile(value);
                return str -> pattern.matcher(str).matches();
            default:
                break;
        }

        throw new RuntimeException(
            "Comparison type " + comparison + " is not compatible with the value type String.");
    }

    private String name;

    private Comparison(String name)
    {
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public <T> Predicate<T> getPredicate(T value)
    {
        if (value instanceof Double)
        {
            return (Predicate<T>) Comparison.getPredicate(this, (Double) value);
        }
        if (value instanceof Integer)
        {
            return (Predicate<T>) Comparison.getPredicate(this, (Integer) value);
        }
        if (value instanceof Long)
        {
            return (Predicate<T>) Comparison.getPredicate(this, (Long) value);
        }
        if (value instanceof String)
        {
            return (Predicate<T>) Comparison.getPredicate(this, (String) value);
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
