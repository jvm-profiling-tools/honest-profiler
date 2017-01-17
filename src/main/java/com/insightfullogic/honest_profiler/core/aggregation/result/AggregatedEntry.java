package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.profiles.lean.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Lowest-level aggregation.
 */
public class AggregatedEntry
{

    private final Aggregation<?> aggregation;
    private String key;
    private final NumericInfo data;
    private final List<FrameInfo> frames;

    public AggregatedEntry(String key, Aggregation<?> aggregation)
    {
        this.key = key;
        this.data = new NumericInfo();
        this.aggregation = aggregation;
        this.frames = new ArrayList<>();
    }

    public AggregatedEntry(String key, NumericInfo data, Aggregation<?> aggregation)
    {
        this.key = key;
        this.data = data.copy();
        this.aggregation = aggregation;
        this.frames = new ArrayList<>();

    }

    public AggregatedEntry(String key,
                           NumericInfo data,
                           Aggregation<?> aggregation,
                           FrameInfo frame)
    {
        this(key, data, aggregation);
        frames.add(frame);
    }

    public Aggregation<?> getAggregation()
    {
        return aggregation;
    }

    public NumericInfo getReference()
    {
        return aggregation.getReferenceData();
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
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
        return data.getSelfTime().doubleValue() / getReference().getTotalTime().longValue();
    }

    public double getTotalTimePct()
    {
        return data.getTotalTime().doubleValue() / getReference().getTotalTime().longValue();
    }

    public double getSelfCntPct()
    {
        return data.getSelfCnt() / (double) getReference().getTotalCnt();
    }

    public double getTotalCntPct()
    {
        return data.getTotalCnt() / (double) getReference().getTotalCnt();
    }

    public int getRefCnt()
    {
        return getReference().getTotalCnt();
    }

    public void add(FrameInfo frame, NumericInfo newData)
    {
        frames.add(frame);
        data.add(newData);
    }

    public void combine(AggregatedEntry other)
    {
        frames.addAll(other.frames);
        data.add(other.data);
    }
}
