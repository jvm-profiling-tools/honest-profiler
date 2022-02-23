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
import static javafx.scene.paint.Color.hsb;

import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

/**
 * FlameGraph Canvas for {@link TreeDiff}s.
 */
public class FlameDiffViewCanvas extends AbstractFlameCanvas<TreeDiff, DiffNode>
{
    // Class Properties

    private static final Color BAD_START = hsb(10, .1, 1.);
    private static final Color BAD_END = hsb(10, 1., 1.);

    private static final Color GOOD_START = hsb(98, .1, 1.);
    private static final Color GOOD_END = hsb(98, 1., 1.);

    // Instance Properties

    private Map<Long, MethodInfo> baseMethodMap;
    private Map<Long, MethodInfo> newMethodMap;

    // Instance Constructors

    /**
     * @see AbstractFlameCanvas#AbstractFlameCanvas(ApplicationContext)
     *
     * @param applicationContext the {@link ApplicationContext} for the application
     */
    public FlameDiffViewCanvas(ApplicationContext applicationContext)
    {
        super(applicationContext);
    }

    // AbstractFlameCanvas Implementation

    @Override
    public void render(final TreeDiff tree)
    {
        clearBlocks();

        // The diff should be fully constructed, i.e. both base and new aggregations should be present.
        Tree baseTree = tree.getBaseAggregation();
        Tree newTree = tree.getNewAggregation();

        baseMethodMap = methodMapFor(baseTree);
        newMethodMap = methodMapFor(newTree);

        final GraphicsContext ctx = getGraphics();

        // Total number of samples
        long nrSamples = baseTree.getSource().getGlobalData().getTotalCnt()
            + newTree.getSource().getGlobalData().getTotalCnt();

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
        int nrRows = tree.getData().stream().mapToInt(DiffNode::getDescendantDepth).max().getAsInt()
            + 1;

        double rowHeight = max(viewportHeight / nrRows, ctx.getFont().getSize() + 2);

        setWidth((int)viewportWidth);
        setHeight((int) (rowHeight * nrRows));

        double startX = 0;
        double startY = getHeight() - rowHeight;
        for (DiffNode node : tree.getData())
        {
            startX += renderNode(ctx, node, 0, colWidth, rowHeight, startX, startY);
        }
    }

    @Override
    protected String getNodeInfo(DiffNode node)
    {
        StringBuilder result = new StringBuilder();

        result.append(node.getKey());
        result.append(" (");
        result.append(node.getBaseTotalCnt());
        result.append("/");
        result.append(node.getNewTotalCnt());
        result.append(" samples, ");
        result.append(renderPercentage(node.getBaseTotalCntPct()));
        result.append("/");
        result.append(renderPercentage(node.getNewTotalCntPct()));
        result.append(", diff=");
        result.append(renderPercentage(node.getTotalCntPctDiff()));
        result.append(")");

        return result.toString();
    }

    @Override
    protected int getTotalCount(DiffNode node)
    {
        return node.getBaseTotalCnt() + node.getNewTotalCnt();
    }

    @Override
    protected Color colorFor(DiffNode node, int row)
    {
        double pctDiff = node.getTotalCntPctDiff();
        int bucket = convertPercent(pctDiff);

        if (bucket == 0)
        {
            return super.colorFor(node, row);
        }

        if (pctDiff < 0)
        {
            return GOOD_START.interpolate(GOOD_END, (double)bucket / BUCKETS);
        }

        return BAD_START.interpolate(BAD_END, (double)bucket / BUCKETS);
    }

    @Override
    protected MethodInfo methodForNode(DiffNode node)
    {
        if (node.getBaseEntry().getAggregatedNodes().size() > 0)
        {
            LeanNode aggregatedNode = node.getBaseEntry().getAggregatedNodes().get(0);
            if (aggregatedNode.isThreadNode())
            {
                return null;
            }
            return baseMethodMap.get(aggregatedNode.getFrame().getMethodId());
        }

        if (node.getNewEntry().getAggregatedNodes().size() == 0)
        {
            return null;
        }

        LeanNode aggregatedNode = node.getNewEntry().getAggregatedNodes().get(0);
        return aggregatedNode.isThreadNode() ? null
            : newMethodMap.get(aggregatedNode.getFrame().getMethodId());
    }

    /**
     * Returns the method mapping for the specified {@link Tree}.
     *
     * @param tree the {@link Tree} in the {@link TreeDiff} for which the method mapping will be returned
     * @return the method mapping for the specified {@link Tree}
     */
    private Map<Long, MethodInfo> methodMapFor(Tree tree)
    {
        return tree == null ? null : tree.getSource().getSource().getMethodInfoMap();
    }
}
