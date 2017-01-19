package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.profiles.lean.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Lowest-level aggregation.
 */
public class Entry<K> implements Keyed<K>
{

    private final Aggregation<K, ? extends Keyed<K>> aggregation;
    private K key;
    private final NumericInfo data;
    private final List<FrameInfo> frames;

    public <T extends Keyed<K>> Entry(K key, Aggregation<K, T> aggregation)
    {
        this.key = key;
        this.data = new NumericInfo();
        this.aggregation = aggregation;
        this.frames = new ArrayList<>();
    }

    public <T extends Keyed<K>> Entry(K key,
                                                NumericInfo data,
                                                Aggregation<K, T> aggregation)
    {
        this.key = key;
        this.data = data.copy();
        this.aggregation = aggregation;
        this.frames = new ArrayList<>();

    }

    public <T extends Keyed<K>> Entry(K key,
                                                NumericInfo data,
                                                Aggregation<K, T> aggregation,
                                                FrameInfo frame)
    {
        this(key, data, aggregation);
        frames.add(frame);
    }

    @SuppressWarnings("unchecked")
    public <T extends Keyed<K>> Aggregation<K, T> getAggregation()
    {
        return (Aggregation<K, T>) aggregation;
    }

    public NumericInfo getReference()
    {
        return aggregation.getReferenceData();
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

    public List<FrameInfo> getFrames()
    {
        return frames;
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
            : data.getSelfTime().doubleValue() / getReference().getTotalTime().longValue();
    }

    public double getTotalTimePct()
    {
        return aggregation == null ? 0
            : data.getTotalTime().doubleValue() / getReference().getTotalTime().longValue();
    }

    public double getSelfCntPct()
    {
        return aggregation == null ? 0 : data.getSelfCnt() / (double) getReference().getTotalCnt();
    }

    public double getTotalCntPct()
    {
        return aggregation == null ? 0 : data.getTotalCnt() / (double) getReference().getTotalCnt();
    }

    public int getRefCnt()
    {
        return aggregation == null ? 0 : getReference().getTotalCnt();
    }

    public void add(FrameInfo frame, NumericInfo newData)
    {
        frames.add(frame);
        data.add(newData);
    }

    public void combine(Entry<K> other)
    {
        frames.addAll(other.frames);
        data.add(other.data);
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
