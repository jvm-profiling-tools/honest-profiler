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

import static com.insightfullogic.honest_profiler.ports.javafx.view.Rendering.renderMethod;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Rendering.renderShortMethod;
import static javafx.application.Platform.runLater;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraphListener;
import com.insightfullogic.honest_profiler.core.profiles.FlameTrace;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

// TODO: remove any flame graph calculation logic from the canvas.
public class FlameGraphCanvas extends Canvas implements FlameGraphListener
{
    private static final Color START_COLOR = Color.BISQUE.deriveColor(0, 1.2, 1.0, 1.0);
    private static final int TEXT_WIDTH = 7;
    private static final int ROW_WRAP = 4;

    private final Tooltip tooltip = new Tooltip();
    private Window window;

    private double rowHeight;
    private List<FlameTrace> traces;
    private List<MethodLocation> methodLocations;
    private FlameGraph graph;

    public FlameGraphCanvas()
    {
        setOnMouseMoved(this::displayMethodName);
    }

    private void displayMethodName(final MouseEvent mouseEvent)
    {
        final double x = mouseEvent.getX();
        final double y = mouseEvent.getY();

        final Optional<MethodLocation> methodLocation = methodLocations
            .stream()
            .filter(location -> location.contains(x, y))
            .findFirst();

        if (methodLocation.isPresent())
        {
            tooltip.setText(renderMethod(methodLocation.get().getMethod()));
            tooltip.show(window, x, y);
        }
        else
        {
            tooltip.hide();
        }
    }

    public void refresh()
    {
        if (graph != null)
        {
            runLater(() -> accept(graph));
        }
    }

    @Override
    public void accept(final FlameGraph graph)
    {
        this.graph = graph;

        final Scene scene = getScene();
        if (scene != null)
        {
            setOpacity(1.0);

            if (window == null)
            {
                window = scene.getWindow();
            }

            methodLocations = new ArrayList<>();

            final GraphicsContext graphics = getGraphicsContext2D();
            // graphics.clearRect();
            // graphics.setFill(Color.GRAY);
            graphics.clearRect(0, 0, getWidth(), getHeight());

            graphics.setStroke(Color.WHITE);

            traces = graph.getTraces();

            final long totalWeight = graph.totalWeight();
            final int maxHeight = graph.maxTraceHeight();

            final double columnWidth = getWidth() / totalWeight;
            rowHeight = getHeight() / maxHeight;
            final double initialY = getHeight() - rowHeight;

            for (int row = 0; row < maxHeight; row++)
            {
                double y = initialY - (row * rowHeight);
                final Color colour = colorAt(row);

                for (int col = 0; col < traces.size();)
                {
                    FlameTrace stack = traces.get(col);
                    final double stackWidth = stack.getWeight() * columnWidth;

                    Method method = stack.at(row);
                    final int numberOfConsecutiveTraces = numberOfConsecutiveTracesWith(
                        method,
                        col,
                        row);
                    double methodWidth = stackWidth * numberOfConsecutiveTraces;

                    if (method != null)
                    {
                        final double x = col * stackWidth;
                        graphics.setFill(colour);
                        graphics.fillRect(x, y, methodWidth, rowHeight);
                        methodLocations.add(
                            new MethodLocation(
                                new Rectangle(x, y, methodWidth, rowHeight),
                                method));

                        final String title = renderShortMethod(method);
                        if (!renderText(graphics, x, y, methodWidth, title))
                        {
                            renderText(graphics, x, y, methodWidth, method.getMethodName());
                        }
                    }

                    col += numberOfConsecutiveTraces;
                }
            }
        }
    }

    private Color colorAt(final int row)
    {
        return START_COLOR.deriveColor(0, 1.15 * (1 + row % ROW_WRAP), 1.0, 1.0);
    }

    private boolean renderText(final GraphicsContext graphics,
        final double x, final double y,
        final double methodWidth,
        final String title)
    {
        if (title.length() * TEXT_WIDTH < methodWidth)
        {
            graphics.setFill(Color.ROYALBLUE);
            graphics.fillText(title, x, y + 0.75 * rowHeight);

            return true;
        }

        return false;
    }

    private int numberOfConsecutiveTracesWith(
        final Method method, final int initialCol, final int row)
    {
        int col = initialCol;
        while (col < traces.size() && traces.get(col).at(row) == method)
        {
            col++;
        }
        return col - initialCol;
    }

    public Tooltip getTooltip()
    {
        return tooltip;
    }
}
