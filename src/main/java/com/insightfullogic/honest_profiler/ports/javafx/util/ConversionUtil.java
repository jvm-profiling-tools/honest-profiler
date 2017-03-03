package com.insightfullogic.honest_profiler.ports.javafx.util;

import static java.util.EnumSet.allOf;

import javafx.util.StringConverter;

/**
 * Utility class for various conversions.
 */
public final class ConversionUtil
{
    // Class Properties

    private static final long NS_TO_MS = 1000 * 1000;

    // Class Methods

    /**
     * Convert the specified number of nanoseconds to number of milliseconds, ignoring the fractional part of the
     * result.
     * <p>
     * @param nanos the number of nanoseconds
     * @return the number of milliseconds
     */
    public static final long toMillis(long nanos)
    {
        return nanos / NS_TO_MS;
    }

    /**
     * Generates a {@link StringConverter} for an {@link Enum}, converting between the String representation obtained by
     * calling {@link Enum#toString()} and the {@link Enum} value.
     * <p>
     * @param <T> the type of the {@link Enum} subclass
     * @param type the type of the {@link Enum} subclass.
     * @return a {@link StringConverter} which converts between {@link Enum} values and their {@link Enum#toString()}
     *         representations
     */
    public static <T extends Enum<T>> StringConverter<T> getStringConverterForType(Class<T> type)
    {
        return new StringConverter<T>()
        {
            @Override
            public String toString(T type)
            {
                return type == null ? "" : type.toString();
            }

            @Override
            public T fromString(String name)
            {
                return name == null ? null
                    : allOf(type).stream().filter(type -> type.toString().equals(name)).findFirst()
                        .get();
            }
        };
    }

    // Instance Constructors

    /**
     * Private Constructor for utility class
     */
    private ConversionUtil()
    {
        // Private Constructor for utility class
    }
}
