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
import static com.insightfullogic.honest_profiler.ports.javafx.ViewType.TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ConversionUtil.getStringConverterForType;

import com.insightfullogic.honest_profiler.ports.javafx.ViewType;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

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

    protected void initialize()
    {
        info(
            viewChoice,
            "Select the View : Flat View lists all methods as a list; Tree View shows the stack trees per thread; Flame View shows the Flame Graph");
    }

    // Instance Accessors

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        flatController.setApplicationContext(applicationContext);
        treeController.setApplicationContext(applicationContext);
    }

    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        baseSourceLabel.setText(baseContext.getName());
        newSourceLabel.setText(newContext.getName());

        flatController.setProfileContexts(baseContext, newContext);
        treeController.setProfileContexts(baseContext, newContext);

        viewChoice.setConverter(getStringConverterForType(ViewType.class));
        viewChoice.getItems().addAll(FLAT, TREE);
        viewChoice.getSelectionModel().selectedItemProperty()
            .addListener((property, oldValue, newValue) -> show(newValue));
        viewChoice.getSelectionModel().select(FLAT);
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
                flatController.activate();
                break;
            case TREE:
                flatController.deactivate();
                treeController.activate();
                break;
            default:
        }
    }
}
