/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_BCI;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN_LINENR;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_METHOD_ID;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.ALL_TOGETHER;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.BY_ID;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.BY_NAME;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.FLAME;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LIVE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil.ANCESTOR_TREE_EXTRACTOR;
import static com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil.DESCENDANT_FLAT_EXTRACTOR;
import static com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil.DESCENDANT_TREE_EXTRACTOR;
import static com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil.flatExtractor;
import static com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil.treeExtractor;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ConversionUtil.getStringConverterForType;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CONTENT_LABEL_PROFILESAMPLECOUNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COMPARE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FREEZE_FROZEN;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FREEZE_UNFROZEN;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_CHOICE_VIEWTYPE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_LABEL_PROFILESAMPLECOUNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TOOLTIP_BUTTON_FREEZE_FROZEN;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TOOLTIP_BUTTON_FREEZE_UNFROZEN;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FREEZE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.UNFREEZE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.ports.javafx.ViewType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for Views which encapsulate other Views related to the same profile.
 */
public class ProfileRootController extends AbstractController
{
    @FXML
    private ChoiceBox<ViewType> viewChoice;
    @FXML
    private Button freezeButton;
    @FXML
    private Tooltip freezeTooltip;
    @FXML
    private Button compareButton;
    @FXML
    private Label profileSampleCount;
    @FXML
    private AnchorPane content;
    @FXML
    private FlatViewController flatController;
    @FXML
    private TreeViewController callingController;
    @FXML
    private TreeViewController calledController;
    @FXML
    private TreeViewController treeController;
    @FXML
    private FlatViewController descendantsController;
    @FXML
    private FlameViewController flameController;

    private ProfileContext profileContext;

    private Map<ViewType, List<AbstractProfileViewController<?, ?>>> controllerMap;

    // FXML Implementation

    @Override
    @FXML
    public void initialize()
    {
        super.initialize();

        controllerMap = new HashMap<>();
        controllerMap.put(FLAT, asList(flatController, callingController, calledController));
        controllerMap.put(TREE, asList(treeController, descendantsController));
        controllerMap.put(FLAME, asList(flameController));
    }

    // Instance Accessors

    /**
     * Sets the {@link ApplicationContext} and propagates it to any contained controllers.
     * <p>
     *
     * @param appCtx the {@link ApplicationContext}
     */
    @Override
    public void setApplicationContext(ApplicationContext appCtx)
    {
        super.setApplicationContext(appCtx);

        flatController.setApplicationContext(appCtx);
        callingController.setApplicationContext(appCtx);
        calledController.setApplicationContext(appCtx);
        treeController.setApplicationContext(appCtx);
        descendantsController.setApplicationContext(appCtx);
        flameController.setApplicationContext(appCtx);
    }

    /**
     * Sets the {@link ProfileContext} and propagates it to any contained controllers. The method also configures the
     * various contained controllers.
     * <p>
     *
     * @param prCtx the {@link ProfileContext}
     */
    public void setProfileContext(ProfileContext prCtx)
    {
        profileContext = prCtx;

        // Configure "main" FlatView and bind it to the profile in the ProfileContext
        flatController.setProfileContext(prCtx);
        flatController.setAllowedThreadGroupings(ALL_TOGETHER);
        flatController.setAllowedFrameGroupings(BY_FQMN, BY_FQMN_LINENR, BY_BCI, BY_METHOD_ID);
        flatController.bind(prCtx.profileProperty(), flatExtractor(flatController));

        // Configure Ancestor TreeView and bind it to the selection in the main FlatView
        callingController.setProfileContext(prCtx);
        callingController.bind(flatController.selectedProperty(), ANCESTOR_TREE_EXTRACTOR);

        // Configure Descendants TreeView and bind it to the selection in the main FlatView
        calledController.setProfileContext(prCtx);
        calledController.bind(flatController.selectedProperty(), DESCENDANT_TREE_EXTRACTOR);

        // Configure "main" TreeView and bind it to the profile in the ProfileContext
        treeController.setProfileContext(prCtx);
        treeController.setAllowedThreadGroupings(BY_NAME, BY_ID, ALL_TOGETHER);
        treeController.setAllowedFrameGroupings(BY_FQMN, BY_FQMN_LINENR, BY_BCI, BY_METHOD_ID);
        treeController.bind(prCtx.profileProperty(), treeExtractor(treeController));

        // Configure Descendants FlatView and bind it to the selection in the main TreeView
        descendantsController.setProfileContext(prCtx);
        descendantsController.bind(treeController.selectedProperty(), DESCENDANT_FLAT_EXTRACTOR);

        // Configure FlameController and bind it to the flameGraph in the ProfileContext
        flameController.setProfileContext(prCtx);
        flameController.setAllowedThreadGroupings(BY_NAME, BY_ID, ALL_TOGETHER);
        flameController.setAllowedFrameGroupings(BY_FQMN, BY_FQMN_LINENR, BY_BCI, BY_METHOD_ID);
        flameController.bind(prCtx.profileProperty(), treeExtractor(flameController));

        // Bind the profile sample count display so it changes when new Profiles come in
        prCtx.profileProperty()
            .addListener((property, oldValue, newValue) -> updateSampleCount(newValue));

        // Bind the profile sample count display so it changes when the display preferences change
        appCtx().getConfiguration()
            .addListener((property, oldValue, newValue) -> updateSampleCount(prCtx));

        // Display the initial sample count
        updateSampleCount(prCtx);

        // Configure the View choice
        viewChoice.setConverter(getStringConverterForType(ViewType.class));
        viewChoice.getSelectionModel().selectedItemProperty()
            .addListener((property, oldValue, newValue) -> show(newValue));
        viewChoice.getItems().addAll(ViewType.values());
        viewChoice.getSelectionModel().select(FLAT);

        freezeButton.setDisable(prCtx.getMode() != LIVE);
    }

