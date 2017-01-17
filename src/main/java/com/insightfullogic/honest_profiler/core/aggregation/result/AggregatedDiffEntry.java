package com.insightfullogic.honest_profiler.core.aggregation.result;

import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Provides the difference between two {@link AggregatedEntry}s.
 */
public class AggregatedDiffEntry
{
    private final AggregatedEntry baseEntry;
    private final AggregatedEntry newEntry;

    public AggregatedDiffEntry(AggregatedEntry baseEntry, AggregatedEntry newEntry)
    {
        this.baseEntry = baseEntry;
        this.newEntry = newEntry;
    }

    public AggregatedEntry getBaseEntry()
    {
        return baseEntry;
    }

    public AggregatedEntry getNewEntry()
    {
        return newEntry;
    }

    public NumericInfo getBaseData()
    {
        return baseEntry.getData();
    }

    public long getBaseSelfTime()
    {
        return baseEntry.getSelfTime();
    }

    public long getBaseTotalTime()
    {
        return baseEntry.getTotalTime();
    }

    public int getBaseSelfCnt()
    {
        return baseEntry.getSelfCnt();
    }

    public int getBaseTotalCnt()
    {
        return baseEntry.getTotalCnt();
    }

    public double getBaseSelfTimePct()
    {
        return baseEntry.getSelfTimePct();
    }

    public double getBaseTotalTimePct()
    {
        return baseEntry.getTotalTimePct();
    }

    public double getBaseSelfCntPct()
    {
        return baseEntry.getSelfCntPct();
    }

    public double getBaseTotalCntPct()
    {
        return baseEntry.getTotalCntPct();
    }

    public NumericInfo getNewData()
    {
        return newEntry.getData();
    }

    public long getNewSelfTime()
    {
        return newEntry.getSelfTime();
    }

    public long getNewTotalTime()
    {
        return newEntry.getTotalTime();
    }

    public int getNewSelfCnt()
    {
        return newEntry.getSelfCnt();
    }

    public int getNewTotalCnt()
    {
        return newEntry.getTotalCnt();
    }

    public double getNewSelfTimePct()
    {
        return newEntry.getSelfTimePct();
    }

    public double getNewTotalTimePct()
    {
        return newEntry.getTotalTimePct();
    }

    public double getNewSelfCntPct()
    {
        return newEntry.getSelfCntPct();
    }

    public double getNewTotalCntPct()
    {
        return newEntry.getTotalCntPct();
    }

    public long getSelfTimeDiff()
    {
        return getNewSelfTime() - getBaseSelfTime();
    }

    public long getTotalTimeDiff()
    {
        return getNewTotalTime() - getBaseTotalTime();
    }

    public int getSelfCntDiff()
    {
        return getNewSelfCnt() - getBaseSelfCnt();
    }

    public int getTotalCntDiff()
    {
        return getNewTotalCnt() - getBaseTotalCnt();
    }

    public double getSelfTimePctDiff()
    {
        return getNewSelfTimePct() - getBaseSelfTimePct();
    }

    public double getTotalTimePctDiff()
    {
        return getNewTotalTimePct() - getBaseTotalTimePct();
    }

    public double getSelfCntPctDiff()
    {
        return getNewSelfCntPct() - getBaseSelfCntPct();
    }

    public double getTotalCntPctDiff()
    {
        return getNewTotalCntPct() - getBaseTotalCntPct();
    }
}
