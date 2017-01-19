package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Provides the difference between two {@link Entry}s.
 */
public class DiffEntry<K> implements Keyed<K>
{
    private Entry<K> baseEntry;
    private Entry<K> newEntry;

    public DiffEntry(Entry<K> baseEntry, Entry<K> newEntry)
    {
        this.baseEntry = baseEntry == null ? new Entry<K>(newEntry.getKey(), null) : baseEntry;
        this.newEntry = newEntry == null ? new Entry<K>(baseEntry.getKey(), null) : newEntry;
    }

    public DiffEntry<K> setBase(Entry<K> entry)
    {
        baseEntry = entry;
        return this;
    }

    public DiffEntry<K> setNew(Entry<K> entry)
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

    public Entry<K> getBaseEntry()
    {
        return baseEntry;
    }

    public Entry<K> getNewEntry()
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
