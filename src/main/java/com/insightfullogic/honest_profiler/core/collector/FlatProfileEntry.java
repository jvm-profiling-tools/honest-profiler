package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;

public final class FlatProfileEntry {

    private final Method method;
    private final double totalTimeShare;
    private final double selfTimeShare;

    public FlatProfileEntry(Method method, double totalTimeShare, double selfTimeShare) {
        this.method = method;
        this.totalTimeShare = totalTimeShare;
        this.selfTimeShare = selfTimeShare;
    }

    public Method getMethod() {
        return method;
    }

    public double getTotalTimeShare() {
        return totalTimeShare;
    }

    public double getSelfTimeShare() {
        return selfTimeShare;
    }
}
