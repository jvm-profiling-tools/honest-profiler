package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * The aggregation item wrapping and providing the difference between two {@link Entry}s.
 */
public class DiffEntry<K> implements Keyed<K>
{
    // Instance Properties

    private Entry<K> baseEntry;
    private Entry<K> newEntry;

    // Instance Constructors

    /**
     * Basic constructor specifying the {@link Entry}s being compared.
     *
     * @param baseEntry the Base {@link Entry}
     * @param newEntry the New {@link Entry}
     */
    public DiffEntry(Entry<K> baseEntry, Entry<K> newEntry)
    {
        this.baseEntry = baseEntry == null ? new Entry<K>(newEntry.getKey(), null) : baseEntry;
        this.newEntry = newEntry == null ? new Entry<K>(baseEntry.getKey(), null) : newEntry;
    }

    // Instance Accessors

    /**
     * Sets the Base {@link Entry}.
     *
     * The return value is provided as a convenience for
     * {@link FlatDiff#setBase(com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat)}.
     *
     * @param entry the Base {@link Entry}
     * @return this {@link DiffEntry}
     */
    public DiffEntry<K> setBase(Entry<K> entry)
    {
        baseEntry = entry;
        return this;
    }

    /**
     * Sets the New {@link Entry}.
     *
     * The return value is provided as a convenience for
     * {@link FlatDiff#setNew(com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat)}.
     *
     * @param entry the New {@link Entry}
     * @return this {@link DiffEntry}
     */
    public DiffEntry<K> setNew(Entry<K> entry)
    {
        newEntry = entry;
        return this;
    }

    /**
     * Aggregate the specified {@link NumericInfo} into the Base {@link Entry}, i.e. combine the numeric info in the
     * Base {@link Entry} and the provided information by replacing the {@link Entry} information with the sum.
     *
     * @param data the data to be aggregated
     */
    public void addBase(NumericInfo data)
    {
        baseEntry.getData().add(data);
    }

    /**
     * Aggregate the specified {@link NumericInfo} into the New {@link Entry}, i.e. combine the numeric info in the New
     * {@link Entry} and the provided information by replacing the {@link Entry} information with the sum.
     *
     * @param data the data to be aggregated
     */
    public void addNew(NumericInfo data)
    {
        newEntry.getData().add(data);
    }

    /**
     * Returns the Base {@link Entry}.
     *
     * @return the Base {@link Entry}
     */
    public Entry<K> getBaseEntry()
    {
        return baseEntry;
    }

    /**
     * Returns the New {@link Entry}.
     *
     * @return the New {@link Entry}
     */
    public Entry<K> getNewEntry()
    {
        return newEntry;
    }

    @Override
    public K getKey()
    {
        return baseEntry == null ? newEntry.getKey() : baseEntry.getKey();
    }

    /**
     * @see Entry#getData()
     */
    public NumericInfo getBaseData()
    {
        return baseEntry.getData();
    }

    /**
     * @see Entry#getSelfTime()
     */
    public long getBaseSelfTime()
    {
        return baseEntry.getSelfTime();
    }

    /**
     * @see Entry#getTotalTime()
     */
    public long getBaseTotalTime()
    {
        return baseEntry.getTotalTime();
    }

    /**
     * @see Entry#getSelfCnt()
     */
    public int getBaseSelfCnt()
    {
        return baseEntry.getSelfCnt();
    }

    /**
     * @see Entry#getTotalCnt()
     */
    public int getBaseTotalCnt()
    {
        return baseEntry.getTotalCnt();
    }

    /**
     * @see Entry#getSelfTimePct()
     */
    public double getBaseSelfTimePct()
    {
        return baseEntry.getSelfTimePct();
    }

    /**
     * @see Entry#getTotalTimePct()
     */
    public double getBaseTotalTimePct()
    {
        return baseEntry.getTotalTimePct();
    }

    /**
     * @see Entry#getSelfCntPct()
     */
    public double getBaseSelfCntPct()
    {
        return baseEntry.getSelfCntPct();
    }

    /**
     * @see Entry#getTotalCntPct()
     */
    public double getBaseTotalCntPct()
    {
        return baseEntry.getTotalCntPct();
    }

