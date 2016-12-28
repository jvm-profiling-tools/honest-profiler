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

import org.omg.PortableInterceptor.ServerRequestInfo;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.javafx.UserInterfaceConfigurationException;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class RootController extends AbstractController implements MachineListener
{
    @FXML
    private MenuBar menuBar;
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
    @FXML
    private Label info;

    private LocalMachineSource machineSource;

    @FXML
    public void initialize()
    {
        setApplicationContext(new ApplicationContext(this));
        info.textProperty().bind(appCtx().getInfo());

        info(
            menuBar,
            "The File menu allows you to open existing log files. The Monitor menu allows you to select a running JVM and profile it. JVMs not running the Honest Profiler agent are greyed out.");

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

        // The following oesn't work properly. Not setting a text might work,
        // except the graphic for an unselected disabled item is not shown, so
        // the menu looks empty. No workaround found yet.

        // Label label = new Label(machineId);
        // info(
        // label,
        // machine.isAgentLoaded() ? "Open profiling window for JVM " +
        // machine.getId()
        // : "This JVM cannot be profiled, it does not have the Honest Profiler
        // Agent attached to it.");
        // machineItem.setGraphic(label);

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
            controller.setApplicationContext(appCtx());

            ProfileContext profileContext = controller.initializeProfile(appCtx(), source);

            Tab tab = new Tab(profileContext.getName());
            tab.setGraphic(new Label(profileContext.getName()));
            info(tab, "Shows profiles " + profileContext.getName());

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
            controller.setApplicationContext(appCtx());

            controller.setProfileContexts(
                appCtx().getProfileContext(baseName),
                appCtx().getProfileContext(newName)
                );

            Tab tab = new Tab();
            tab.setGraphic(new Label("Diff : " + baseName + " <-> " + newName));
            info(tab, "Shows the difference between profiles " + baseName + " and " + newName);
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
