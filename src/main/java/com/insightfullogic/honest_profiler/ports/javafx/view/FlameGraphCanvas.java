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

import static com.insightfullogic.honest_profiler.ports.javafx.view.Rendering.renderPercentage;
import static java.lang.Math.max;
import static javafx.scene.paint.Color.ROYALBLUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FlameGraphCanvas extends Canvas
{
    private static final Color START_COLOR = Color.BISQUE.deriveColor(0, 1.2, 1.0, 1.0);
    private static final int TEXT_WIDTH = 7;
    private static final int ROW_WRAP = 4;

    private final Tooltip tooltip = new Tooltip();

    private List<FlameBlock> flameBlocks;
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

        flameBlocks = new ArrayList<>();

        final GraphicsContext ctx = getGraphicsContext2D();

        ctx.clearRect(0, 0, getWidth(), getHeight());
        ctx.setStroke(Color.WHITE);

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
        double colWidth = getWidth() / nrSamples;

        // Nr Rows = max depth of a stack. The root Node represents all threads, but since the descendant depth of a
        // Node without children is defined as 0, this works out fine.
        int nrRows = tree.getData().stream().mapToInt(Node::getDescendantDepth).max().getAsInt()
            + 1;

        double rowHeight = max(getHeight() / nrRows, ctx.getFont().getSize());

        double startX = 0;
        double startY = getHeight() - rowHeight;
        for (Node node : tree.getData())
        {
            startX += renderNode(ctx, node, 0, colWidth, rowHeight, startX, startY);
        }
    }

    private double renderNode(GraphicsContext ctx, Node node, int row, double columnWidth,
        double rowHeight, double startX, double startY)
    {
        // Colour based on row index
        ctx.setFill(colorAt(row));

        double x = startX;
        double y = startY - ((row + 1) * rowHeight);

        double width = node.getTotalCnt() * columnWidth;

        ctx.fillRect(x, y, width, rowHeight);
        flameBlocks.add(new FlameBlock(new Rectangle(x, y, width, rowHeight), node));

        renderNodeText(ctx, x, y, width, rowHeight, node);

        for (Node child : node.getChildren())
        {
            x += renderNode(ctx, child, row + 1, columnWidth, rowHeight, x, startY);
        }

        return width;
    }

    private Color colorAt(final int row)
    {
        return START_COLOR.deriveColor(0, 1.15 * (1 + row % ROW_WRAP), 1.0, 1.0);
    }

    private void renderNodeText(final GraphicsContext ctx, final double x, final double y,
        double width, double height, Node node)
    {
        List<String> titles = new ArrayList<>();
        titles.add(node.getKey());

        MethodInfo method = methodForNode(node);
        if (method != null)
        {
            titles.add(method.getFqmn());
            titles.add(method.getCompactName());
            titles.add(method.getMethodName());
        }

        renderText(ctx, x, y, width, height, titles.toArray(new String[titles.size()]));
    }

    /**
     * Examine the width of the provided Strings, which should be specified in decreasing length, and render the longest
     * one which can be rendered in a rectangle with the specified width, or do not render anything.
     *
     * @param ctx the {@link GraphicsContext} for the rendition
     * @param x the x coordinate where the text should be rendered
     * @param y the y coordinate where the text should be rendered
     * @param width the maximum width of the rectangle within which the text should be rendered
     * @param height the height of the rectangle within which the text should be rendered
     * @param titles the alternative labels to be rendered
     */
    private void renderText(final GraphicsContext ctx, final double x, final double y, double width,
        double height, String... titles)
    {

        for (String title : titles)
        {
            if (title.length() * TEXT_WIDTH < width)
            {
                ctx.setFill(ROYALBLUE);
                ctx.fillText(title, x, y + 0.75 * height);
                return;
            }
        }
    }

    private void displayMethodName(final MouseEvent mouseEvent)
    {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        Optional<FlameBlock> flameBlock = flameBlocks.stream()
            .filter(location -> location.contains(x, y)).findFirst();

        if (flameBlock.isPresent())
        {
            Node node = flameBlock.get().getNode();

            tooltip.setText(node.getKey());
            tooltip.show(getScene().getWindow(), x, y);

            appCtx.setRawInfo(
                node.getKey()
                    + " ("
                    + node.getTotalCnt()
                    + " samples, "
                    + renderPercentage(node.getTotalCntPct())
                    + ")");
        }
        else
        {
            tooltip.hide();
            appCtx.clearInfo();
        }
    }

    private MethodInfo methodForNode(Node node)
    {
        if (node.getAggregatedNodes().size() == 0)
        {
            return null;
        }

        LeanNode aggregatedNode = node.getAggregatedNodes().get(0);
        return aggregatedNode.isThreadNode() ? null
            : methodMap.get(aggregatedNode.getFrame().getMethodId());
    }
}
