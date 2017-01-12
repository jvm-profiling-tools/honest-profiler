package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.math.BigInteger;

public class NumericInfo
{
    private BigInteger nanosSpent;
    private int selfCnt;
    private int totalCnt;

    public NumericInfo(long nanos)
    {
        this(BigInteger.valueOf(nanos), 1, 1);
    }

    public NumericInfo(BigInteger nanosSpent, int selfCnt, int totalCnt)
    {
        this.nanosSpent = nanosSpent;
        this.selfCnt = selfCnt;
        this.totalCnt = totalCnt;
    }

    public NumericInfo update(long nanos, boolean self)
    {
        nanosSpent = nanosSpent.add(BigInteger.valueOf(nanos));
        totalCnt++;
        if (self)
        {
            selfCnt++;
        }
        return this;
    }

    public NumericInfo copy()
    {
        return new NumericInfo(nanosSpent, selfCnt, totalCnt);
    }
}
