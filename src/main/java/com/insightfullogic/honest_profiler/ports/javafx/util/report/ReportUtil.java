package com.insightfullogic.honest_profiler.ports.javafx.util.report;

import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.LEFT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.RIGHT;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;

public final class ReportUtil
{

    private static char BOX_HORIZONTAL = 0x2500;
    private static char BOX_VERTICAL = 0x2502;
    private static char BOX_UPANDRIGHT = 0x2514;
    private static char BOX_VERTICALANDRIGHT = 0x251C;

    private static String DROP_STRAIGHT = new String(new char[]
    { BOX_VERTICAL, ' ' });
    private static String DROP_NONLAST = new String(new char[]
    { BOX_VERTICALANDRIGHT, BOX_HORIZONTAL });
    private static String DROP_LAST = new String(new char[]
    { BOX_UPANDRIGHT, BOX_HORIZONTAL });

    private static NumberFormat FMT_PERCENT;

    static
    {
        FMT_PERCENT = NumberFormat.getPercentInstance();
        FMT_PERCENT.setMinimumFractionDigits(2);
        FMT_PERCENT.setMaximumFractionDigits(2);
    }

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

    public static void writeStack(PrintWriter out, ProfileNode node)
    {
        Table table = new Table();
        table.addColumn("Method", obj -> obj.toString(), LEFT);
        table.addColumn("Self Time Share", object -> FMT_PERCENT.format(object), RIGHT);
        table.addColumn("Total Time Share", object -> FMT_PERCENT.format(object), RIGHT);

        buildStackTable(node, 0, table, new ArrayList<>());
        table.print(out);
    }

    private static void buildStackTable(ProfileNode node, int level, Table table,
        List<DropLine> dropLines)
    {
        table.addRow(
            indent(dropLines, level)
                + node.getFrameInfo().getClassName()
                + "."
                + node.getFrameInfo().getMethodName(),
            node.getSelfTimeShare(),
            node.getTotalTimeShare());

        if (level > 0)
        {
            dropLines.get(level - 1).decrement();
        }

        List<ProfileNode> children = node.getChildren();

        if (children.size() > 0)
        {
            dropLines.add(new DropLine(children.size()));
            children.forEach(child -> buildStackTable(child, level + 1, table, dropLines));
            dropLines.remove(level);
        }
    }

    public static void writeThread(PrintWriter out, ProfileTree thread, Mode mode)
    {
        // TODO Implement
    }

    public static void writeFlatProfileCsv(PrintWriter out, List<Entry<String>> entries,
        Mode mode)
    {
        mode.start(out);
        out.print("Method");

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

    public static void writeFlatProfileDiffCsv(PrintWriter out,
        Collection<DiffEntry<String>> entries, Mode mode)
    {
        mode.start(out);
        out.print("Method");

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

    private static class DropLine
    {
        private int childrenLeft;

        private DropLine(int childrenLeft)
        {
            this.childrenLeft = childrenLeft;
        }

        private void decrement()
        {
            childrenLeft--;
        }
    }

    private ReportUtil()
    {
        // Empty Constructor for utility class
    }
}
