package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Lowest-level aggregation.
 */
public class Entry<K> implements Keyed<K>
{

    private Aggregation<K, ? extends Keyed<K>> aggregation;
    private K key;
    private NumericInfo data;
    private List<LeanNode> aggregatedNodes;
    private NumericInfo reference;

    public <T extends Keyed<K>> Entry(Aggregation<K, T> aggregation)
    {
        this.data = new NumericInfo();
        this.aggregation = aggregation;
        this.aggregatedNodes = new ArrayList<>();
    }

    public <T extends Keyed<K>> Entry(K key, Aggregation<K, T> aggregation)
    {
        this(aggregation);
        this.key = key;
    }

    public <T extends Keyed<K>> Entry(K key, NumericInfo data, Aggregation<K, T> aggregation)
    {
        this.key = key;
        this.data = data.copy();
        this.aggregation = aggregation;
        this.aggregatedNodes = new ArrayList<>();

    }

    @SuppressWarnings("unchecked")
    public <T extends Keyed<K>> Aggregation<K, T> getAggregation()
    {
        return (Aggregation<K, T>)aggregation;
    }

    public void setReference(NumericInfo reference)
    {
        this.reference = reference;
    }

    @Override
    public K getKey()
    {
        return key;
    }

    public void setKey(K key)
    {
        this.key = key;
    }

    public NumericInfo getData()
    {
        return data;
    }

    public List<LeanNode> getAggregatedNodes()
    {
        return this.aggregatedNodes;
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
        return aggregation == null ? 0
            : data.getSelfTime().doubleValue() / reference.getTotalTime().longValue();
    }

    public double getTotalTimePct()
    {
        return aggregation == null ? 0
            : data.getTotalTime().doubleValue() / reference.getTotalTime().longValue();
    }

    public double getSelfCntPct()
    {
        return aggregation == null ? 0 : data.getSelfCnt() / (double)reference.getTotalCnt();
    }

    public double getTotalCntPct()
    {
        return aggregation == null ? 0 : data.getTotalCnt() / (double)reference.getTotalCnt();
    }

    public int getRefCnt()
    {
        return aggregation == null ? 0 : reference.getTotalCnt();
    }

    public void add(LeanNode node)
    {
        aggregatedNodes.add(node);
        data.add(node.getData());
    }

    protected void copyInto(Entry<K> other)
    {
        other.aggregation = aggregation;
        other.key = key;
        other.data = data.copy();
        other.aggregatedNodes = new ArrayList<>(aggregatedNodes);
        other.reference = reference;
    }

    public Entry<K> combine(Entry<K> other)
    {
        key = other.key;
        aggregatedNodes.addAll(other.aggregatedNodes);
        data.add(other.data);
        reference = other.reference;
        return this;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("entry[");
        result.append(key);
        result.append(":");
        result.append(data);
        result.append("]");
        return result.toString();
    }
}
