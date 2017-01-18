package com.insightfullogic.honest_profiler.core.aggregation.result;

import java.util.List;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

public class Aggregation<K, T extends Keyed<K>>
{
    private List<T> data;
    private LeanNode reference;

    public Aggregation(List<T> data, LeanNode reference)
    {
        super();
        this.data = data;
        this.reference = reference;
    }

    public LeanNode getReference()
    {
        return reference;
    }

    public NumericInfo getReferenceData()
    {
        return reference.getData();
    }

    public void setReference(LeanNode reference)
    {
        this.reference = reference;
    }

    public List<T> getData()
    {
        return data;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        System.err.println("Aggregation :");
        data.forEach(t -> System.err.println(t.toString()));
        return result.toString();
    }
}
