package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Wrapper for {@link AggregatedEntry} which allows organizing them into a tree
 * structure.
 */
public class AggregatedNode
{
    private final AggregatedEntry entry;
    private final List<AggregatedNode> children;

    public AggregatedNode(AggregatedEntry entry)
    {
        this.entry = entry;
        this.children = new ArrayList<>();
    }

    public AggregatedEntry getEntry()
    {
        return entry;
    }

    public List<AggregatedNode> getChildren()
    {
        return children;
    }

    public String getKey()
    {
        return entry.getKey();
    }

    public NumericInfo getData()
    {
        return entry.getData();
    }

    public long getSelfTime()
    {
        return entry.getSelfTime();
    }

    public long getTotalTime()
    {
        return entry.getTotalTime();
    }

    public int getSelfCnt()
    {
        return entry.getSelfCnt();
    }

    public int getTotalCnt()
    {
        return entry.getTotalCnt();
    }

    public double getSelfTimePct()
    {
        return entry.getSelfTimePct();
    }

    public double getTotalTimePct()
    {
        return entry.getTotalTimePct();
    }

    public double getSelfCntPct()
    {
        return entry.getSelfCntPct();
    }

    public double getTotalCntPct()
    {
        return entry.getTotalCntPct();
    }

    public void addChild(AggregatedEntry entry)
    {
        children.add(new AggregatedNode(entry));
    }
}
