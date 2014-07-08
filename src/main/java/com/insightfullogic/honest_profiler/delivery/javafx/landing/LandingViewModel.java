package com.insightfullogic.honest_profiler.delivery.javafx.landing;

import com.insightfullogic.honest_profiler.core.infrastructure.source.JavaVirtualMachine;
import com.insightfullogic.honest_profiler.core.infrastructure.source.VirtualMachineListener;
import com.insightfullogic.honest_profiler.delivery.javafx.WindowViewModel;
import com.insightfullogic.honest_profiler.core.model.parser.LogParser;
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
import java.util.Set;

import static com.insightfullogic.honest_profiler.delivery.javafx.WindowViewModel.Window.Profile;

public class LandingViewModel implements VirtualMachineListener {

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
    public void update(Set<JavaVirtualMachine> added, Set<JavaVirtualMachine> removed) {
        Platform.runLater(() -> {
            ObservableList<Node> children = landingView.getChildren();
            addSpawnedChildren(added, children);
            removeExitedChildren(removed, children);
        });
    }

    private void addSpawnedChildren(Set<JavaVirtualMachine> added, ObservableList<Node> children) {
        added.forEach(vm -> {
            MachineButton button = new MachineButton(vm);
            button.setToggleGroup(toggleMachines);
            children.add(button);
        });
    }

    private void removeExitedChildren(Set<JavaVirtualMachine> removed, ObservableList<Node> children) {
        removed.forEach(vm -> {
            children.removeIf(node -> vm.getId().equals(node.getId()));
        });
    }

}
