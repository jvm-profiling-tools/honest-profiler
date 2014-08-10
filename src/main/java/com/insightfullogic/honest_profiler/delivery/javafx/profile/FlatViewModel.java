package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.collector.Profile;
import com.insightfullogic.honest_profiler.delivery.javafx.Rendering;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FlatViewModel implements ProfileListener {

    private final ObservableList<FlatProfileEntry> flatProfile;

    @FXML
    private TableView<FlatProfileEntry> flatProfileView;

    @FXML
    private TableColumn<FlatProfileEntry, Double> totalTimeShare;

    @FXML
    private TableColumn<FlatProfileEntry, Double> selfTimeShare;

    @FXML
    private TableColumn<FlatProfileEntry, String> methods;

    @FXML
    private void initialize() {
        configureTimeShareColumn(totalTimeShare, "totalTimeShare");
        configureTimeShareColumn(selfTimeShare, "selfTimeShare");

        methods.setCellValueFactory(Rendering::method);
        flatProfileView.setItems(flatProfile);
    }

    private void configureTimeShareColumn(TableColumn<FlatProfileEntry, Double> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory(propertyName));
        column.setCellFactory(Rendering::getTimeShareCellFactory);
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
