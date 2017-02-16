package com.insightfullogic.honest_profiler.ports.javafx.view.flame;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.hsb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.aggregation.result.Parent;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.MethodInfo;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Abstract class with common code for rendering a FlameGraph.
 *
 * @param <T> the type of {@link Aggregation} being rendered
 * @param <U> the type of data items in the aggregation
 */
public abstract class AbstractFlameCanvas<T, U extends Keyed<String> & Parent<U>> extends Canvas
{
    // Class Properties

    // The colour palettes used are in fact discrete gradients, "bucketized" with this number of buckets
    protected static final int BUCKETS = 50;

    // Start colour for interpolation of non-diff method flame blocks
    protected static final Color DEFAUL_START = hsb(28, 1., .5);
    // End colour for interpolation of non-diff method flame blocks
    protected static final Color DEFAULT_END = hsb(39, .22, 1.);

    private static final int TEXT_WIDTH = 7;

    // Instance Properties

    private ApplicationContext appCtx;
    private List<FlameBlock<U>> flameBlocks;
    private Tooltip tooltip;

    // Instance COnstructors

    /**
     * Simple constructor.
     *
     * @param applicationContext the {@link ApplicationContext} for the application
     */
    protected AbstractFlameCanvas(ApplicationContext applicationContext)
    {
        this.appCtx = applicationContext;

        this.flameBlocks = new ArrayList<>();
        this.tooltip = new Tooltip();

        setOnMouseMoved(this::displayMethodName);
    }

    // Instance Accessors

    /**
     * Returns the {@link ApplicationContext} for the application.
     *
     * @return the {@link ApplicationContext} for the application.
     */
    protected ApplicationContext appCtx()
    {
        return appCtx;
    }

    // Abstract Methods

    /**
     * Returns the {@link MethodInfo} object for the method aggregated in the specified {@link Node} or
     * {@link DiffNode}. Returns null if the node is a thread-level aggregation.
     *
     * @param node the {@link Node} or {@link DiffNode}
     * @return the {@link MethodInfo} for the method aggregated in the specified node
     */
    protected abstract MethodInfo methodForNode(U node);

    /**
     * Renders the specified {@link Tree} or {@link TreeDiff}.
     *
     * @param tree the {@link Tree} or {@link TreeDiff} to be rendered
     */
    public abstract void render(T tree);

    /**
     * Returns the total sample count for the specified {@link Node} or {@link DiffNode}.
     *
     * @param node the input {@link Node} or {@link DiffNode}
     * @return the total sample count for the specified node
     */
    protected abstract int getTotalCount(U node);

    /**
     * Returns the information about the node to be shown in the InfoBar when the corresponding {@link FlameBlock} is
     * hoverd over.
     *
     * @param node the {@link Node} or {@link DiffNode}
     * @return the information about the node to be shown in the InfoBar.
     */
    protected abstract String getNodeInfo(U node);

    // FlameBlock Management Methods

    /**
     * Clears the internal list of {@link FlameBlock}s.
     */
    protected void clearBlocks()
    {
        flameBlocks.clear();
    }

    /**
     * Add a {@link FlameBlock} with the specified parameters for the specified {@link Node} or {@link DiffNode}.
     *
     * @param x the x coordinate at which the block has been rendered
     * @param y the y coordinate at which the block has been rendered
     * @param width the width of the block
     * @param height the height of the block
     * @param node the node whose information is rendered in the block
     */
    protected void addBlock(double x, double y, double width, double height, U node)
    {
        flameBlocks.add(new FlameBlock<U>(new Rectangle(x, y, width, height), node));
    }

    // UI Helper Methods

    /**
     * Returns the pre-configured {@link GraphicsContext} for the {@link Canvas}.
     *
     * @return the pre-configured {@link GraphicsContext} for the {@link Canvas}
     */
    protected GraphicsContext getGraphics()
    {
        GraphicsContext ctx = getGraphicsContext2D();

        ctx.clearRect(0, 0, getWidth(), getHeight());
        ctx.setStroke(WHITE);
        return ctx;
    }

    /**
     * Renders the {@link Node} or {@link DiffNode}, and recursively renders the descendant nodes.
     *
     * @param ctx the {@link GraphicsContext} in which the node is rendered
     * @param node the node to be rendered
     * @param row the row in which the node is rendered
     * @param columnWidth the width for rendering the node box
     * @param rowHeight the height for rendering the node box
     * @param startX the x coordinate where to render the node
     * @param startY the y coordinate where the entire graph is rendered
     * @return the new x coordinate for rendering the next node in the same row
     */
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

    /**
     * Returns the {@link Color} in the default gradient palette based on the default start and end {@link Color}s, and
     * interpolating based on the row number. If the row number is larger than {@link #BUCKETS}, the gradient wraps
     * round.
     * <p>
     * This is the default colouring scheme for non-diff FlameGraph nodes.
     *
     * @param node the {@link Node} or {@link DiffNode} for which the {@link Color} is being picked
     * @param row the row on which the node is situated
     * @return the corresponding {@link Color} from the default palette
     */
    protected Color colorFor(U node, int row)
    {
        return DEFAUL_START.interpolate(DEFAULT_END, (row % BUCKETS) / (double)BUCKETS);
    }

    /**
     * Converts a percentage to an integer between 0 and {@link #BUCKETS}, with the bucket boundaries being determined
     * by {@link #limit(int)}.
     *
     * @param pct the percentage to be converted
     * @return an integer between 0 and {@link #BUCKETS}
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

    // Internal Implementation Methods

    /**
     * Renders the textual information for the {@link Node} or {@link DiffNode}.
     *
     * @param ctx the {@link GraphicsContext} in which the text is rendered
     * @param color the {@link Color} for rendering the text
     * @param x the x coordinate where the text should be rendered
     * @param y the y coordinate where the text should be rendered
     * @param width the width within which the text should fit
     * @param height the height within which the text should fit
     * @param node the node whose information is to be rendered
     */
    private void renderNodeText(final GraphicsContext ctx, Color color, final double x,
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
    private void renderText(final GraphicsContext ctx, Color color, final double x, final double y,
        double width, double height, String... titles)
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

    /**
     * Returns the upper boundary for the specified bucket number. The boundaries increase logarithmically.
     *
     * @param i the bucket number
     * @return the upper boundary
     */
    private double limit(int i)
    {
        // works well for range 1-20, so rescale from there
        return (exp(i * 0.3) / 1000.) * BUCKETS / 20;
    }

    /**
     * When the mouse enters the region of a {@link FlameBlock}, this method displays a tooltip containing the method
     * name of the {@link Node} or {@link DiffNode} represented by the {@link FlameBlock} being hovered over, and
     * displays more detailed information in the InfoBar.
     * <p>
     * If the mouse leaves a region, the information is hidden or cleared.
     *
     * @param mouseEvent the {@link MouseEvent} triggering the display
     */
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
