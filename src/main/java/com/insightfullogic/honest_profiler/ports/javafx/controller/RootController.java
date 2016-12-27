package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.selectLogFile;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.FXML_FLAT_DIFF_VIEW;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.FXML_PROFILE_ROOT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.loaderFor;
import static javafx.application.Platform.exit;
import static javafx.application.Platform.runLater;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.javafx.UserInterfaceConfigurationException;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class RootController implements MachineListener
{
    @FXML
    private Menu fileMenu;
    @FXML
    private MenuItem openLogItem;
    @FXML
    private MenuItem quitItem;
    @FXML
    private Menu monitorMenu;
    @FXML
    private TabPane profileTabs;

    private LocalMachineSource machineSource;

    private ApplicationContext applicationContext;

    @FXML
    public void initialize()
    {
        applicationContext = new ApplicationContext(this);

        openLogItem.setOnAction(event -> selectFile());
        quitItem.setOnAction(event -> exit());

        machineSource = new LocalMachineSource(getLogger(getClass()), this);
        machineSource.start();
    }

    // File-related Helper Methods

    private void selectFile()
    {
        File file = selectLogFile();
        if (file != null)
        {
            generateProfileTab(file);
        }
    }

    // MachineListener Implementation

    @Override
    public void onNewMachine(final VirtualMachine machine)
    {
        runLater(() -> addToMachineMenu(machine));
    }

    @Override
    public void onClosedMachine(final VirtualMachine machine)
    {
        runLater(() -> removeFromMachineMenu(machine));
    }

    // Machine-related Helper Methods

    private void addToMachineMenu(final VirtualMachine machine)
    {
        final String machineId = machine.getDisplayName() + " (" + machine.getId() + ")";
        MenuItem machineItem = new MenuItem(machineId);
        machineItem.setId(machine.getId());
        machineItem.setDisable(!machine.isAgentLoaded());
        machineItem.setOnAction(event -> generateProfileTab(machine));

        if (monitorMenu != null)
        {
            monitorMenu.getItems().add(machineItem);
        }
    }

    private void removeFromMachineMenu(final VirtualMachine machine)
    {
        monitorMenu.getItems().removeIf(node -> machine.getId().equals(node.getId()));
    }

    public void close()
    {
        machineSource.stop();
    }

    // Profile-related Helper Methods

    private void generateProfileTab(Object source)
    {
        try
        {
            FXMLLoader loader = loaderFor(this, FXML_PROFILE_ROOT);
            Node root = loader.load();
            ProfileRootController controller = loader.getController();

            ProfileContext profileContext = controller.initializeProfile(source);
            applicationContext.registerProfileContext(profileContext);

            Tab tab = new Tab(profileContext.getName());
            tab.setContent(root);
            profileTabs.getTabs().add(tab);
            profileTabs.getSelectionModel().select(tab);
        }
        catch (IOException ioe)
        {
            throw new UserInterfaceConfigurationException(ioe);
        }
    }

    public void generateDiffTab(String baseName, String newName)
    {
        try
        {
            FXMLLoader loader = loaderFor(this, FXML_FLAT_DIFF_VIEW);
            Node root = loader.load();
            FlatDiffViewController controller = loader.getController();

            controller.setProfileContexts(
                applicationContext.getProfileContext(baseName),
                applicationContext.getProfileContext(newName));

            Tab tab = new Tab("Diff : " + baseName + " <-> " + newName);
            tab.setContent(root);
            profileTabs.getTabs().add(tab);
            profileTabs.getSelectionModel().select(tab);
        }
        catch (IOException ioe)
        {
            throw new UserInterfaceConfigurationException(ioe);
        }
    }
}
