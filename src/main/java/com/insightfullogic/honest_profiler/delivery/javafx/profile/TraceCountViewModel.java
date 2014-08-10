package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TraceCountViewModel implements ProfileListener {

    @FXML
    private Label traceCount;

    /**
     * Not threadsafe: must be run on JavaFx thread.
     */
    @Override
    public void accept(Profile profile) {
        traceCount.setText(profile.getTraceCount() + " samples");
    }
}
