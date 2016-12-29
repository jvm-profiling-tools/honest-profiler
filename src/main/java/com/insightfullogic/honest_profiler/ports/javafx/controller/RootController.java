package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.selectLogFile;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.FXML_FLAT_DIFF_VIEW;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.FXML_PROFILE_ROOT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.addProfileNr;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.createColoredLabelContainer;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.loaderFor;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.LIVE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.LOG_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;
import static javafx.application.Platform.exit;
import static javafx.application.Platform.runLater;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.javafx.UserInterfaceConfigurationException;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

public class RootController extends AbstractController implements MachineListener
{

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu fileMenu;
    @FXML
    private MenuItem openLogItem;
    @FXML
    private MenuItem openLiveItem;
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

        openLogItem.setOnAction(event -> generateProfileTab(selectLogFile(), false));
        openLiveItem.setOnAction(event -> generateProfileTab(selectLogFile(), true));
        quitItem.setOnAction(event -> exit());

        machineSource = new LocalMachineSource(getLogger(getClass()), this);
        machineSource.start();
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

    private void addToMachineMenu(final VirtualMachine vm)
    {
        String vmName = vm.getDisplayName();
        final String vmId = (vmName.contains(" ") ? vmName.substring(0, vmName.indexOf(" "))
            : vmName) + " (" + vm.getId() + ")";
        MenuItem machineItem = new MenuItem(vmId);

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

        machineItem.setId(vm.getId());
        machineItem.setDisable(!vm.isAgentLoaded());
        machineItem.setOnAction(event -> generateProfileTab(vm, true));

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

    private void generateProfileTab(Object source, boolean live)
    {
        if (source == null)
        {
            return;
        }

        Tab tab = new Tab();
        ProfileRootController controller = loadViewIntoTab(FXML_PROFILE_ROOT, tab);

        ProfileContext profileContext = controller.initializeProfile(appCtx(), source, live);

        Pane tabInfo = createColoredLabelContainer();
        tab.setGraphic(tabInfo);
        addProfileNr(tabInfo, profileContext);
        tabInfo.getChildren().add(viewFor(profileContext.getMode() == LOG ? LOG_16 : LIVE_16));
        tabInfo.getChildren().add(new Label(profileContext.getName()));

        info(tab, "Shows profile " + profileContext.getName());
    }

    public void generateDiffTab(String baseName, String newName)
    {
        Tab tab = new Tab();
        FlatDiffViewController controller = loadViewIntoTab(FXML_FLAT_DIFF_VIEW, tab);

        ProfileContext baseCtx = appCtx().getProfileContext(baseName);
        ProfileContext newCtx = appCtx().getProfileContext(newName);
        controller.setProfileContexts(baseCtx, newCtx);

        Pane tabInfo = createColoredLabelContainer();
        tab.setGraphic(tabInfo);
        info(tab, "Shows the difference between profiles " + baseName + " and " + newName);

        profileTabs.getTabs().add(tab);

        addProfileNr(tabInfo, baseCtx);
        tabInfo.getChildren().add(new Label("<->"));
        addProfileNr(tabInfo, newCtx);
    }

    private <T extends AbstractController> T loadViewIntoTab(String fxml, Tab tab)
    {
        try
        {
            FXMLLoader loader = loaderFor(this, fxml);
            tab.setContent(loader.load());
            T controller = loader.getController();
            controller.setApplicationContext(appCtx());
            profileTabs.getTabs().add(tab);
            profileTabs.getSelectionModel().select(tab);
            return controller;
        }
        catch (IOException ioe)
        {
            throw new UserInterfaceConfigurationException(ioe);
        }
    }
}
