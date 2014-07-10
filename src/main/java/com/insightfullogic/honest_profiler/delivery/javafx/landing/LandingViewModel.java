package com.insightfullogic.honest_profiler.delivery.javafx.landing;

import com.insightfullogic.honest_profiler.core.conductor.MachineListener;
import com.insightfullogic.honest_profiler.core.conductor.ProfileListener;
import com.insightfullogic.honest_profiler.core.parser.LogParser;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.delivery.javafx.WindowViewModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

import static com.insightfullogic.honest_profiler.delivery.javafx.WindowViewModel.Window.Profile;

public class LandingViewModel implements MachineListener {

    @FXML
    private VBox landingView;

    @FXML
    private Button monitor;

    private final ToggleGroup toggleMachines;
    private final LogParser parser;
    private final WindowViewModel windowModel;

    public LandingViewModel(LogParser parser, WindowViewModel windowModel) {
        this.parser = parser;
        this.windowModel = windowModel;
        toggleMachines = new ToggleGroup();
    }

    @FXML
    private void initialize() {
        toggleMachines.selectedToggleProperty()
                      .addListener((of, from, to) -> {
                          monitor.setDisable(to == null);
                      });
    }

    public void open(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a log file");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            windowModel.display(Profile);
            parser.parse(file);
        }
    }

    public void monitor(ActionEvent actionEvent) {
        windowModel.display(Profile);
        MachineButton selectedButton = (MachineButton) toggleMachines.getSelectedToggle();
        File logFile = selectedButton.getJvm().getLogFile();
        parser.monitor(logFile);
    }

    @Override
    public ProfileListener add(VirtualMachine machine) {
        Platform.runLater(() -> {
            ObservableList<Node> children = landingView.getChildren();
            MachineButton button = new MachineButton(machine);
            button.setToggleGroup(toggleMachines);
            children.add(button);
        });
        // TODO
        return null;
    }

    @Override
    public void remove(VirtualMachine machine) {
        Platform.runLater(() -> {
            String id = machine.getId();
            ObservableList<Node> children = landingView.getChildren();
            children.removeIf(node -> {
                return id.equals(node.getId());
            });
        });
    }

}
