package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.collector.Profile;

public class ProfileUpdateModerator extends Thread implements ProfileListener {

    private static final long UI_UPDATE_WINDOW_IN_MS = 1000;

    private final ProfileListener listener;

    private volatile Profile newProfile = null;

    public ProfileUpdateModerator(ProfileListener listener) {
        this.listener = listener;
        setDaemon(true);
        setName(getClass().getSimpleName());
    }

    @Override
    public void run() {
        Profile lastProfile = null;
        while (true) {
            Profile newProfile = this.newProfile;
            if (hasUpdatedProfile(newProfile, lastProfile)) {
                listener.accept(newProfile);
                lastProfile = newProfile;
            }

            try {
                Thread.sleep(UI_UPDATE_WINDOW_IN_MS);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private boolean hasUpdatedProfile(Profile newProfile, Profile lastProfile) {
        return newProfile != lastProfile && newProfile != null;
    }

    @Override
    public void accept(Profile profile) {
        newProfile = profile;
    }

}
