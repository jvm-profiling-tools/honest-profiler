/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.delivery.javafx.landing;

import com.insightfullogic.honest_profiler.core.Conductor;
import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.ProfileListener;
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
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static com.insightfullogic.honest_profiler.delivery.javafx.WindowViewModel.Window.Profile;

public class LandingViewModel implements MachineListener {

    @FXML
    private VBox landingView;

    @FXML
    private Button monitor;

    private final ToggleGroup toggleMachines;
    private final Logger logger;
    private final Conductor conductor;
    private final WindowViewModel windowModel;
    private final ProfileListener profileListener;

    public LandingViewModel(
            final Logger logger,
            final Conductor conductor,
            final WindowViewModel windowModel,
            final ProfileListener profileListener) {
        this.logger = logger;
        this.conductor = conductor;
        this.windowModel = windowModel;
        this.profileListener = profileListener;
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
            try {
                conductor.consumeFile(file, null, profileListener);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void monitor(ActionEvent actionEvent) {
        windowModel.display(Profile);
        MachineButton selectedButton = (MachineButton) toggleMachines.getSelectedToggle();
        File logFile = selectedButton.getJvm().getLogSource();
        try {
            conductor.pipeFile(logFile, null, profileListener);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onNewMachine(VirtualMachine machine) {
        Platform.runLater(() -> {
            ObservableList<Node> children = landingView.getChildren();
            MachineButton button = new MachineButton(machine);
            button.setToggleGroup(toggleMachines);
            children.add(button);
        });
    }

    @Override
    public void onClosedMachine(VirtualMachine machine) {
        Platform.runLater(() -> {
            String id = machine.getId();
            ObservableList<Node> children = landingView.getChildren();
            children.removeIf(node -> {
                return id.equals(node.getId());
            });
        });
    }

}
