package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TraceCountViewModel implements ProfileListener {

    @FXML
    private Label traceCount;

    @Override
    public void accept(Profile profile) {
        Platform.runLater(() -> traceCount.setText(profile.getTraceCount() + " samples"));
    }
}
