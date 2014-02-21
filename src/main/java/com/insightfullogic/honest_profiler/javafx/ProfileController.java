package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.log.Method;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.MessageFormat;

public class ProfileController {

    @FXML
    private TableView<FlatProfileEntry> flatProfileView;

    @FXML
    private TableColumn<FlatProfileEntry, String> proportions;

    @FXML
    private TableColumn<FlatProfileEntry, String> methods;

    @FXML
    private void initialize() {
        proportions.setCellValueFactory(features -> {
            double timeShare = features.getValue().getTimeShare();
            String formattedTimeShare = MessageFormat.format("{0,number,#.##%}", timeShare);
            return new ReadOnlyObjectWrapper<>(formattedTimeShare);
        });

        methods.setCellValueFactory(features -> {
            Method method = features.getValue().getMethod();
            String representation = method.getClassName() + "." + method.getMethodName();
            return new ReadOnlyObjectWrapper<>(representation);
        });
    }

    public void setViewModel(ProfileViewModel viewModel) {
        flatProfileView.setItems(viewModel.getFlatProfile());
    }

    public void quit(ActionEvent actionEvent) {
        Platform.exit();
    }

}
