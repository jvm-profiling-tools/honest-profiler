package com.insightfullogic.honest_profiler.delivery.web;

import com.insightfullogic.honest_profiler.core.collector.Profile;

public class NewProfileMessage {

    private final String id;
    private final Profile profile;

    public NewProfileMessage(String id, Profile profile) {
        this.id = id;
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getType() {
        return "newProfile";
    }

    public String getId() {
        return id;
    }

}
