package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import javafx.application.Platform;
import org.slf4j.Logger;

public class CachingProfileListener implements ProfileListener {

    private final Logger logger;
    private final FlatViewModel flatModel;
    private final TreeViewModel treeModel;
    private final TraceCountViewModel countModel;
    private final ProfileFilter profileFilter;

    private Profile lastProfile;

    public CachingProfileListener(
            final Logger logger,
            final FlatViewModel flatModel,
            final TreeViewModel treeModel,
            final TraceCountViewModel countModel,
            final ProfileFilter profileFilter) {
        this.logger = logger;
        this.flatModel = flatModel;
        this.treeModel = treeModel;
        this.countModel = countModel;
        this.profileFilter = profileFilter;
    }

    @Override
    public void accept(Profile profile) {
        lastProfile = profile;

        profileFilter.accept(profile);

        // All UI updates must go through here.
        onFxThread(() -> {
            flatModel.accept(profile);
            treeModel.accept(profile);
            countModel.accept(profile);
        });
    }

    public void reflushLastProfile() {
        if (lastProfile != null) {
            accept(lastProfile);
        }
    }

    // ViewModel instances can happily update the UI
    // without worrying about threading implications
    private void onFxThread(final Runnable block) {
        try {
            if (Platform.isFxApplicationThread()) {
                block.run();
            } else {
                Platform.runLater(block);
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

}
