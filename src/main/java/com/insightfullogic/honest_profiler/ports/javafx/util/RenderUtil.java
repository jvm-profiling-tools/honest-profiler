package com.insightfullogic.honest_profiler.ports.javafx.util;

import static java.text.MessageFormat.format;

import com.insightfullogic.honest_profiler.core.collector.Frame;
import com.insightfullogic.honest_profiler.core.parser.Method;

public final class RenderUtil
{
    public static String renderPercentage(double percentage)
    {
        return format("{0,number,0.00 %}", percentage);
    }

    public static String renderMethod(Frame method)
    {
        if (method == null)
        {
            return "unknown";
        }

        return method.getClassName() + "." + method.getMethodName();
    }

    public static String renderShortMethod(Method method)
    {
        String className = method.getClassName();
        int index = className.lastIndexOf('.');
        String shortClassName = index == -1 ? className : className.substring(index + 1);
        return shortClassName + "." + method.getMethodName();
    }

    /**
     * Empty Constructor for utility class.
     */
    private RenderUtil()
    {
        // Empty Constructor for utility class
    }
}
