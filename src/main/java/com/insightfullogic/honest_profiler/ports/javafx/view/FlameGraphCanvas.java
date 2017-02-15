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

import static java.lang.Math.max;
import static javafx.scene.paint.Color.ROYALBLUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

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
    private Map<Long, MethodInfo> methodMap;

    private ApplicationContext appCtx;

    public FlameGraphCanvas(ApplicationContext applicationContext)
    {
        this.appCtx = applicationContext;
        setOnMouseMoved(this::displayMethodName);
    }

    public void accept(final Tree tree)
    {
        methodMap = tree.getSource().getSource().getMethodInfoMap();

        methodLocations = new ArrayList<>();

        final GraphicsContext graphics = getGraphicsContext2D();

        // graphics.clearRect();
        // graphics.setFill(Color.GRAY);

        graphics.clearRect(0, 0, getWidth(), getHeight());
        graphics.setStroke(Color.WHITE);

        Node rootNode = tree.getData().get(0);

        // // Total number of samples
        long nrSamples = tree.getSource().getGlobalData().getTotalCnt();

        // TODO Reaggregate when filtering so the width can be calculated properly. Now we have bla

        // Total number of samples in the leaves (the Tree may be filtered so we can't use the AggregationProfile global
        // data)
        // long nrSamples = rootNode.flattenDescendants()
        // .filter(node -> node.getChildren().size() == 0)
        // .flatMap(node -> node.getAggregatedNodes().stream())
        // .mapToLong(node -> node.getData().getTotalCnt()).sum();

        // Any frame will be represented with its width proportional to its total sample count divided by the profile
        // total sample count
        double columnWidth = getWidth() / nrSamples;

        // Nr Rows = max depth of a stack. The root Node represents all threads, but since the descendant depth of a
        // Node without children is defined as 0, this works out fine.
        int nrRows = rootNode.getDescendantDepth();

        double rowHeight = max(getHeight() / nrRows, graphics.getFont().getSize());

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

            renderText(
                graphics,
                x,
                y,
                width,
                rowHeight,
                method.getFqmn(),
                method.getCompactName(),
                method.getMethodName());

            renderChildNodes(graphics, child, row + 1, columnWidth, rowHeight, currentX, startY);

            currentX += width;
        }

        return currentX;
    }

    private Color colorAt(final int row)
    {
        return START_COLOR.deriveColor(0, 1.15 * (1 + row % ROW_WRAP), 1.0, 1.0);
    }

    /**
     * Examine the width of the provided Strings, which should be specified in decreasing length, and render the longest
     * one which can be rendered in a rectangle with the specified width, or do not render anything.
     *
     * @param graphics the {@link GraphicsContext} for the rendition
     * @param x the x coordinate where the text should be rendered
     * @param y the y coordinate where the text should be rendered
     * @param width the maximum width of the rectangle within which the text should be rendered
     * @param height the height of the rectangle within which the text should be rendered
     * @param titles the alternative labels to be rendered
     */
    private void renderText(final GraphicsContext graphics, final double x, final double y,
        double width, double height, String... titles)
    {
        for (String title : titles)
        {
            if (title.length() * TEXT_WIDTH < width)
            {
                graphics.setFill(ROYALBLUE);
                graphics.fillText(title, x, y + 0.75 * height);
                return;
            }
        }
    }

    private void displayMethodName(final MouseEvent mouseEvent)
    {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        Optional<MethodLocation> methodLocation = methodLocations.stream()
            .filter(location -> location.contains(x, y)).findFirst();

        if (methodLocation.isPresent())
        {
            String text = methodLocation.get().getMethod().getFqmn();
            tooltip.setText(text);
            tooltip.show(getScene().getWindow(), x, y);
            appCtx.setRawInfo(text);
        }
        else
        {
            tooltip.hide();
            appCtx.clearInfo();
        }
    }
}
