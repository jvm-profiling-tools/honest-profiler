/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// TODO: remove any flame graph calculation logic from the canvas.
public class FlameGraphCanvas extends Canvas
{
    private static final Color START_COLOR = Color.BISQUE.deriveColor(0, 1.2, 1.0, 1.0);
    private static final int TEXT_WIDTH = 7;
    private static final int ROW_WRAP = 4;

    private final Tooltip tooltip = new Tooltip();

    private List<MethodLocation> methodLocations;

    private Tree tree;
    private Map<Long, MethodInfo> methodMap;

    public FlameGraphCanvas()
    {
        setOnMouseMoved(this::displayMethodName);
    }

    public void refresh()
    {
        if (tree != null)
        {
            accept(tree);
        }
    }

    public void accept(final Tree tree)
    {
        this.tree = tree;

        methodMap = tree.getSource().getSource().getMethodInfoMap();

        methodLocations = new ArrayList<>();

        final GraphicsContext graphics = getGraphicsContext2D();
        // graphics.clearRect();
        // graphics.setFill(Color.GRAY);
        graphics.clearRect(0, 0, getWidth(), getHeight());

        graphics.setStroke(Color.WHITE);

        Node rootNode = tree.getData().get(0);

        // Total number of samples
        long nrSamples = tree.getSource().getGlobalData().getTotalCnt();

        // Any frame will be represented with its width proportional to its total sample count divided by the profile
        // total sample count
        double columnWidth = getWidth() / nrSamples;

        // Nr Rows = max depth of a stack. The root Node represents all threads, but since the descendant depth of a
        // Node without children is defined as 0, this works out fine.
        int nrRows = rootNode.getDescendantDepth();

        double rowHeight = getHeight() / nrRows;

        renderChildNodes(graphics, rootNode, 0, columnWidth, rowHeight, 0, getHeight() - rowHeight);
    }

    private double renderChildNodes(GraphicsContext graphics, Node node, int row,
        double columnWidth, double rowHeight, double startX, double startY)
    {
        // Colour based on row index
        graphics.setFill(colorAt(row));

        double currentX = startX;

        for (Node child : node.getChildren())
        {
            MethodInfo method = methodMap
                .get(child.getAggregatedNodes().get(0).getFrame().getMethodId());

            double x = currentX;
            double y = startY - ((row + 1) * rowHeight);

            double width = child.getTotalCnt() * columnWidth;

            graphics.fillRect(x, y, width, rowHeight);
            methodLocations.add(new MethodLocation(new Rectangle(x, y, width, rowHeight), method));

            String title = method.getCompactName();
            if (!renderText(graphics, x, y, width, title, rowHeight))
            {
                renderText(graphics, x, y, width, method.getMethodName(), rowHeight);
            }

            renderChildNodes(graphics, child, row + 1, columnWidth, rowHeight, currentX, startY);

            currentX += width;
        }

        return currentX;
    }

    private Color colorAt(final int row)
    {
        return START_COLOR.deriveColor(0, 1.15 * (1 + row % ROW_WRAP), 1.0, 1.0);
    }

    private boolean renderText(final GraphicsContext graphics, final double x, final double y,
        final double methodWidth, final String title, double rowHeight)
    {
        if (title.length() * TEXT_WIDTH < methodWidth)
        {
            graphics.setFill(Color.ROYALBLUE);
            graphics.fillText(title, x, y + 0.75 * rowHeight);

            return true;
        }

        return false;
    }

    private void displayMethodName(final MouseEvent mouseEvent)
    {
        final double x = mouseEvent.getX();
        final double y = mouseEvent.getY();

        final Optional<MethodLocation> methodLocation = methodLocations.stream()
            .filter(location -> location.contains(x, y)).findFirst();

        if (methodLocation.isPresent())
        {
            tooltip.setText(methodLocation.get().getMethod().getFqmn());
            tooltip.show(getScene().getWindow(), x, y);
        }
        else
        {
            tooltip.hide();
        }
    }

    public Tooltip getTooltip()
    {
        return tooltip;
    }
}
