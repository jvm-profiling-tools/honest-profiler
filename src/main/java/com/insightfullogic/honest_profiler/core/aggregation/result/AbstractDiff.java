package com.insightfullogic.honest_profiler.core.aggregation.result;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

public abstract class AbstractDiff<K, T extends Keyed<K>>
{
    private Aggregation<K, T> baseAggregation;
    private Aggregation<K, T> newAggregation;

    protected void setBaseAggregation(Aggregation<K, T> baseAggregation)
    {
        this.baseAggregation = baseAggregation;
    }

    protected void setNewAggregation(Aggregation<K, T> newAggregation)
    {
        this.newAggregation = newAggregation;
    }

    public LeanNode getBaseReference()
    {
        return baseAggregation.getReference();
    }

    public NumericInfo getBaseReferenceData()
    {
        return baseAggregation.getReferenceData();
    }

    public void setBaseReference(LeanNode reference)
    {
        baseAggregation.setReference(reference);
    }

    public LeanNode getNewReference()
    {
        return newAggregation.getReference();
    }

    public NumericInfo getNewReferenceData()
    {
        return newAggregation.getReferenceData();
    }

    public void setNewReference(LeanNode reference)
    {
        newAggregation.setReference(reference);
    }
}
