package com.insightfullogic.honest_profiler.collector;

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
