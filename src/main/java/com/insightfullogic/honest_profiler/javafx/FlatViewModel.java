package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.collector.ProfileListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FlatViewModel implements ProfileListener {

    private final ObservableList<FlatProfileEntry> flatProfile;

    public FlatViewModel() {
        flatProfile = FXCollections.observableArrayList();
    }

    @Override
    public void accept(Profile profile) {
        flatProfile.clear();
        profile.flatProfile()
               .forEach(flatProfile::add);
    }

    public ObservableList<FlatProfileEntry> getFlatProfile() {
        return flatProfile;
    }

}
