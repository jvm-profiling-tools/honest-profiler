package com.insightfullogic.honest_profiler.ports.javafx.util.report;

import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.LEFT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.RIGHT;
import static java.text.NumberFormat.getPercentInstance;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.FlatDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

/**
 * Utility class for creating various text reports (either ASCII-art based or CSV) based on profile data.
 */
public final class ReportUtil
{
    // Class Properties

    public static enum Mode
    {
        TEXT("", " ", ""), CSV("\"", "\",\"", "\"");

        private String start;
        private String middle;
        private String end;

        private Mode(String start, String middle, String end)
        {
            this.start = start;
            this.middle = middle;
            this.end = end;
        }

        private PrintWriter start(PrintWriter out)
        {
            out.print(start);
            return out;
        }

        private PrintWriter middle(PrintWriter out)
        {
            out.print(middle);
            return out;
        }

        private PrintWriter end(PrintWriter out)
        {
            out.println(end);
            return out;
        }
    }

    // - Special characters

    private static char BOX_HORIZONTAL = 0x2500;
    private static char BOX_VERTICAL = 0x2502;
    private static char BOX_UPANDRIGHT = 0x2514;
    private static char BOX_VERTICALANDRIGHT = 0x251C;

    // - Predefined character sequences
    private static String DROP_STRAIGHT = new String(new char[]
    { BOX_VERTICAL, ' ' });
    private static String DROP_NONLAST = new String(new char[]
    { BOX_VERTICALANDRIGHT, BOX_HORIZONTAL });
    private static String DROP_LAST = new String(new char[]
    { BOX_UPANDRIGHT, BOX_HORIZONTAL });

    // - Formatter which renders percentages with 2 digits after the dot.
    private static NumberFormat FMT_PERCENT;

    // Class Constructors

    // - Initialize the FMT_PERCENT NumberFormat
    static
    {
        FMT_PERCENT = getPercentInstance();
        FMT_PERCENT.setMinimumFractionDigits(2);
        FMT_PERCENT.setMaximumFractionDigits(2);
    }

    /**
     * Writes a stack (fragment) with the specified {@link Node} as root to the specified {@link PrintWriter}. Nicely
     * formatted at that, using droplines.
     * <p>
     * TODO FIX - {@link ProfileNode}s are no longer used.
     * <p>
     *
     * @param out the {@link PrintWriter} to wite the stack to
     * @param node the root {@link Node} of the stack (fragment)
     */
    public static void writeStack(PrintWriter out, Node node)
    {
        Table table = new Table();
        table.addColumn("Method", obj -> obj.toString(), LEFT);
        table.addColumn("Self Time %", object -> FMT_PERCENT.format(object), RIGHT);
        table.addColumn("Total Time %", object -> FMT_PERCENT.format(object), RIGHT);
        table.addColumn("Self Sample #", object -> Integer.toString((Integer)object), RIGHT);
        table.addColumn("Total Sample #", object -> Integer.toString((Integer)object), RIGHT);

        buildStackTable(node, 0, table, new ArrayList<>());
        table.print(out);
    }

    /**
     * Helper method which recursively adds {@link Node}s into a {@link Table}
     * <p>
     * TODO FIX - {@link ProfileNode}s are no longer used.
     * <p>
     *
     * @param node the root {@link Node} being added to the {@link Table}
     * @param level the depth the root {@link Node} is at
     * @param table the {@link Table} the {@link Node} will be added to
     * @param dropLines a {@link List} containing the {@link DropLine}s to be added left of the {@link Node}
     *            information.
     */
    private static void buildStackTable(Node node, int level, Table table, List<DropLine> dropLines)
    {
        table.addRow(
            indent(dropLines, level) + node.getKey(),
            node.getSelfTimePct(),
            node.getTotalTimePct(),
            node.getSelfCnt(),
            node.getTotalCnt());

        if (level > 0)
        {
            dropLines.get(level - 1).decrement();
        }

        List<Node> children = node.getChildren();

        if (children.size() > 0)
        {
            dropLines.add(new DropLine(children.size()));
            children.forEach(child -> buildStackTable(child, level + 1, table, dropLines));
            dropLines.remove(level);
        }
    }

    // public static void writeThread(PrintWriter out, ProfileTree thread, Mode mode)
    // {
    // // TODO Implement
    // }

