package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.selectLogFile;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showErrorDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExceptionDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.FXML_PROFILE_DIFF_ROOT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.FXML_PROFILE_ROOT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.addProfileNr;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.createColoredLabelContainer;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.getProgressIndicator;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.loaderFor;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CONTENT_TAB_LOADING;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.HEADER_DIALOG_ERR_ALREADYOPENPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.HEADER_DIALOG_ERR_OPENPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_MENU_ROOT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TAB_PROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TAB_PROFILEDIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.MESSAGE_DIALOG_ERR_ALREADYOPENPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.MESSAGE_DIALOG_ERR_OPENPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.MESSAGE_DIALOG_ERR_TASKCANCELED;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TITLE_DIALOG_ERR_ALREADYOPENPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TITLE_DIALOG_ERR_OPENPROFILE;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.LIVE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.LOG_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;
import static javafx.application.Platform.exit;
import static javafx.application.Platform.runLater;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import com.insightfullogic.honest_profiler.core.MachineListener;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.javafx.UserInterfaceConfigurationException;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.task.InitializeProfileTask;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;

import javafx.concurrent.Task;
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

    @Override
    @FXML
    public void initialize()
    {
        super.initialize();

        setApplicationContext(new ApplicationContext(this));
        info.textProperty().bind(appCtx().getInfo());

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
        MenuItem vmItem = new MenuItem(vmId);

        vmItem.setId(vm.getId());
        vmItem.setDisable(!vm.isAgentLoaded());
        vmItem.setOnAction(event ->
        {
            vmItem.setDisable(true);
            createNewProfile(vm, true);
        });

        if (monitorMenu != null)
        {
            monitorMenu.getItems().add(vmItem);
        }
    }

    private void removeFromMachineMenu(final VirtualMachine vm)
    {
        monitorMenu.getItems().removeIf(node -> vm.getId().equals(node.getId()));
    }

    public void close()
    {
        machineSource.stop();
    }

    // Profile-related Helper Methods

    private void createNewProfile(Object source, boolean live)
    {
        Tab tab = newLoadingTab();
        ProfileRootController controller = loadViewIntoTab(FXML_PROFILE_ROOT, tab);
        tab.getContent().setVisible(false);

        Task<ProfileContext> task = new InitializeProfileTask(appCtx(), source, live);

        task.setOnSucceeded(event -> handleNewProfile(tab, controller, task.getValue()));

        task.setOnFailed(

            event ->
            {
                profileTabs.getTabs().remove(tab);
                showExceptionDialog(
                    appCtx(),
                    appCtx().textFor(TITLE_DIALOG_ERR_OPENPROFILE),
                    appCtx().textFor(HEADER_DIALOG_ERR_OPENPROFILE),
                    appCtx().textFor(MESSAGE_DIALOG_ERR_OPENPROFILE),
                    task.getException());
            });

        task.setOnCancelled(
            event ->
            {
                profileTabs.getTabs().remove(tab);
                showErrorDialog(
                    appCtx().textFor(TITLE_DIALOG_ERR_OPENPROFILE),
                    appCtx().textFor(HEADER_DIALOG_ERR_OPENPROFILE),
                    appCtx().textFor(MESSAGE_DIALOG_ERR_TASKCANCELED));
            });

        appCtx().execute(task);
    }

    private void handleNewProfile(Tab tab, ProfileRootController controller,
        ProfileContext profileContext)
    {
        controller.setApplicationContext(appCtx());
        controller.setProfileContext(profileContext);
        initializeProfileTabTitle(tab, profileContext);

        tab.getContent().setVisible(true);
    }

    public void generateDiffTab(String baseName, String newName)
    {
        Tab tab = newLoadingTab();
        ProfileDiffRootController controller = loadViewIntoTab(FXML_PROFILE_DIFF_ROOT, tab);
        tab.getContent().setVisible(false);

        controller.setApplicationContext(appCtx());
        ProfileContext baseCtx = appCtx().getProfileContext(baseName);
        ProfileContext newCtx = appCtx().getProfileContext(newName);
        controller.setProfileContexts(baseCtx, newCtx);

        tab.setText(null);
        Pane tabInfo = createColoredLabelContainer();
        tab.setGraphic(tabInfo);
        info(tab, INFO_TAB_PROFILEDIFF, baseName, newName);

        addProfileNr(tabInfo, baseCtx);
        tabInfo.getChildren().add(new Label("<->"));
        addProfileNr(tabInfo, newCtx);

        runLater(() -> tab.getContent().setVisible(true));
    }

    private void initializeProfileTabTitle(Tab tab, ProfileContext profileContext)
    {
        tab.setText(null);
        Pane tabInfo = createColoredLabelContainer();
        tab.setGraphic(tabInfo);
        addProfileNr(tabInfo, profileContext);
        tabInfo.getChildren().add(viewFor(profileContext.getMode() == LOG ? LOG_16 : LIVE_16));
        tabInfo.getChildren().add(new Label(profileContext.getName()));

        info(tab, INFO_TAB_PROFILE, profileContext.getName());
    }

    private <T extends AbstractController> T loadViewIntoTab(String fxml, Tab tab)
    {
        try
        {
            FXMLLoader loader = loaderFor(this, fxml);
            tab.setContent(loader.load());
            T controller = loader.getController();
            profileTabs.getTabs().add(tab);
            profileTabs.getSelectionModel().select(tab);
            return controller;
        }
        catch (IOException ioe)
        {
            throw new UserInterfaceConfigurationException(ioe);
        }
    }

    // UI State Helper Methods

    private Tab newLoadingTab()
    {
        Tab tab = new Tab(appCtx().textFor(CONTENT_TAB_LOADING));
        tab.setGraphic(getProgressIndicator(15, 15));
        return tab;
    }

    private void doWithFile(Consumer<File> fileBasedAction)
    {

        setRootDisabled(true);
        File file = selectLogFile(appCtx());
        setRootDisabled(false);

        if (file != null)
        {
            Integer id = appCtx().getContextIdByPath(file);
            if (id == null)
            {
                fileBasedAction.accept(file);
            }
            else
            {
                showErrorDialog(
                    appCtx().textFor(TITLE_DIALOG_ERR_ALREADYOPENPROFILE),
                    appCtx().textFor(HEADER_DIALOG_ERR_ALREADYOPENPROFILE),
                    appCtx().textFor(MESSAGE_DIALOG_ERR_ALREADYOPENPROFILE, id.toString()));
            }
        }
    }

    private void setRootDisabled(boolean disable)
    {
        menuBar.setDisable(disable);
        profileTabs.setDisable(disable);
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(menuBar, INFO_MENU_ROOT);
    }

    @Override
    protected void initializeHandlers()
    {
        openLogItem.setOnAction(event -> doWithFile(file -> createNewProfile(file, false)));
        openLiveItem.setOnAction(event -> doWithFile(file -> createNewProfile(file, true)));
        quitItem.setOnAction(event -> exit());
    }
}
