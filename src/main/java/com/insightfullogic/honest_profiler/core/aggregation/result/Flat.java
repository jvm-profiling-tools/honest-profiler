package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.List;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

public class Flat<K> extends Aggregation<K, Entry<K>>
{
    public Flat(List<Entry<K>> data, LeanNode reference)
    {
        super(data, reference);
    }

    @Override
    public List<Entry<K>> getData()
    {
        return super.getData();
    }
}
