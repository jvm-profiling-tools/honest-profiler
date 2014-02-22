package com.insightfullogic.honest_profiler.javafx.flat;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.javafx.Rendering;
import com.insightfullogic.honest_profiler.javafx.flat.FlatViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FlatProfileController {

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
    }

    public void setViewModel(FlatViewModel viewModel) {
        flatProfileView.setItems(viewModel.getFlatProfile());
    }

}
