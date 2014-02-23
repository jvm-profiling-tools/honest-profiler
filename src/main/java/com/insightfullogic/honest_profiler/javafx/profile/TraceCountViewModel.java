package com.insightfullogic.honest_profiler.javafx.profile;

import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.collector.ProfileListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class TraceCountViewModel implements ProfileListener {

    @FXML
    private Label traceCount;

    @Override
    public void accept(Profile profile) {
        traceCount.setText(profile.getTraceCount() + " samples");
    }

}