    // Sample Count Display

    /**
     * Update the sample count based on the {@link ProfileContext}.
     *
     * @param profileContext the {@link ProfileContext}
     */
    private void updateSampleCount(ProfileContext profileContext)
    {
        updateSampleCount(profileContext == null ? null : profileContext.getProfile());
    }

    /**
     * Update the sample count for the {@link AggregationProfile}.
     *
     * @param profile the {@link AggregationProfile}
     */
    private void updateSampleCount(AggregationProfile profile)
    {

        if (profile == null)
        {
            profileSampleCount.setText(null);
            return;
        }

        profileSampleCount.setText(getText(
            CONTENT_LABEL_PROFILESAMPLECOUNT,
            appCtx().displayIntegral(profile.getGlobalData().getTotalCnt())));
    }

    // View Methods

    /**
     * Show the selected View.
     * <p>
     *
     * @param viewType the type of View which was selected
     */
    private void show(ViewType viewType)
    {
        // Show/hide the Views based on the selected ViewType.
        for (int i = 0; i < viewChoice.getItems().size(); i++)
        {
            Node child = content.getChildren().get(i);
            child.setManaged(viewType.ordinal() == i);
            child.setVisible(viewType.ordinal() == i);
        }

        // Activate and deactivate the relevant controllers.
        controllerMap
            .forEach((type, list) -> list.forEach(ctrl -> ctrl.setActive(viewType == type)));
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(viewChoice, INFO_CHOICE_VIEWTYPE);
        info(compareButton, INFO_BUTTON_COMPARE);
        info(freezeButton, INFO_BUTTON_FREEZE_UNFROZEN);
        info(profileSampleCount, INFO_LABEL_PROFILESAMPLECOUNT);
    }

    @Override
    protected void initializeHandlers()
    {
        compareButton.setOnMousePressed(this::showCompareMenu);
        freezeButton.setOnAction(this::handleFreezeAction);
    }

    // Handler-related Helper Methods

    /**
     * Show the {@link ContextMenu} listing any profiles the currently shown profile can be compared to.
     * <p>
     *
     * @param event the {@link MouseEvent} triggering the displaying of the {@link ContextMenu}
     */
    private void showCompareMenu(MouseEvent event)
    {
        ContextMenu ctxMenu = compareButton.getContextMenu();
        if (ctxMenu == null)
        {
            ctxMenu = new ContextMenu();
            compareButton.setContextMenu(ctxMenu);
        }
        refreshContextMenu(compareButton.getContextMenu());
        compareButton.getContextMenu().show(compareButton, event.getScreenX(), event.getScreenY());
    }

    /**
     * Add profiles available for comparison to the specified {@link ContextMenu}.
     * <p>
     *
     * @param menu the {@link ContextMenu} to which available profiles will be added
     */
    private void refreshContextMenu(ContextMenu menu)
    {
        menu.getItems().clear();
        // Only compare against profiles which have actually been opened
        List<String> profileNames = appCtx().getOpenProfileNames();

        profileNames.forEach(name ->
        {
            // Don't compare a profile against itself
            if (!name.equals(profileContext.getName()))
            {
                MenuItem item = new MenuItem(name);
                item.setOnAction(event -> appCtx().createDiffView(profileContext.getName(), name));
                menu.getItems().add(item);
            }
        });
    }

    /**
     * Freeze or unfreeze the profile.
     * <p>
     *
     * @param event the {@link ActionEvent} triggering the (un)freezing
     */
    private void handleFreezeAction(ActionEvent event)
    {
        if (profileContext.isFrozen())
        {
            unfreeze();
        }
        else
        {
            freeze();
        }
    }

    /**
     * Freeze the profile.
     */
    private void freeze()
    {
        profileContext.setFrozen(true);
        freezeButton.setGraphic(viewFor(UNFREEZE_16));
        freezeTooltip.setText(appCtx().textFor(TOOLTIP_BUTTON_FREEZE_FROZEN));
        info(freezeButton, INFO_BUTTON_FREEZE_FROZEN);
    }

    /**
     * Unfreeze the profile.
     */
    private void unfreeze()
    {
        profileContext.setFrozen(false);
        freezeButton.setGraphic(viewFor(FREEZE_16));
        freezeTooltip.setText(appCtx().textFor(TOOLTIP_BUTTON_FREEZE_UNFROZEN));
        info(freezeButton, INFO_BUTTON_FREEZE_UNFROZEN);
    }
}
