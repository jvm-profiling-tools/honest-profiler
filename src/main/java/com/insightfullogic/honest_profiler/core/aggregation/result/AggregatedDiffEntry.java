package com.insightfullogic.honest_profiler.core.aggregation.result;

import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Provides the difference between two {@link AggregatedEntry}s.
 */
public class AggregatedDiffEntry<K> implements Keyed<K>
{
    private AggregatedEntry<K> baseEntry;
    private AggregatedEntry<K> newEntry;

    public AggregatedDiffEntry(AggregatedEntry<K> baseEntry, AggregatedEntry<K> newEntry)
    {
        this.baseEntry = baseEntry == null ? new AggregatedEntry<K>(newEntry.getKey(), null)
            : baseEntry;
        this.newEntry = newEntry == null ? new AggregatedEntry<K>(baseEntry.getKey(), null)
            : newEntry;
    }

    public AggregatedDiffEntry<K> setBase(AggregatedEntry<K> entry)
    {
        baseEntry = entry;
        return this;
    }

    public AggregatedDiffEntry<K> setNew(AggregatedEntry<K> entry)
    {
        newEntry = entry;
        return this;
    }

    public void addBase(NumericInfo data)
    {
        baseEntry.getData().add(data);
    }

    public void addNew(NumericInfo data)
    {
        newEntry.getData().add(data);
    }

    public AggregatedEntry<K> getBaseEntry()
    {
        return baseEntry;
    }

    public AggregatedEntry<K> getNewEntry()
    {
        return newEntry;
    }

    @Override
    public K getKey()
    {
        return baseEntry == null ? newEntry.getKey() : baseEntry.getKey();
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

    public int getBaseRefCnt()
    {
        return baseEntry.getRefCnt();
    }

    public int getNewRefCnt()
    {
        return newEntry.getRefCnt();
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

    public int getRefCntDiff()
    {
        return getNewRefCnt() - getBaseRefCnt();
    }
}
