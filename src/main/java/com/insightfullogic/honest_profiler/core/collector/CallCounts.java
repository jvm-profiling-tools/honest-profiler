package com.insightfullogic.honest_profiler.core.collector;

public class CallCounts {

    public int timeAppeared;
    public int timeInvokingThis;

    public CallCounts(final int timeAppeared, final int timeInvokingThis) {
        this.timeAppeared = timeAppeared;
        this.timeInvokingThis = timeInvokingThis;
    }
}
