package com.insightfullogic.honest_profiler.javafx.landing;

import com.insightfullogic.honest_profiler.discovery.VirtualMachineListener;
import com.insightfullogic.honest_profiler.javafx.WindowViewModel;
import com.insightfullogic.honest_profiler.log.LogParser;
import com.sun.tools.attach.VirtualMachineDescriptor;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Set;

import static com.insightfullogic.honest_profiler.javafx.WindowViewModel.Window.Profile;

public class LandingViewModel implements VirtualMachineListener {

    @FXML
    private VBox landingView;

    private final ToggleGroup toggleMachines;
    private final LogParser parser;
    private final WindowViewModel windowModel;

    public LandingViewModel(LogParser parser, WindowViewModel windowModel) {
        this.parser = parser;
        this.windowModel = windowModel;
        toggleMachines = new ToggleGroup();
    }

    public void open(ActionEvent actionEvent) {
        windowModel.display(Profile);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a log file");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            parser.parse(file);
        }
    }

    public void monitor(ActionEvent actionEvent) {
        // TODO:
        //parser.monitor(null);
    }

    @Override
    public void update(Set<VirtualMachineDescriptor> added, Set<VirtualMachineDescriptor> removed) {
        Platform.runLater(() -> {
            ObservableList<Node> children = landingView.getChildren();
            addSpawnedChildren(added, children);
            removeExitedChildren(removed, children);
        });
    }

    private void addSpawnedChildren(Set<VirtualMachineDescriptor> added, ObservableList<Node> children) {
        added.forEach(vm -> {
            MachineButton button = new MachineButton(vm);
            button.setToggleGroup(toggleMachines);
            children.add(button);
        });
    }

    private void removeExitedChildren(Set<VirtualMachineDescriptor> removed, ObservableList<Node> children) {
        removed.forEach(vm -> {
            children.removeIf(node -> vm.id().equals(node.getId()));
        });
    }

}
