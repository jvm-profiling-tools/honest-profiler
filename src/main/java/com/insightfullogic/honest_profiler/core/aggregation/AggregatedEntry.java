package com.insightfullogic.honest_profiler.core.aggregation;

import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Lowest-level aggregation.
 */
public class AggregatedEntry
{

    private final String key;
    private final NumericInfo data;
    private final NumericInfo reference;

    public AggregatedEntry(String key, NumericInfo data, NumericInfo reference)
    {
        this.key = key;
        this.data = data.copy();
        this.reference = reference;
    }

    public String getKey()
    {
        return key;
    }

    public NumericInfo getData()
    {
        return data;
    }

    public long getSelfTime()
    {
        return data.getSelfTime().longValue();
    }

    public long getTotalTime()
    {
        return data.getTotalTime().longValue();
    }

    public int getSelfCnt()
    {
        return data.getSelfCnt();
    }

    public int getTotalCnt()
    {
        return data.getTotalCnt();
    }

    public double getSelfTimePct()
    {
        return data.getSelfTime().doubleValue() / reference.getTotalTime().longValue();
    }

    public double getTotalTimePct()
    {
        return data.getTotalTime().doubleValue() / reference.getTotalTime().longValue();
    }

    public double getSelfCntPct()
    {
        return data.getSelfCnt() / (double) reference.getTotalCnt();
    }

    public double getTotalCntPct()
    {
        return data.getTotalCnt() / (double) reference.getTotalCnt();
    }

    public void add(NumericInfo newData)
    {
        data.add(newData);
    }
}
