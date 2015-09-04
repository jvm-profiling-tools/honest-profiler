/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.flame_graph;

import com.insightfullogic.honest_profiler.core.collector.FlameGraph;
import com.insightfullogic.honest_profiler.core.collector.FlameTrace;
import com.insightfullogic.honest_profiler.core.parser.Method;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import java.util.List;

import static com.insightfullogic.honest_profiler.ports.javafx.Rendering.renderMethod;
import static com.insightfullogic.honest_profiler.ports.javafx.Rendering.renderShortMethod;

/**
 * .
 */
// TODO: compress together method squares if they are the same.
public class FlameGraphCanvas extends Canvas
{

    private final Tooltip tooltip = new Tooltip();
    private final Window window;

    private double initialY;
    private double columnWidth;
    private double rowHeight;
    private List<FlameTrace> traces;

    public FlameGraphCanvas(final Window window)
    {
        this.window = window;
        setOnMouseClicked(this::displayMethodName);
    }

    private void displayMethodName(final MouseEvent mouseEvent)
    {
        final double x = mouseEvent.getX();
        final double y = mouseEvent.getY();

        int column = (int) (x / columnWidth);
        int row = -1 * (int) ((y - initialY) / rowHeight);

        for (FlameTrace flameTrace : traces)
        {
            column -= flameTrace.getWeight();
            if (column < 0)
            {
                List<Method> stackTrace = flameTrace.getMethods();
                if (row >= stackTrace.size() || row < 0)
                {
                    tooltip.hide();
                }
                else
                {
                    Method method = stackTrace.get(row);
                    tooltip.setText(renderMethod(method));
                    tooltip.show(window, x, y);
                }
                return;
            }
        }
        tooltip.hide();
    }

    public void display(FlameGraph graph)
    {
        final GraphicsContext graphics = getGraphicsContext2D();
        graphics.setStroke(Color.WHITE);

        traces = graph.getTraces();

        final long totalWeight = graph.totalWeight();
        final int maxHeight = graph.maxTraceHeight();

        columnWidth = getWidth() / totalWeight;
        rowHeight = getHeight() / maxHeight;
        double x = 0;

        for (FlameTrace stack : traces)
        {
            double stackWidth = stack.getWeight() * columnWidth;
            initialY = getHeight() - rowHeight;
            double y = initialY;
            Color colour = Color.BISQUE;

            for (Method method : stack.getMethods())
            {
                y -= rowHeight;

                colour = colour.deriveColor(0, 1.05, 1.0, 1.0);
                graphics.setFill(colour);
                graphics.fillRect(x, y, stackWidth, rowHeight);

                final String title = renderShortMethod(method);
                if (title.length() * 7 < stackWidth)
                {
                    graphics.setFill(Color.ROYALBLUE);
                    graphics.fillText(title, x, y + 0.75 * rowHeight);
                }
            }

            x += stackWidth;
        }
        ;
    }

    public Tooltip getTooltip()
    {
        return tooltip;
    }
}
