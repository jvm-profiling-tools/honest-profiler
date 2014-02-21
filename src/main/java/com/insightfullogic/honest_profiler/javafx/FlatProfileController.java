package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.log.Method;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.MessageFormat;

import static javafx.scene.control.TableColumn.CellDataFeatures;

public class FlatProfileController {

    @FXML
    private TableView<FlatProfileEntry> flatProfileView;

    @FXML
    private TableColumn<FlatProfileEntry, String> proportions;

    @FXML
    private TableColumn<FlatProfileEntry, String> methods;

    @FXML
    private void initialize() {
        proportions.setCellValueFactory(CellValues::timeShare);
        methods.setCellValueFactory(CellValues::method);
    }

    public void setViewModel(ProfileViewModel viewModel) {
        flatProfileView.setItems(viewModel.getFlatProfile());
    }

}
