package com.insightfullogic.honest_profiler.core;

import com.insightfullogic.honest_profiler.core.collector.Profile;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class ProfileUpdateModerator extends Thread implements ProfileListener {

    private static final long UI_UPDATE_WINDOW_IN_MS = 1000;

    private final Logger logger;
    private final ProfileListener listener;
    private final AtomicReference<Profile> incomingProfile = new AtomicReference<>();

    public ProfileUpdateModerator(final Logger logger, final ProfileListener listener) {
        this.logger = logger;
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
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void accept(Profile profile) {
        incomingProfile.set(profile);
    }

}
