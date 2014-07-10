package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.core.conductor.ProfileListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TraceCountViewModel implements ProfileListener {

    @FXML
    private Label traceCount;

    @Override
    public void accept(Profile profile) {
        traceCount.setText(profile.getTraceCount() + " samples");
    }

}
