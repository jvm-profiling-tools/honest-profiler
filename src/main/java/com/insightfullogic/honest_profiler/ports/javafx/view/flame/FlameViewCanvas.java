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

import static com.insightfullogic.honest_profiler.ports.javafx.util.RenderUtil.renderPercentage;
import static java.lang.Math.max;

import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;

/**
 * FlameGraph Canvas for {@link Tree}s.
 */
public class FlameViewCanvas extends AbstractFlameCanvas<Tree, Node>
{
    // Instance Properties

    private Map<Long, MethodInfo> methodMap;

    // Instance Constructors

    /**
     * @see AbstractFlameCanvas#AbstractFlameCanvas(ApplicationContext)
     *
     * @param applicationContext the {@link ApplicationContext} for the application
     */
    public FlameViewCanvas(ApplicationContext applicationContext)
    {
        super(applicationContext);
    }

    // AbstractFlameCanvas Implementation

    @Override
    public void render(final Tree tree)
    {
        clearBlocks();

        methodMap = tree.getSource().getSource().getMethodInfoMap();

        final GraphicsContext ctx = getGraphics();

        // // Total number of samples
        long nrSamples = tree.getSource().getGlobalData().getTotalCnt();

        // TODO Reaggregate when filtering so the width can be calculated properly. Now we have bla

        // Total number of samples in the leaves (the Tree may be filtered so we can't use the AggregationProfile global
        // data)
        // long nrSamples = rootNode.flattenDescendants()
        // .filter(node -> node.getChildren().size() == 0)
        // .flatMap(node -> node.getAggregatedNodes().stream())
        // .mapToLong(node -> node.getData().getTotalCnt()).sum();

        final ScrollPane sp = getScrollPane();
        final Bounds viewport = sp.getViewportBounds();
        // viewport.[width|height] probably get updated asynchronously after we have set prefSize in the caller
        // so we have cannot simply use viewport.[getWidth()|getHeight()].
        // But the difference (sp.getWidth() - viewport.getWidth()) (actually equal to scroll bar width plus some
        // other things) should be invariant and we can thus use it to compute the not yet set viewport size.
        final double viewportWidth = sp.getPrefWidth() - (sp.getWidth() - viewport.getWidth());
        final double viewportHeight = sp.getPrefHeight() - (sp.getHeight() - viewport.getHeight());

        // Any frame will be represented with its width proportional to its total sample count divided by the profile
        // total sample count
        double colWidth = viewportWidth / nrSamples;

        // Nr Rows = max depth of a stack. The root Node represents all threads, but since the descendant depth of a
        // Node without children is defined as 0, this works out fine.
        int nrRows = tree.getData().stream().mapToInt(Node::getDescendantDepth).max().getAsInt()
            + 1;

        double rowHeight = max(viewportHeight / nrRows, ctx.getFont().getSize());

        setWidth((int)viewportWidth);
        setHeight((int) (rowHeight * nrRows));

        double startX = 0;
        double startY = getHeight() - rowHeight;
        for (Node node : tree.getData())
        {
            startX += renderNode(ctx, node, 0, colWidth, rowHeight, startX, startY);
        }
    }

    @Override
    protected String getNodeInfo(Node node)
    {
        StringBuilder result = new StringBuilder();

        result.append(node.getKey());
        result.append(" (");
        result.append(node.getTotalCnt());
        result.append(" samples, ");
        result.append(renderPercentage(node.getTotalCntPct()));
        result.append(")");

        return result.toString();
    }

    @Override
    protected int getTotalCount(Node node)
    {
        return node.getTotalCnt();
    }

    @Override
    protected MethodInfo methodForNode(Node node)
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
