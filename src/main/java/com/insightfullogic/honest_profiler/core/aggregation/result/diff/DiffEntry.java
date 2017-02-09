package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;

/**
 * The aggregation item wrapping and providing the difference between two {@link Entry}s.
 */
public class DiffEntry implements Keyed<String>
{
    // Instance Properties

    private Entry baseEntry;
    private Entry newEntry;

    // Instance Constructors

    /**
     * Basic constructor specifying the {@link Entry}s being compared.
     *
     * @param baseEntry the Base {@link Entry}
     * @param newEntry the New {@link Entry}
     */
    public DiffEntry(Entry baseEntry, Entry newEntry)
    {
        this.baseEntry = baseEntry == null ? new Entry(newEntry.getKey(), null) : baseEntry;
        this.newEntry = newEntry == null ? new Entry(baseEntry.getKey(), null) : newEntry;
    }

    // Instance Accessors

    /**
     * Sets the Base {@link Entry}.
     *
     * The return value is provided as a convenience for
     * {@link FlatDiff#set(com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat, com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat)}.
     *
     * @param entry the Base {@link Entry}
     * @return this {@link DiffEntry}
     */
    public DiffEntry setBase(Entry entry)
    {
        baseEntry = entry;
        return this;
    }

    /**
     * Sets the New {@link Entry}.
     *
     * The return value is provided as a convenience for
     * {@link FlatDiff#set(com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat, com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat)}.
     *
     * @param entry the New {@link Entry}
     * @return this {@link DiffEntry}
     */
    public DiffEntry setNew(Entry entry)
    {
        newEntry = entry;
        return this;
    }

    /**
     * Returns the Base {@link Entry}.
     *
     * @return the Base {@link Entry}
     */
    public Entry getBaseEntry()
    {
        return baseEntry;
    }

    /**
     * Returns the New {@link Entry}.
     *
     * @return the New {@link Entry}
     */
    public Entry getNewEntry()
    {
        return newEntry;
    }

    @Override
    public String getKey()
    {
        return baseEntry == null ? newEntry.getKey() : baseEntry.getKey();
    }

    /**
     * @see Entry#getSelfTime()
     *
     * @return the Base aggregated self time in nanoseconds
     */
    public long getBaseSelfTime()
    {
        return baseEntry.getSelfTime();
    }

    /**
     * @see Entry#getTotalTime()
     *
     * @return the Base aggregated total time in nanoseconds
     */
    public long getBaseTotalTime()
    {
        return baseEntry.getTotalTime();
    }

    /**
     * @see Entry#getSelfCnt()
     *
     * @return the Base aggregated self sample count
     */
    public int getBaseSelfCnt()
    {
        return baseEntry.getSelfCnt();
    }

    /**
     * @see Entry#getTotalCnt()
     *
     * @return the Base aggregated total sample count
     */
    public int getBaseTotalCnt()
    {
        return baseEntry.getTotalCnt();
    }

    /**
     * @see Entry#getSelfTimePct()
     *
     * @return the Base self time divided by the Base reference total time
     */
    public double getBaseSelfTimePct()
    {
        return baseEntry.getSelfTimePct();
    }

    /**
     * @see Entry#getTotalTimePct()
     *
     * @return the Base total time divided by the Base reference total time
     */
    public double getBaseTotalTimePct()
    {
        return baseEntry.getTotalTimePct();
    }

    /**
     * @see Entry#getSelfCntPct()
     *
     * @return the Base self sample count divided by the Base reference total sample count
     */
    public double getBaseSelfCntPct()
    {
        return baseEntry.getSelfCntPct();
    }

    /**
     * @see Entry#getTotalCntPct()
     *
     * @return the Base total sample count divided by the Base reference total sample count
     */
    public double getBaseTotalCntPct()
    {
        return baseEntry.getTotalCntPct();
    }

    /**
     * @see Entry#getSelfTime()
     *
     * @return the New aggregated self time in nanoseconds
     */
    public long getNewSelfTime()
    {
        return newEntry.getSelfTime();
    }

    /**
     * @see Entry#getTotalTime()
     *
     * @return the New aggregated total time in nanoseconds
     */
    public long getNewTotalTime()
    {
        return newEntry.getTotalTime();
    }

    /**
     * @see Entry#getSelfCnt()
     *
     * @return the New aggregated self sample count
     */
    public int getNewSelfCnt()
    {
        return newEntry.getSelfCnt();
    }

    /**
     * @see Entry#getTotalCnt()
     *
     * @return the New aggregated total sample count
     */
    public int getNewTotalCnt()
    {
        return newEntry.getTotalCnt();
    }

    /**
     * @see Entry#getSelfTimePct()
     *
     * @return the New self time divided by the New reference total time
     */
    public double getNewSelfTimePct()
    {
        return newEntry.getSelfTimePct();
    }

    /**
     * @see Entry#getTotalTimePct()
     *
     * @return the New total time divided by the New reference total time
     */
    public double getNewTotalTimePct()
    {
        return newEntry.getTotalTimePct();
    }

    /**
     * @see Entry#getSelfCntPct()
     *
     * @return the New self sample count divided by the New reference total sample count
     */
    public double getNewSelfCntPct()
    {
        return newEntry.getSelfCntPct();
    }

    /**
     * @see Entry#getTotalCntPct()
     *
     * @return the New total sample count divided by the New reference total sample count
     */
    public double getNewTotalCntPct()
    {
        return newEntry.getTotalCntPct();
    }

    /**
     * @see Entry#getRefCnt()
     *
     * @return the Base reference total sample count
     */
    public int getBaseRefCnt()
    {
        return baseEntry.getRefCnt();
    }

    /**
     * @see Entry#getRefCnt()
     *
     * @return the New reference total sample count
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
