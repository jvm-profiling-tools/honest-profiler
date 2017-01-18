package com.insightfullogic.honest_profiler.core.profiles.lean;

import static java.math.BigInteger.ZERO;

import java.math.BigInteger;

public class NumericInfo
{
    private BigInteger selfTime;
    private BigInteger totalTime;

    private int selfCnt;
    private int totalCnt;

    /**
     * Constructor for an item which will be updated with non-self data.
     */
    public NumericInfo()
    {
        this(ZERO, ZERO, 0, 0);
    }

    /**
     * Constructor for a "final" item with self and total time known.
     *
     * @param selfNanos the self (and total) time
     */
    public NumericInfo(long selfNanos)
    {
        this(BigInteger.valueOf(selfNanos), BigInteger.valueOf(selfNanos), 1, 1);
    }

    /**
     * Copy constructor.
     *
     * @param source the source NumericInfo which is being copied
     */
    private NumericInfo(NumericInfo source)
    {
        this(source.selfTime, source.totalTime, source.selfCnt, source.totalCnt);
    }

    /**
     * Convenience internal constructor.
     *
     * @param selfTime the self time in ns
     * @param totalTime the total time in ns
     * @param selfCnt the self count
     * @param totalCnt the total count
     */
    private NumericInfo(BigInteger selfTime, BigInteger totalTime, int selfCnt, int totalCnt)
    {
        this.selfTime = selfTime;
        this.totalTime = totalTime;
        this.selfCnt = selfCnt;
        this.totalCnt = totalCnt;
    }

    public BigInteger getSelfTime()
    {
        return selfTime;
    }

    public BigInteger getTotalTime()
    {
        return totalTime;
    }

    public int getSelfCnt()
    {
        return selfCnt;
    }

    public int getTotalCnt()
    {
        return totalCnt;
    }

    /**
     * Update method for initial lean aggregation.
     *
     * @param nanos the number of ns spent in the stack
     * @param self a boolean indicating whether the associated frame is the last
     *            in the stack
     * @return this object
     */
    public NumericInfo update(long nanos, boolean self)
    {
        BigInteger converted = BigInteger.valueOf(nanos);

        totalTime = totalTime.add(converted);
        totalCnt++;

        if (self)
        {
            selfTime = selfTime.add(converted);
            selfCnt++;
        }

        return this;
    }

    public NumericInfo add(NumericInfo other)
    {
        selfTime = selfTime.add(other.selfTime);
        totalTime = totalTime.add(other.totalTime);
        selfCnt += other.selfCnt;
        totalCnt += other.totalCnt;
        return this;
    }

    public NumericInfo copy()
    {
        return new NumericInfo(this);
    }

    @Override
    public String toString()
    {
        return "data[" + selfTime + ":" + totalTime + ":" + selfCnt + ":" + totalCnt + "]";
    }
}
