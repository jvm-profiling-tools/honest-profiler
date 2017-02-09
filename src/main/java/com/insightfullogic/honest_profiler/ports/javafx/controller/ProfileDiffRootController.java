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
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil.flatExtractor;
import static com.insightfullogic.honest_profiler.ports.javafx.util.BindUtil.treeExtractor;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ConversionUtil.getStringConverterForType;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_CHOICE_VIEWTYPE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_LABEL_BASESOURCE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_LABEL_NEWSOURCE;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.ports.javafx.ViewType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for Views which encapsulate other Views related to the comparison between the same two profiles.
 */
public class ProfileDiffRootController extends AbstractController
{
    @FXML
    private ChoiceBox<ViewType> viewChoice;
    @FXML
    private Label baseSourceLabel;
    @FXML
    private Label newSourceLabel;
    @FXML
    private AnchorPane content;
    @FXML
    private FlatDiffViewController flatController;
    @FXML
    private TreeDiffViewController treeController;

    private Map<ViewType, List<AbstractProfileDiffViewController<?, ?>>> controllerMap;

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize();

        controllerMap = new HashMap<>();
        controllerMap.put(FLAT, asList(flatController));
        controllerMap.put(TREE, asList(treeController));
    }

    // Instance Accessors

    /**
     * Sets the {@link ApplicationContext} and propagates it to any contained controllers.
     * <p>
     *
     * @param applicationContext the {@link ApplicationContext}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);

        flatController.setApplicationContext(applicationContext);
        treeController.setApplicationContext(applicationContext);
    }

    /**
     * Sets the {@link ProfileContext}s and propagates it to any contained controllers. The method also configures the
     * various contained controllers.
     * <p>
     *
     * @param baseContext the {@link ProfileContext} for the Base profile
     * @param newContext the {@link ProfileContext} for the New profile
     */
    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        baseSourceLabel.setText(baseContext.getName());
        newSourceLabel.setText(newContext.getName());

        // Configure "main" FlatDiffView and bind it to the profiles in the ProfileContext
        flatController.setProfileContexts(baseContext, newContext);
        flatController.setAllowedThreadGroupings(ALL_TOGETHER);
        flatController.setAllowedFrameGroupings(BY_FQMN, BY_FQMN_LINENR, BY_BCI, BY_METHOD_ID);
        flatController.bind(
            baseContext.profileProperty(),
            newContext.profileProperty(),
            flatExtractor(flatController));

        // Configure "main" TreeDiffView and bind it to the profiles in the ProfileContext
        treeController.setProfileContexts(baseContext, newContext);
        treeController.setAllowedThreadGroupings(BY_NAME, BY_ID, ALL_TOGETHER);
        treeController.setAllowedFrameGroupings(BY_FQMN, BY_FQMN_LINENR, BY_BCI, BY_METHOD_ID);
        treeController.bind(
            baseContext.profileProperty(),
            newContext.profileProperty(),
            treeExtractor(treeController));

        // Configure the View choice
        viewChoice.setConverter(getStringConverterForType(ViewType.class));
        viewChoice.getItems().addAll(FLAT, TREE);
        viewChoice.getSelectionModel().selectedItemProperty()
            .addListener((property, oldValue, newValue) -> show(newValue));
        viewChoice.getSelectionModel().select(FLAT);
    }

    // View Switch

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
        info(baseSourceLabel, INFO_LABEL_BASESOURCE);
        info(newSourceLabel, INFO_LABEL_NEWSOURCE);
    }

    @Override
    protected void initializeHandlers()
    {
        // NOOP
    }
}
