package com.insightfullogic.honest_profiler.ports.javafx.model.task;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.concurrent.Task;

public class AggregateProfileTask extends Task<AggregationProfile>
{
    private ProfileContext context;
    private LeanProfile leanProfile;

    public AggregateProfileTask(ProfileContext context, LeanProfile leanProfile)
    {
        super();
        this.context = context;
        this.leanProfile = leanProfile;
    }

    @Override
    protected AggregationProfile call() throws Exception
    {
        return new AggregationProfile(leanProfile);
    }

    @Override
    protected void succeeded()
    {
        super.succeeded();
        context.update(this.getValue());
    }

    @Override
    protected void failed()
    {
        super.failed();
        getException().printStackTrace();
    }
}
