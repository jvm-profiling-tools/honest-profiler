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

import static javafx.scene.paint.Color.DARKRED;

import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GraphicalShareTableCell<T> extends TableCell<T, Double>
{
    // Class Properties

    private static final double HEIGHT = 8;
    private static final Color COLOR = DARKRED;

    // Instance Properties

    private ApplicationContext appCtx;

    // Instance Constructors

    /**
     * Simple Constructor.
     *
     * @param appCtx the {@link ApplicationContext} for the application
     */
    public GraphicalShareTableCell(ApplicationContext appCtx)
    {
        this.appCtx = appCtx;
    }

    // TableCell Implementation

    @Override
    protected void updateItem(Double share, boolean empty)
    {
        super.updateItem(share, empty);

        if (empty || share == null)
        {
            setText(null);
            setGraphic(null);
            return;
        }

        setText(appCtx.displayPercent(share));
        setGraphic(new Rectangle(share * getWidth(), HEIGHT, COLOR));
    }
}
