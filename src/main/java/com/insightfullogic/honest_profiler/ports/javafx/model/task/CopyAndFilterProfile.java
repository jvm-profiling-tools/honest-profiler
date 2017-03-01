package com.insightfullogic.honest_profiler.ports.javafx.model.task;

import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.core.profiles.Profile;

import javafx.concurrent.Task;

public class CopyAndFilterProfile extends Task<Profile>
{
    private final Profile originalProfile;
    private final ProfileFilter filter;

    public CopyAndFilterProfile(Profile originalProfile, ProfileFilter filter)
    {
        super();
        this.originalProfile = originalProfile;
        this.filter = filter;
    }

    @Override
    protected Profile call() throws Exception
    {
        Profile newProfile = originalProfile.copy();
        filter.accept(newProfile);
        return newProfile;
    }
}
