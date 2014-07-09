package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.model.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.model.collector.Profile;
import com.insightfullogic.honest_profiler.model.collector.ProfileListener;
import com.insightfullogic.honest_profiler.delivery.javafx.Rendering;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FlatViewModel implements ProfileListener {

    private final ObservableList<FlatProfileEntry> flatProfile;

    @FXML
    private TableView<FlatProfileEntry> flatProfileView;

    @FXML
    private TableColumn<FlatProfileEntry, String> proportions;

    @FXML
    private TableColumn<FlatProfileEntry, String> methods;

    @FXML
    private void initialize() {
        proportions.setCellValueFactory(Rendering::timeShare);
        methods.setCellValueFactory(Rendering::method);
        flatProfileView.setItems(flatProfile);
    }

    public FlatViewModel() {
        flatProfile = FXCollections.observableArrayList();
    }

    @Override
    public void accept(Profile profile) {
        flatProfile.clear();
        profile.flatProfile()
               .forEach(flatProfile::add);
    }

}
