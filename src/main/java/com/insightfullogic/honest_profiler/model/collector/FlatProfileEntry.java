package com.insightfullogic.honest_profiler.model.collector;

import com.insightfullogic.honest_profiler.model.parser.Method;

public final class FlatProfileEntry {

    private final Method method;
    private final double timeShare;

    public FlatProfileEntry(Method method, double timeShare) {
        this.method = method;
        this.timeShare = timeShare;
    }

    public Method getMethod() {
        return method;
    }

    public double getTimeShare() {
        return timeShare;
    }

}
