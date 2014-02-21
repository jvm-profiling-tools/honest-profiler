package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.collector.ProfileListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class ProfileViewModel implements ProfileListener {

    private final ObservableList<FlatProfileEntry> flatProfile;

    public ProfileViewModel() {
        flatProfile = FXCollections.observableArrayList();
    }

    @Override
    public void accept(Profile profile) {
        flatProfile.clear();
        profile.flatProfile()
               .collect(toCollection(() -> flatProfile));
    }

    public ObservableList<FlatProfileEntry> getFlatProfile() {
        return flatProfile;
    }

}
