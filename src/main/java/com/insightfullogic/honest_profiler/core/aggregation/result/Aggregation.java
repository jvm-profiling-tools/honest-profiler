package com.insightfullogic.honest_profiler.core.aggregation.result;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

public class Aggregation<T>
{
    private T data;
    private LeanNode reference;

    public Aggregation(T data, LeanNode reference)
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

    public T getData()
    {
        return data;
    }
}