    /**
     * Write the contents of a {@link Flat} aggregation to a CSV or text file, depending on the specified {@link Mode}.
     * <p>
     *
     * @param out the {@link PrintWriter} to wite the data to
     * @param entries the data to be written
     * @param mode the {@link Mode} for formatting the output
     */
    public static void writeFlatProfileCsv(PrintWriter out, List<Entry> entries, Mode mode)
    {
        mode.start(out);
        out.print("Key");

        mode.middle(out);
        out.print("Self %");
        mode.middle(out);
        out.print("Total %");

        mode.middle(out);
        out.print("Self #");
        mode.middle(out);
        out.print("Total #");
        mode.end(out);

        entries.forEach(entry ->
        {
            mode.start(out);
            out.print(entry.getKey());

            mode.middle(out);
            out.printf("%.4f", entry.getSelfCntPct());
            mode.middle(out);
            out.printf("%.4f", entry.getTotalCntPct());

            mode.middle(out);
            out.printf("%d", entry.getSelfCnt());
            mode.middle(out);
            out.printf("%d", entry.getTotalCnt());
            mode.end(out);
        });

        out.flush();
    }

    /**
     * Write the contents of a {@link FlatDiff} aggregation to a CSV or text file, depending on the specified
     * {@link Mode}.
     * <p>
     *
     * @param out the {@link PrintWriter} to wite the data to
     * @param entries the data to be written
     * @param mode the {@link Mode} for formatting the output
     */
    public static void writeFlatProfileDiffCsv(PrintWriter out, Collection<DiffEntry> entries,
        Mode mode)
    {
        mode.start(out);
        out.print("Key");

        mode.middle(out);
        out.print("Base Self %");
        mode.middle(out);
        out.print("New Self %");
        mode.middle(out);
        out.print("Self % Diff");

        mode.middle(out);
        out.print("Base Total %");
        mode.middle(out);
        out.print("New Total %");
        mode.middle(out);
        out.print("Total % Diff");

        mode.middle(out);
        out.print("Base Self #");
        mode.middle(out);
        out.print("New Self #");
        mode.middle(out);
        out.print("Self # Diff");

        mode.middle(out);
        out.print("Base Total #");
        mode.middle(out);
        out.print("New Total #");
        mode.middle(out);
        out.print("Total # Diff");

        mode.end(out);

        entries.forEach(entry ->
        {
            mode.start(out);
            out.write(entry.getKey());

            mode.middle(out);
            out.printf("%.4f", entry.getBaseSelfCntPct());
            mode.middle(out);
            out.printf("%.4f", entry.getNewSelfCntPct());
            mode.middle(out);
            out.printf("%.4f", entry.getSelfCntPctDiff());

            mode.middle(out);
            out.printf("%.4f", entry.getBaseTotalCntPct());
            mode.middle(out);
            out.printf("%.4f", entry.getNewTotalCntPct());
            mode.middle(out);
            out.printf("%.4f", entry.getTotalCntPctDiff());

            mode.middle(out);
            out.printf("%d", entry.getBaseSelfCnt());
            mode.middle(out);
            out.printf("%d", entry.getNewSelfCnt());
            mode.middle(out);
            out.printf("%d", entry.getSelfCntDiff());

            mode.middle(out);
            out.printf("%d", entry.getBaseTotalCnt());
            mode.middle(out);
            out.printf("%d", entry.getNewTotalCnt());
            mode.middle(out);
            out.printf("%d", entry.getTotalCntDiff());

            mode.end(out);
        });

        out.flush();
    }

    /**
     * Internal helper method for indenting stack frames, preceded by the specified {@link DropLine}s.
     * <p>
     *
     * @param dropLines the {@link DropLine}s to be rendered
     * @param level the indentation level
     * @return a String containing the rendered {@link DropLine}s and whitespace to be used as prefix for indenting the
     *         stack frame information.
     */
    private static String indent(List<DropLine> dropLines, int level)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < level - 1; i++)
        {
            result.append(dropLines.get(i).childrenLeft > 0 ? DROP_STRAIGHT : "  ");
        }

        if (level > 0)
        {
            result.append(dropLines.get(level - 1).childrenLeft > 1 ? DROP_NONLAST : DROP_LAST);
        }

        return result.toString();
    }

    /**
     * Helper class which manages the offset of a dropline to be rendered.
     * <p>
     * TODO Unfortunately I've completely forgotten how the algorithm I came up with works. Figure it out sometime and
     * update the documentation accordingly.
     */
    private static class DropLine
    {
        // Instance Properties

        private int childrenLeft;

        /**
         * Constructor.
         * <p>
         *
         * @param childrenLeft some parameter
         */
        private DropLine(int childrenLeft)
        {
            this.childrenLeft = childrenLeft;
        }

        /**
         * Decrement method.
         */
        private void decrement()
        {
            childrenLeft--;
        }
    }

    /**
     * Empty Constructor for utility class.
     */
    private ReportUtil()
    {
        // Empty Constructor for utility class
    }
}
