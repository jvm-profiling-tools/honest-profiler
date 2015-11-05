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
package com.insightfullogic.honest_profiler.ports.javafx;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.DARKRED;

public class GraphicalShareTableCell extends TableCell<FlatProfileEntry, Double>
{

    private static final double height = 29;
    private static final Color timeTakenColor = DARKRED;

    private final double width;

    public GraphicalShareTableCell(final double width)
    {
        this.width = width;
    }

    @Override
    protected void updateItem(Double timeShare, boolean empty)
    {
        if (timeShare == null)
        {
            setText("");
        }
        else
        {
            final double scaledShare = timeShare * width;

            Canvas canvas = new Canvas(width, height);
            GraphicsContext context = canvas.getGraphicsContext2D();
            context.setFill(timeTakenColor);
            context.fillRect(0, 0, scaledShare, height);
            setGraphic(canvas);
        }
    }

}
