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
package com.insightfullogic.honest_profiler.ports.javafx.view.cell;

import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.WHEAT;

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeTableCell;
import javafx.scene.paint.Color;

public class GraphicalShareTreeTableCell extends TreeTableCell<Node, Number>
{
    // Class Properties

    private static final int IMAGE_WIDTH = 50;
    private static final int IMAGE_HEIGHT = 15;

    private static final int TEXT_HORIZONTAL_INSET = 10;
    private static final int TEXT_VERTICAL_INSET = 12;

    // Instance Properties

    private ApplicationContext appCtx;

    // Instance Constructors

    /**
     * Simple Constructor.
     *
     * @param appCtx the {@link ApplicationContext} for the application
     */
    public GraphicalShareTreeTableCell(ApplicationContext appCtx)
    {
        this.appCtx = appCtx;
    }

    // TreeTableCell Implementation

    @Override
    protected void updateItem(Number number, boolean empty)
    {
        super.updateItem(number, empty);

        if (empty || number == null)
        {
            setGraphic(null);
            return;
        }

        Canvas canvas = new Canvas(IMAGE_WIDTH, IMAGE_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.BLACK);
        context.strokeRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        double share = number.doubleValue();
        double scaledShare = share * IMAGE_WIDTH;
        double xStart = IMAGE_WIDTH - scaledShare;
        context.setFill(Color.GREEN);
        context.fillRect(xStart, 0, scaledShare, IMAGE_HEIGHT);

        Color color = share > 0.5 ? WHEAT : RED;
        context.setFill(color);
        context.fillText(appCtx.displayPercent(number), TEXT_HORIZONTAL_INSET, TEXT_VERTICAL_INSET);

        setGraphic(canvas);
    }
}
