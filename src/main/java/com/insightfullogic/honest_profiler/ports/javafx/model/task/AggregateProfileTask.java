package com.insightfullogic.honest_profiler.ports.javafx.model.task;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.concurrent.Task;

/**
 * Background task which aggregates a back-end {@link LeanProfile} into an {@link AggregationProfile} for the front-end.
 * <p>
 * It also serves the important function of decoupling the back-end thread which invokes the {@link LeanProfileListener}
 * accept(LeanProfile) method from the front-end threads : the task will be submitted for processing on a worker thread
 * by the back-end thread. When the task finishes, it invokes its succeeded() method on the FX thread.
 * </p>
 */
public class AggregateProfileTask extends Task<AggregationProfile>
{
    // Instance Properties

    private ProfileContext context;
    private LeanProfile leanProfile;

    // Instance Constructors

    /**
     * Constructor which specifies the {@link ProfileContext} which will receive the resulting
     * {@link AggregationProfile}, and the {@link LeanProfile} being aggregated.
     * <p>
     * @param context the {@link ProfileContext} which will receive the resulting {@link AggregationProfile}
     * @param leanProfile the {@link LeanProfile} being aggregated
     */
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

    // Guaranteed to be called on the FX thread.
    @Override
    protected void succeeded()
    {
        super.succeeded();
        context.update(this.getValue());
    }

    // Guaranteed to be called on the FX thread.
    @Override
    protected void failed()
    {
        super.failed();
        getException().printStackTrace();
    }
}
