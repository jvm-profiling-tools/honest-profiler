package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.conductor.ProfileListener;

public class FakeProfileListener implements ProfileListener {

    private Profile profile;

    @Override
    public void accept(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

}
