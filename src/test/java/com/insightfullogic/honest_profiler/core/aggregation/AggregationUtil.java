package com.insightfullogic.honest_profiler.core.aggregation;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.insightfullogic.honest_profiler.core.parser.Method;

public class AggregationUtil
{
    public static final long nano(int seconds)
    {
        return SECONDS.toNanos(seconds);
    }

    public static final String getFqmn(Method method)
    {
        StringBuilder result = new StringBuilder();
        result.append(method.getClassName()).append(".");
        result.append(method.getMethodName());
        return result.toString();
    }

    public static final String getFqmnPlusBci(Method method)
    {
        StringBuilder result = new StringBuilder(getFqmn(method));
        result.append(":").append(method.getBci());
        return result.toString();
    }

    public static final String getFqmnPlusLineNr(Method method)
    {
        StringBuilder result = new StringBuilder(getFqmn(method));
        result.append(":").append(method.getLine());
        return result.toString();
    }

    public static final String getMethodIdPlusFqmn(Method method)
    {
        StringBuilder result = new StringBuilder();
        result.append("(").append(method.getMethodId()).append(") ");
        result.append(getFqmn(method));
        return result.toString();
    }
}
