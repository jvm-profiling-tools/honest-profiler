package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.collector.Profile;

import java.util.concurrent.atomic.AtomicReference;

public class ProfileUpdateModerator extends Thread implements ProfileListener {

    private static final long UI_UPDATE_WINDOW_IN_MS = 1000;

    private final ProfileListener listener;

    private AtomicReference<Profile> incomingProfile = new AtomicReference<>();

    public ProfileUpdateModerator(ProfileListener listener) {
        this.listener = listener;
        setDaemon(true);
        setName(getClass().getSimpleName());
    }

    @Override
    public void run() {
        try {
            while (true) {
                final Profile profile = incomingProfile.get();
                if (profile != null) {
                    listener.accept(profile);
                    incomingProfile.compareAndSet(profile, null);
                }

                Thread.sleep(UI_UPDATE_WINDOW_IN_MS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void accept(Profile profile) {
        incomingProfile.set(profile);
    }

}
