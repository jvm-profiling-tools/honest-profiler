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
package com.insightfullogic.honest_profiler.ports.javafx.view.flame;

import java.util.Objects;

import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;

import javafx.scene.shape.Rectangle;

/**
 * Container representing a FlameGraph rectangle which was rendered for the contained {@link Node} or {@link DiffNode}.
 *
 * @param <T> the type of node represented by the FlameGraph rectangle
 */
public class FlameBlock<T>
{
    // Instance Properties

    private final Rectangle rectangle;
    private final T node;

    // Instance COnstructors

    /**
     * Trivial constructor.
     *
     * @param rectangle the {@link Rectangle} representing the region rendered for the contained {@link Node} or
     *            {@link DiffNode}
     * @param node the contained {@link Node} or {@link DiffNode}
     */
    public FlameBlock(final Rectangle rectangle, T node)
    {
        Objects.requireNonNull(rectangle);
        Objects.requireNonNull(node);
        this.rectangle = rectangle;
        this.node = node;
    }

    // Instance Accessors

    /**
     * Returns a boolean indicating whether the specified coordinates lie within the contained {@link Rectangle}.
     *
     * @param x the x coordinate being tested
     * @param y the y coordinate being tested
     * @return a boolean indicating whether the specified coordinates lie within the contained {@link Rectangle}
     */
    public boolean contains(final double x, final double y)
    {
        return rectangle.contains(x, y);
    }

    /**
     * Returns the contained {@link Node} or {@link DiffNode}.
     *
     * @return the contained {@link Node} or {@link DiffNode}
     */
    public T getNode()
    {
        return node;
    }
}
