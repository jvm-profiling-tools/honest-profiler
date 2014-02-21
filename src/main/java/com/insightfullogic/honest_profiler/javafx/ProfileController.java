package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProfileController {

    @FXML
    private TableView<FlatProfileEntry> flatProfileView;

    @FXML
    private TableColumn<FlatProfileEntry, String> proportions;

    @FXML
    private TableColumn<FlatProfileEntry, String> methods;

    @FXML
    private void initialize() {
        proportions.setCellValueFactory(new PropertyValueFactory<FlatProfileEntry, String>("timeShare"));
        methods.setCellValueFactory(new PropertyValueFactory<FlatProfileEntry, String>("method"));
    }

    public void setViewModel(ProfileViewModel viewModel) {
        flatProfileView.setItems(viewModel.getFlatProfile());
    }

    public void quit(ActionEvent actionEvent) {
        Platform.exit();
    }

}
