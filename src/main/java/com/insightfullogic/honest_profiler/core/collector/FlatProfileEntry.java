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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlatProfileEntry that = (FlatProfileEntry) o;

        if (Double.compare(that.selfTimeShare, selfTimeShare) != 0) return false;
        if (Double.compare(that.totalTimeShare, totalTimeShare) != 0) return false;
        if (method != null ? !method.equals(that.method) : that.method != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = method != null ? method.hashCode() : 0;
        temp = Double.doubleToLongBits(totalTimeShare);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(selfTimeShare);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
