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

import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LIVE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ConversionUtil.getStringConverterForType;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.COMPARE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FREEZE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.UNFREEZE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;

import java.util.List;

import com.insightfullogic.honest_profiler.ports.javafx.ViewType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.event.EventHandler;
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

public class ProfileRootController extends AbstractController
{
    @FXML
    private ChoiceBox<ViewType> viewChoice;
    @FXML
    private Button freezeButton;
    @FXML
    private Button compareButton;
    @FXML
    private Label traceCount;
    @FXML
    private AnchorPane content;
    @FXML
    private FlatViewController flatController;
    @FXML
    private TreeViewController treeController;
    @FXML
    private FlameViewController flameController;

    private ProfileContext profileContext;

    @FXML
    public void initialize()
    {
        info(
            viewChoice,
            "Select the View : Flat View lists all methods as a list; Tree View shows the stack trees per thread; Flame View shows the Flame Graph");
        info(compareButton, "Click to select another open profile to compare this profile against");
        info(
            freezeButton,
            "Freeze a live profile. The view will not update when new profiling information is available.");
        info(traceCount, "Shows the number of samples in the profile");

        initializeComparison();

        freezeButton.setGraphic(viewFor(FREEZE_16));
        freezeButton.setDisable(true);
        freezeButton.setTooltip(new Tooltip("Freeze the live profile."));
        info(
            freezeButton,
            "Freeze the live profile. The view will not update when new profiling information is available.");

        freezeButton.setOnAction(event ->
        {
            if (profileContext.isFrozen())
            {
                profileContext.setFrozen(false);
                freezeButton.setGraphic(viewFor(FREEZE_16));
                freezeButton.setTooltip(new Tooltip("Freeze the live profile"));
                info(
                    freezeButton,
                    "Freeze the live profile. The view will not update when new profiling information is available.");
            }
            else
            {
                profileContext.setFrozen(true);
                freezeButton.setGraphic(viewFor(UNFREEZE_16));
                freezeButton.setTooltip(new Tooltip("Unfreeze the live profile"));
                info(
                    freezeButton,
                    "Unfreeze the live profile. The view will update again when new profiling information is available.");
            }
        });
    }

    // Instance Accessors

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        flatController.setApplicationContext(applicationContext);
        treeController.setApplicationContext(applicationContext);
        flameController.setApplicationContext(applicationContext);
    }

    public void setProfileContext(ProfileContext profileContext)
    {
        this.profileContext = profileContext;

        flatController.setProfileContext(profileContext);
        treeController.setProfileContext(profileContext);
        flameController.setProfileContext(profileContext);

        profileContext.profileProperty().addListener(
            (property, oldValue, newValue) -> traceCount
                .setText(newValue == null ? null : newValue.getTraceCount() + " samples"));

        if (profileContext.getProfile() != null)
        {
            traceCount.setText(profileContext.getProfile().getTraceCount() + " samples");
        }

        viewChoice.setConverter(getStringConverterForType(ViewType.class));
        viewChoice.getItems().addAll(ViewType.values());
        viewChoice.getSelectionModel().selectedItemProperty()
            .addListener((property, oldValue, newValue) -> show(newValue));
        viewChoice.getSelectionModel().select(FLAT);

        freezeButton.setDisable(profileContext.getMode() != LIVE);
    }

    private void initializeComparison()
    {
        compareButton.setGraphic(viewFor(COMPARE_16));
        compareButton.setTooltip(new Tooltip("Compare this profile with another open profile"));
        compareButton.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                ContextMenu ctxMenu = compareButton.getContextMenu();
                if (ctxMenu == null)
                {
                    ctxMenu = new ContextMenu();
                    compareButton.setContextMenu(ctxMenu);
                }
                refreshContextMenu(compareButton.getContextMenu());
                compareButton.getContextMenu().show(
                    compareButton,
                    event.getScreenX(),
                    event.getScreenY());
            }
        });
    }
    // View Switch

    private void show(ViewType viewType)
    {
        for (int i = 0; i < viewChoice.getItems().size(); i++)
        {
            Node child = content.getChildren().get(i);
            child.setManaged(viewType.ordinal() == i);
            child.setVisible(viewType.ordinal() == i);
        }

        switch (viewType)
        {
            case FLAT:
                treeController.deactivate();
                flameController.deactivate();
                flatController.activate();
                break;
            case TREE:
                flatController.deactivate();
                flameController.deactivate();
                treeController.activate();
                break;
            case FLAME:
                flameController.activate();
                flameController.refreshFlameView();
                flatController.deactivate();
                treeController.deactivate();
                break;
            default:
        }
    }

    // Compare Helper Methods

    private void refreshContextMenu(ContextMenu menu)
    {
        menu.getItems().clear();

        List<String> profileNames = appCtx().getOpenProfileNames();

        profileNames.forEach(name ->
        {
            if (name.equals(profileContext.getName()))
            {
                return;
            }

            MenuItem item = new MenuItem(name);
            item.setOnAction(
                event -> appCtx().createDiffView(profileContext.getName(), name));
            menu.getItems().add(item);
        });
    }
}
