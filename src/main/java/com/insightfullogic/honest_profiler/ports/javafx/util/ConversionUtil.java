package com.insightfullogic.honest_profiler.ports.javafx.util;

import static java.util.EnumSet.allOf;

import javafx.util.StringConverter;

public final class ConversionUtil
{
    private static final long NS_TO_MS = 1000 * 1000;

    public static final long toMillis(long nanos)
    {
        return nanos / NS_TO_MS;
    }

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

    private ConversionUtil()
    {
        // Private Constructor for utility class
    }
}
