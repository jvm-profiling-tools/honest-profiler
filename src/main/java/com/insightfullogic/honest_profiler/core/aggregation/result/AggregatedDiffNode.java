package com.insightfullogic.honest_profiler.core.aggregation.result;

import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Provides the difference between two {@link AggregatedEntry}s.
 */
public class AggregatedDiffNode
{
    private final AggregatedDiffEntry entry;

    public AggregatedDiffNode(AggregatedDiffEntry entry)
    {
        this.entry = entry;
    }

    public String getKey()
    {
        return entry.getKey();
    }

    public AggregatedEntry getBaseEntry()
    {
        return entry.getBaseEntry();
    }

    public AggregatedEntry getNewEntry()
    {
        return entry.getNewEntry();
    }

    public NumericInfo getBaseData()
    {
        return entry.getBaseData();
    }

    public long getBaseSelfTime()
    {
        return entry.getBaseSelfTime();
    }

    public long getBaseTotalTime()
    {
        return entry.getBaseTotalTime();
    }

    public int getBaseSelfCnt()
    {
        return entry.getBaseSelfCnt();
    }

    public int getBaseTotalCnt()
    {
        return entry.getBaseTotalCnt();
    }

    public double getBaseSelfTimePct()
    {
        return entry.getBaseSelfTimePct();
    }

    public double getBaseTotalTimePct()
    {
        return entry.getBaseTotalTimePct();
    }

    public double getBaseSelfCntPct()
    {
        return entry.getBaseSelfCntPct();
    }

    public double getBaseTotalCntPct()
    {
        return entry.getBaseTotalCntPct();
    }

    public int getBaseRefCnt()
    {
        return entry.getBaseRefCnt();
    }

    public NumericInfo getNewData()
    {
        return entry.getNewData();
    }

    public long getNewSelfTime()
    {
        return entry.getNewSelfTime();
    }

    public long getNewTotalTime()
    {
        return entry.getNewTotalTime();
    }

    public int getNewSelfCnt()
    {
        return entry.getNewSelfCnt();
    }

    public int getNewTotalCnt()
    {
        return entry.getNewTotalCnt();
    }

    public double getNewSelfTimePct()
    {
        return entry.getNewSelfTimePct();
    }

    public double getNewTotalTimePct()
    {
        return entry.getNewTotalTimePct();
    }

    public double getNewSelfCntPct()
    {
        return entry.getNewSelfCntPct();
    }

    public double getNewTotalCntPct()
    {
        return entry.getNewTotalCntPct();
    }

    public int getNewRefCnt()
    {
        return entry.getNewRefCnt();
    }

    public long getSelfTimeDiff()
    {
        return entry.getSelfTimeDiff();
    }

    public long getTotalTimeDiff()
    {
        return entry.getTotalTimeDiff();
    }

    public int getSelfCntDiff()
    {
        return entry.getSelfCntDiff();
    }

    public int getTotalCntDiff()
    {
        return entry.getTotalCntDiff();
    }

    public double getSelfTimePctDiff()
    {
        return entry.getSelfTimePctDiff();
    }

    public double getTotalTimePctDiff()
    {
        return entry.getTotalTimePctDiff();
    }

    public double getSelfCntPctDiff()
    {
        return entry.getSelfCntPctDiff();
    }

    public double getTotalCntPctDiff()
    {
        return entry.getTotalCntPctDiff();
    }

    public int getRefCntDiff()
    {
        return entry.getRefCntDiff();
    }
}
