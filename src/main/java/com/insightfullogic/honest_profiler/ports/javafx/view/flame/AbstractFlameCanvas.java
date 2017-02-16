package com.insightfullogic.honest_profiler.ports.javafx.view.flame;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.hsb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.aggregation.result.Parent;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class AbstractFlameCanvas<T, U extends Keyed<String> & Parent<U>> extends Canvas
{
    protected static final int BUCKETS = 50;

    protected static final Color DEFAUL_START = hsb(28, 1., .5);
    protected static final Color DEFAULT_END = hsb(39, .22, 1.);

    private static final int TEXT_WIDTH = 7;

    private ApplicationContext appCtx;
    private List<FlameBlock<U>> flameBlocks;
    private Tooltip tooltip;

    protected AbstractFlameCanvas(ApplicationContext applicationContext)
    {
        this.appCtx = applicationContext;

        this.flameBlocks = new ArrayList<>();
        this.tooltip = new Tooltip();

        setOnMouseMoved(this::displayMethodName);
    }

    protected ApplicationContext appCtx()
    {
        return appCtx;
    }

    protected abstract MethodInfo methodForNode(U node);

    public abstract void render(T tree);

    protected abstract int getTotalCount(U node);

    protected abstract String getNodeInfo(U node);

    protected void clearBlocks()
    {
        flameBlocks.clear();
    }

    protected void addBlock(double x, double y, double width, double height, U node)
    {
        flameBlocks.add(new FlameBlock<U>(new Rectangle(x, y, width, height), node));
    }

    protected GraphicsContext getGraphics()
    {
        GraphicsContext ctx = getGraphicsContext2D();

        ctx.clearRect(0, 0, getWidth(), getHeight());
        ctx.setStroke(WHITE);
        return ctx;
    }

    protected double renderNode(GraphicsContext ctx, U node, int row, double columnWidth,
        double rowHeight, double startX, double startY)
    {
        // Colour based on diff pct or row index for (nearly) no difference.
        Color fill = colorFor(node, row);

        ctx.setFill(fill);

        double x = startX;
        double y = startY - ((row + 1) * rowHeight);

        double width = getTotalCount(node) * columnWidth;

        ctx.fillRect(x, y, width, rowHeight);
        addBlock(x, y, width, rowHeight, node);

        renderNodeText(ctx, fill.invert().darker(), x, y, width, rowHeight, node);

        for (U child : node.getChildren())
        {
            x += renderNode(ctx, child, row + 1, columnWidth, rowHeight, x, startY);
        }

        return width;
    }

    protected void renderNodeText(final GraphicsContext ctx, Color color, final double x,
        final double y, double width, double height, U node)
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

        renderText(ctx, color, x, y, width, height, titles.toArray(new String[titles.size()]));
    }

    /**
     * Examine the width of the provided Strings, which should be specified in decreasing length, and render the longest
     * one which can be rendered in a rectangle with the specified width, or do not render anything.
     *
     * @param ctx the {@link GraphicsContext} for the rendition
     * @param color the color for the rendition
     * @param x the x coordinate where the text should be rendered
     * @param y the y coordinate where the text should be rendered
     * @param width the maximum width of the rectangle within which the text should be rendered
     * @param height the height of the rectangle within which the text should be rendered
     * @param titles the alternative labels to be rendered
     */
    protected void renderText(final GraphicsContext ctx, Color color, final double x,
        final double y, double width, double height, String... titles)
    {

        for (String title : titles)
        {
            if (title.length() * TEXT_WIDTH < width)
            {
                ctx.setFill(color);
                ctx.fillText(title, x, y + (0.75 * height));
                return;
            }
        }
    }

    protected Color colorFor(U node, int row)
    {
        return DEFAUL_START.interpolate(DEFAULT_END, (row % BUCKETS) / (double)BUCKETS);
    }

    /**
     * Converts a percentage to an integer between 0 and 20
     *
     * @param pct the percentage to be converted
     * @return an integer between 0 and 20
     */
    protected int convertPercent(double pct)
    {
        double absPct = abs(pct);
        for (int i = 1; i < BUCKETS; i++)
        {
            if (absPct < limit(i))
            {
                return i - 1;
            }
        }
        return BUCKETS;
    }

    private double limit(int i)
    {
        // works well for range 1-20, so rescale from there
        return (exp(i * 0.3) / 1000.) * BUCKETS / 20;
    }

    private void displayMethodName(final MouseEvent mouseEvent)
    {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();

        Optional<FlameBlock<U>> flameBlock = flameBlocks.stream()
            .filter(location -> location.contains(x, y)).findFirst();

        if (flameBlock.isPresent())
        {
            U node = flameBlock.get().getNode();

            tooltip.setText(node.getKey());
            tooltip.show(getScene().getWindow(), x, y);

            appCtx.setRawInfo(getNodeInfo(node));
        }
        else
        {
            tooltip.hide();
            appCtx.clearInfo();
        }
    }
}