    /**
     * @see Entry#getData()
     */
    public NumericInfo getNewData()
    {
        return newEntry.getData();
    }

    /**
     * @see Entry#getSelfTime()()
     */
    public long getNewSelfTime()
    {
        return newEntry.getSelfTime();
    }

    /**
     * @see Entry#getTotalTime()
     */
    public long getNewTotalTime()
    {
        return newEntry.getTotalTime();
    }

    /**
     * @see Entry#getSelfCnt()
     */
    public int getNewSelfCnt()
    {
        return newEntry.getSelfCnt();
    }

    /**
     * @see Entry#getTotalCnt()
     */
    public int getNewTotalCnt()
    {
        return newEntry.getTotalCnt();
    }

    /**
     * @see Entry#getSelfTimePct()
     */
    public double getNewSelfTimePct()
    {
        return newEntry.getSelfTimePct();
    }

    /**
     * @see Entry#getTotalTimePct()
     */
    public double getNewTotalTimePct()
    {
        return newEntry.getTotalTimePct();
    }

    /**
     * @see Entry#getSelfCntPct()
     */
    public double getNewSelfCntPct()
    {
        return newEntry.getSelfCntPct();
    }

    /**
     * @see Entry#getTotalCntPct()
     */
    public double getNewTotalCntPct()
    {
        return newEntry.getTotalCntPct();
    }

    /**
     * @see Entry#getRefCnt()
     */
    public int getBaseRefCnt()
    {
        return baseEntry.getRefCnt();
    }

    /**
     * @see Entry#getRefCnt()
     */
    public int getNewRefCnt()
    {
        return newEntry.getRefCnt();
    }

    /**
     * Returns the difference between the Self Time of the New and Base {@link Entry}s.
     *
     * @return the difference between the Self Time of the New and Base {@link Entry}s
     */
    public long getSelfTimeDiff()
    {
        return getNewSelfTime() - getBaseSelfTime();
    }

    /**
     * Returns the difference between the Total Time of the New and Base {@link Entry}s.
     *
     * @return the difference between the Total Time of the New and Base {@link Entry}s
     */
    public long getTotalTimeDiff()
    {
        return getNewTotalTime() - getBaseTotalTime();
    }

    /**
     * Returns the difference between the Self Count of the New and Base {@link Entry}s.
     *
     * @return the difference between the Self Count of the New and Base {@link Entry}s
     */
    public int getSelfCntDiff()
    {
        return getNewSelfCnt() - getBaseSelfCnt();
    }

    /**
     * Returns the difference between the Total Count of the New and Base {@link Entry}s.
     *
     * @return the difference between the Total Count of the New and Base {@link Entry}s
     */
    public int getTotalCntDiff()
    {
        return getNewTotalCnt() - getBaseTotalCnt();
    }

    /**
     * Returns the difference between the Self Time % of the New and Base {@link Entry}s.
     *
     * @return the difference between the Self Time % of the New and Base {@link Entry}s
     */
    public double getSelfTimePctDiff()
    {
        return getNewSelfTimePct() - getBaseSelfTimePct();
    }

    /**
     * Returns the difference between the Total Time % of the New and Base {@link Entry}s.
     *
     * @return the difference between the Total Time % of the New and Base {@link Entry}s
     */
    public double getTotalTimePctDiff()
    {
        return getNewTotalTimePct() - getBaseTotalTimePct();
    }

    /**
     * Returns the difference between the Self Count % of the New and Base {@link Entry}s.
     *
     * @return the difference between the Self Count % of the New and Base {@link Entry}s
     */
    public double getSelfCntPctDiff()
    {
        return getNewSelfCntPct() - getBaseSelfCntPct();
    }

    /**
     * Returns the difference between the Total Count % of the New and Base {@link Entry}s.
     *
     * @return the difference between the Total Count % of the New and Base {@link Entry}s
     */
    public double getTotalCntPctDiff()
    {
        return getNewTotalCntPct() - getBaseTotalCntPct();
    }

    /**
     * Returns the difference between the Total Count of the New and Base {@link Entry} references.
     *
     * @return the difference between the Total Count of the New and Base {@link Entry} references
     */
    public int getRefCntDiff()
    {
        return getNewRefCnt() - getBaseRefCnt();
    }
}
