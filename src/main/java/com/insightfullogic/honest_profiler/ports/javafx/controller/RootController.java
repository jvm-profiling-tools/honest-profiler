package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;
import static com.insightfullogic.honest_profiler.ports.javafx.model.configuration.Configuration.DEFAULT_CONFIGURATION;
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
import com.insightfullogic.honest_profiler.ports.javafx.controller.configuration.ConfigurationDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode;
import com.insightfullogic.honest_profiler.ports.javafx.model.task.InitializeProfileTask;
import com.insightfullogic.honest_profiler.ports.sources.LocalMachineSource;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

/**
 * Controller which manages the View-independent controls for the application.
 */
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
    private MenuItem preferencesItem;
    @FXML
    private MenuItem quitItem;
    @FXML
    private Menu monitorMenu;
    @FXML
    private TabPane profileTabs;
    @FXML
    private Label info;
    @FXML
    private ConfigurationDialogController configurationController;

    private LocalMachineSource machineSource;

    // FXML Implementation

    @Override
    @FXML
    public void initialize()
    {
        super.initialize();

        setApplicationContext(new ApplicationContext(this));

        // Bind the InfoBar Node to the ApplicationContext.
        info.textProperty().bind(appCtx().getInfo());

        // Monitor running VMs on the local machine.
        machineSource = new LocalMachineSource(getLogger(getClass()), this);
        machineSource.start();

        appCtx().setConfiguration(DEFAULT_CONFIGURATION);
        configurationController.readConfiguration(DEFAULT_CONFIGURATION);
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

    /**
     * Add a JVM to the Monitor menu.
     * <p>
     *
     * @param vm the JVM to be added
     */
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

    /**
     * remove a JVM from the Monitor menu.
     * <p>
     *
     * @param vm the JVM to be removed
     */
    private void removeFromMachineMenu(final VirtualMachine vm)
    {
        monitorMenu.getItems().removeIf(node -> vm.getId().equals(node.getId()));
    }

    /**
     * Stop the thread which monitors running VMs.
     */
    public void close()
    {
        appCtx().stop();
        machineSource.stop();
    }

    // Profile-related Helper Methods

    /**
     * Create a {@link Tab} which will contain the Views for a newly opened profile.
     * <p>
     *
     * @param source the source of the profile
     * @param live a boolean indicating whether the source is "live"
     */
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

    /**
     * Initializes the {@link ProfileRootController} for a new {@link Tab} with the specified {@link ProfileContext}.
     * <p>
     *
     * @param tab the {@link Tab} in which the profile data will be shown
     * @param controller the {@link ProfileRootController} controlling the Views for the profile
     * @param profileContext the {@link ProfileContext} for the profile
     */
    private void handleNewProfile(Tab tab, ProfileRootController controller,
        ProfileContext profileContext)
    {
        controller.setApplicationContext(appCtx());
        controller.setProfileContext(profileContext);
        initializeProfileTabTitle(tab, profileContext);

        tab.getContent().setVisible(true);
    }

    /**
     * Create a {@link Tab} which will contain the Views for the Diff between two opened profiles.
     * <p>
     *
     * @param baseName the name of the Base {@link ProfileContext}
     * @param newName the name of the New {@link ProfileContext}
     */
    public void createDiffTab(String baseName, String newName)
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

    /**
     * Set the title of a {@link Tab} for a profile.
     * <p>
     *
     * @param tab the {@link Tab} whose title will be set
     * @param profileContext the {@link ProfileContext} for the profile
     */
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

    /**
     * Loads the View using the specifie FXML file into the {@link Tab}.
     * <p>
     *
     * @param <T> the type of the resulting controller
     * @param fxml the path of the FXML file
     * @param tab the {@link Tab} into which the View will be loaded
     * @return the controller which was created for the View
     */
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

    /**
     * Create a {@link Tab} with a {@link ProgressIndicator} in the {@link Tab} header.
     * <p>
     *
     * @return a new {@link Tab} with a {@link ProgressIndicator} in the {@link Tab} header
     */
    private Tab newLoadingTab()
    {
        Tab tab = new Tab(appCtx().textFor(CONTENT_TAB_LOADING));
        tab.setGraphic(getProgressIndicator(15, 15));
        return tab;
    }

    /**
     * Helper method which presents the user with a {@link FileChooser} dialog, and executes an action based on the
     * selected {@link File}.
     * <p>
     *
     * @param fileBasedAction the action to be executed if a {@link File} was selected
     */
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

    /**
     * Disable or enable the root-level controls.
     * <p>
     *
     * @param disable a boolean indicating whether the controls should be disabled
     */
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
        preferencesItem.setOnAction(
            event -> appCtx().setConfiguration(configurationController.showAndWait().get()));
        quitItem.setOnAction(event -> exit());
    }

    // For Unit Tests Only

    public void testNewProfile(File file, boolean live)
    {
        runLater(() -> createNewProfile(file, live));
    }

    public ProfileContext getContextForFile(File file, ProfileMode mode)
    {
        return new ProfileContext(appCtx(), file.getName(), mode, file);
    }

    public void testNewProfile(ProfileContext context)
    {
        runLater(() ->
        {
            Tab tab = newLoadingTab();
            ProfileRootController controller = loadViewIntoTab(FXML_PROFILE_ROOT, tab);
            tab.getContent().setVisible(false);
            appCtx().registerProfileContext(context);
            handleNewProfile(tab, controller, context);
        });
    }
}
