/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.landing;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.collector.FlameGraphCollector;
import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.LogEventPublisher;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraphListener;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import com.insightfullogic.honest_profiler.core.sources.CantReadFromSourceException;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.javafx.WindowViewModel;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;
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
import java.util.HashSet;
import java.util.Set;

import static com.insightfullogic.honest_profiler.ports.javafx.WindowViewModel.Window.Profile;

public class LandingViewModel implements MachineListener
{

    @FXML
    private VBox landingView;

    @FXML
    private Button monitorButton;

    private final ToggleGroup toggleMachines;
    private final Logger logger;
    private final WindowViewModel windowModel;
    private final ProfileListener profileListener;
    private final FlameGraphListener flameGraphListener;
    private final Set<VirtualMachine> machines;

    public LandingViewModel(
        final Logger logger,
        final WindowViewModel windowModel,
        final ProfileListener profileListener,
        final FlameGraphListener flameGraphListener)
    {
        this.logger = logger;
        this.windowModel = windowModel;
        this.profileListener = profileListener;
        this.flameGraphListener = flameGraphListener;
        toggleMachines = new ToggleGroup();
        machines = new HashSet<>();
    }

    @FXML
    private void initialize()
    {
        logger.debug("Initializing LandingViewModel");
        toggleMachines.selectedToggleProperty()
            .addListener((of, from, to) -> {
                monitorButton.setDisable(to == null);
            });
        machines.forEach(this::displayMachine);
    }

    public void open(final ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a log file");
        File file = fileChooser.showOpenDialog(null);
        if (file != null)
        {
            windowModel.display(Profile);
            try
            {
                final LogEventListener collector = new LogEventPublisher()
                    .publishTo(new LogCollector(profileListener, false))
                    .publishTo(new FlameGraphCollector(flameGraphListener));
                Monitor.pipe(new FileLogSource(file), collector, false).run();
            }
            catch (Exception e)
            {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void monitorButton(final ActionEvent actionEvent)
    {
        windowModel.display(Profile);
        MachineButton selectedButton = (MachineButton) toggleMachines.getSelectedToggle();
        VirtualMachine vm = selectedButton.getJvm();
        try
        {
            Monitor.pipeFile(vm.getLogSource(), profileListener);
        }
        catch (CantReadFromSourceException e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onNewMachine(final VirtualMachine machine)
    {
        logger.debug("New machine: {}", machine.getDisplayName());
        machines.add(machine);
        Platform.runLater(() -> displayMachine(machine));
    }

    private void displayMachine(final VirtualMachine machine)
    {
        ObservableList<Node> children = landingView.getChildren();
        MachineButton button = new MachineButton(machine);
        button.setToggleGroup(toggleMachines);
        children.add(button);
    }

    @Override
    public void onClosedMachine(final VirtualMachine machine)
    {
        logger.debug("Closed: {}", machine.getDisplayName());
        Platform.runLater(() -> {
            String id = machine.getId();
            ObservableList<Node> children = landingView.getChildren();
            children.removeIf(node -> id.equals(node.getId()));
        });
    }

}
