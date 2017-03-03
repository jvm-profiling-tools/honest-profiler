package com.insightfullogic.honest_profiler.core.profiles.lean.info;

import static java.math.BigInteger.ZERO;

import java.math.BigInteger;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;

/**
 * NumericInfo collects the four basic amounts tracked by the profiles and {@link Aggregation}s
 * <ul>
 * <li>Self Time, which is the estimated amount of time in nanoseconds spent executing a method (or an aggregated set of
 * methods), excluding the amount of time spent in methods (in)directly called by the method(s)</li>
 * <li>Total Time, which is the estimated amount of time in nanoseconds spent executing a method (or an aggregated set
 * of methods), including the amount of time spent in methods (in)directly called by the method(s)</li>
 * <li>Self Sample Count, which is the number of sample stack traces in which the code from a method (or an aggregated
 * set of methods) was seen as being executed</li>
 * <li>Total Sample Count, which is the number of sample stack traces in which the code from a method (or an aggregated
 * set of methods) or a method called (in)directly by the method(s) was seen as being executed</li>
 * </ul>
 * <p>
 * The class also provides aggregation methods which aggregate a sample or other NumericInfo into this one.
 */
public class NumericInfo
{
    // Instance Properties

    private BigInteger selfTime;
    private BigInteger totalTime;

    private int selfCnt;
    private int totalCnt;

    // Instance Constructors

    /**
     * Constructor for an accumulator item, which will be updated while aggregating.
     */
    public NumericInfo()
    {
        this(ZERO, ZERO, 0, 0);
    }

    /**
     * Copy constructor.
     * <p>
     * @param source the source NumericInfo which is being copied
     */
    private NumericInfo(NumericInfo source)
    {
        this(source.selfTime, source.totalTime, source.selfCnt, source.totalCnt);
    }

    /**
     * Convenience internal constructor.
     * <p>
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

    // Instance Accessors

    /**
     * Returns the self time for the method or aggregated set of methods.
     * <p>
     * @return the self time for the method or aggregated set of methods
     */
    public BigInteger getSelfTime()
    {
        return selfTime;
    }

    /**
     * Returns the total time for the method or aggregated set of methods.
     * <p>
     * @return the total time for the method or aggregated set of methods
     */
    public BigInteger getTotalTime()
    {
        return totalTime;
    }

    /**
     * Returns the self sample count for the method or aggregated set of methods.
     * <p>
     * @return the self sample count for the method or aggregated set of methods
     */
    public int getSelfCnt()
    {
        return selfCnt;
    }

    /**
     * Returns the total sample count for the method or aggregated set of methods.
     * <p>
     * @return the total sample count for the method or aggregated set of methods
     */
    public int getTotalCnt()
    {
        return totalCnt;
    }

    // Aggregation Methods

    /**
     * Aggregation method for initial lean aggregation, used when the info from a new {@link StackFrame} is aggregated
     * into the info from an existing one.
     * <p>
     * The nanoseconds is added to total time, and if self is true, to self time as well. The total sample count is
     * incremented, and if self is true, the self sample count as well.
     * <p>
     * @param nanos the number of nanoseconds spent in the stack
     * @param self a boolean indicating whether the associated frame is the last in the stack
     * @return this object
     */
    public NumericInfo add(long nanos, boolean self)
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

    /**
     * Aggregation method for aggregating another @link NumericInfo object into this one. The corresponding values are
     * added together.
     * <p>
     * @param other the NumericInfo object to be aggregated into this one
     * @return this object
     */
    public NumericInfo add(NumericInfo other)
    {
        selfTime = selfTime.add(other.selfTime);
        totalTime = totalTime.add(other.totalTime);
        selfCnt += other.selfCnt;
        totalCnt += other.totalCnt;
        return this;
    }

    // Copy Methods

    /**
     * Returns a copy of this object.
     * <p>
     * @return a copy of this object
     */
    public NumericInfo copy()
    {
        return new NumericInfo(this);
    }

    // Object Implementation

    @Override
    public String toString()
    {
        return "data[" + selfTime + ":" + totalTime + ":" + selfCnt + ":" + totalCnt + "]";
    }
}
