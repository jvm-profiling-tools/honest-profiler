package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Provides the difference between two {@link AggregatedEntry}s.
 */
public class AggregatedDiffNode<K> implements Keyed<K>
{
    private final AggregatedDiffEntry<K> entry;
    private final Map<K, AggregatedDiffNode<K>> children;

    public AggregatedDiffNode(AggregatedDiffEntry<K> entry)
    {
        this.entry = entry;
        this.children = new HashMap<>();
    }

    public AggregatedDiffNode(AggregatedNode<K> baseNode, AggregatedNode<K> newNode)
    {
        this.entry = new AggregatedDiffEntry<>(
            baseNode == null ? null : baseNode.getEntry(),
            newNode == null ? null : newNode.getEntry());
        this.children = new HashMap<>();
        addBaseChildren(baseNode);
        addNewChildren(newNode);
    }

    @Override
    public K getKey()
    {
        return entry.getKey();
    }

    public AggregatedDiffEntry<K> getDiffEntry()
    {
        return entry;
    }

    public AggregatedEntry<K> getBaseEntry()
    {
        return entry.getBaseEntry();
    }

    public AggregatedEntry<K> getNewEntry()
    {
        return entry.getNewEntry();
    }

    public AggregatedDiffNode<K> setBase(AggregatedNode<K> node)
    {
        entry.setBase(node.getEntry());
        addBaseChildren(node);
        return this;
    }

    public AggregatedDiffNode<K> setNew(AggregatedNode<K> node)
    {
        entry.setNew(node.getEntry());
        addNewChildren(node);
        return this;
    }

    public Collection<AggregatedDiffNode<K>> getChildren()
    {
        return children.values();
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

    private void addBaseChildren(AggregatedNode<K> node)
    {
        if (node == null)
        {
            return;
        }
        node.getChildren().forEach(this::addBaseChild);
    }

    private void addNewChildren(AggregatedNode<K> node)
    {
        if (node == null)
        {
            return;
        }
        node.getChildren().forEach(this::addNewChild);
    }

    private void addBaseChild(AggregatedNode<K> child)
    {
        children.compute(
            child.getKey(),
            (k, v) -> v == null ? new AggregatedDiffNode<>(child, null) : v.setBase(child));
    }

    private void addNewChild(AggregatedNode<K> child)
    {
        children.compute(
            child.getKey(),
            (k, v) -> v == null ? new AggregatedDiffNode<>(null, child) : v.setNew(child));
    }
}
