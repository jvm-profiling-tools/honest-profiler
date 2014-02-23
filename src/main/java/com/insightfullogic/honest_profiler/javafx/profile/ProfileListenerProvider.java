package com.insightfullogic.honest_profiler.javafx.profile;

import com.insightfullogic.honest_profiler.collector.ProfileListener;
import org.picocontainer.injectors.ProviderAdapter;

public class ProfileListenerProvider extends ProviderAdapter {

    public ProfileListener provide(FlatViewModel flatModel, TreeViewModel treeModel) {
        return profile -> {
            flatModel.accept(profile);
            treeModel.accept(profile);
        };
    }

}
