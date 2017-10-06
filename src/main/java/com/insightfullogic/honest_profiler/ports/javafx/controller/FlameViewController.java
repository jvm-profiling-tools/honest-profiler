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

import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.ENTRY;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.view.flame.FlameViewCanvas;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FlameViewController extends AbstractProfileViewController<Tree, Node>
{
    @FXML
    private Button filterButton;
    @FXML
    private TextField quickFilterText;
    @FXML
    private Button quickFilterButton;

    @FXML
    private Label threadGroupingLabel;
    @FXML
    private ChoiceBox<ThreadGrouping> threadGrouping;
    @FXML
    private Label frameGroupingLabel;
    @FXML
    private ChoiceBox<FrameGrouping> frameGrouping;

    @FXML
    private VBox flameBox;

    @FXML
    private FilterDialogController<Node> filterController;

    private FlameViewCanvas flameCanvas;

    private double currentWidth;
    private double currentHeight;

    // FXML Implementation

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(ENTRY);
        super.initializeFiltering(filterController, filterButton, quickFilterButton, quickFilterText);
        super.initializeGrouping(threadGroupingLabel, threadGrouping, frameGroupingLabel, frameGrouping);
    }

    // AbstractController Implementation

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);

        flameCanvas = new FlameViewCanvas(applicationContext);
        flameBox.getChildren().add(flameCanvas);
    }

    @Override
    protected void initializeInfoText()
    {
        // NOOP
    }

    @Override
    protected void initializeHandlers()
    {
        flameBox.widthProperty().addListener((property, oldValue, newValue) -> refreshIfResized());
        flameBox.heightProperty().addListener((property, oldValue, newValue) -> refreshIfResized());
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        Tree tree = getTarget();

        if (tree != null)
        {
            flameCanvas.setWidth(currentWidth);
            flameCanvas.setHeight(currentHeight);
            flameCanvas.render(tree.filter(getFilterSpecification()));
        }
    }

    @Override
    protected void initializeTable()
    {
        // NOOP
    }

    @Override
    protected HBox getColumnHeader(TableColumnBase<?, ?> column, String title,
        ProfileContext context)
    {
        return null;
    }


    /**
     * Check new dimensions : if either has changed, the new dimensions are cached and the graph is refreshed.
     */
    private void refreshIfResized()
    {
        double newWidth = flameBox.getWidth();
        double newHeight = flameBox.getHeight();

        if (newWidth != currentWidth || newHeight != currentHeight)
        {
            currentWidth = newWidth;
            currentHeight = newHeight;
            refresh();
        }
    }
}
