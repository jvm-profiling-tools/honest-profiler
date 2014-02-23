package com.insightfullogic.honest_profiler.javafx.profile;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.javafx.Rendering;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FlatProfileController {

    private final FlatViewModel viewModel;

    @FXML
    private TableView<FlatProfileEntry> flatProfileView;

    @FXML
    private TableColumn<FlatProfileEntry, String> proportions;

    @FXML
    private TableColumn<FlatProfileEntry, String> methods;

    public FlatProfileController(FlatViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    private void initialize() {
        proportions.setCellValueFactory(Rendering::timeShare);
        methods.setCellValueFactory(Rendering::method);
        flatProfileView.setItems(viewModel.getFlatProfile());
    }

}
