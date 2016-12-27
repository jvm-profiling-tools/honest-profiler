package com.insightfullogic.honest_profiler.ports.javafx.util.report;

import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.LEFT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.RIGHT;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Table
{
    public static enum Alignment
    {
        LEFT,
        RIGHT
    }

    private List<String> columnTitles;
    private List<Function<Object, String>> columnFormatters;
    private List<Alignment> columnAlignments;
    private List<Integer> columnValueMaxWidths;

    private List<List<String>> rows;

    public Table()
    {
        columnTitles = new ArrayList<>();
        columnFormatters = new ArrayList<>();
        columnAlignments = new ArrayList<>();
        columnValueMaxWidths = new ArrayList<>();
        rows = new ArrayList<>();
    }

    public void addColumn(String title, Function<Object, String> formatter, Alignment alignment)
    {
        columnTitles.add(title);
        columnFormatters.add(formatter);
        columnAlignments.add(alignment);
        columnValueMaxWidths.add(title.length());
    }

    public void addRow(Object... data)
    {
        if (data == null || data.length != columnTitles.size())
        {
            throw new RuntimeException(
                "Incorrect number of column entries when trying to add data to a table : "
                    + columnTitles.size()
                    + " columns defined, but adding "
                    + (data == null ? 0 : data.length)
                    + " column entries.");
        }

        List<String> row = new ArrayList<>();
        for (int i = 0; i < columnFormatters.size(); i++)
        {
            String columnEntry = columnFormatters.get(i).apply(data[i]);
            if (columnEntry == null)
            {
                columnEntry = "<NULL>";
            }

            row.add(columnEntry);

            if (columnValueMaxWidths.get(i) < columnEntry.length())
            {
                columnValueMaxWidths.set(i, columnEntry.length());
            }
        }
        rows.add(row);
    }

    public void print(PrintWriter out)
    {
        int width = columnValueMaxWidths.stream().mapToInt(i -> i).sum()
            + (columnTitles.size() * 2)
            + 2;
        printLine(out, width);
        printRow(out, columnTitles, LEFT);
        printLine(out, width);
        rows.forEach(row -> printRow(out, row));
        printLine(out, width);
    }

    private void printRow(PrintWriter out, List<String> entries)
    {
        printRow(out, entries, null);
    }

    private void printRow(PrintWriter out, List<String> entries, Alignment alignment)
    {
        out.print("|");

        for (int i = 0; i < columnTitles.size(); i++)
        {
            out.print(" ");
            out.print(pad(
                entries.get(i),
                columnValueMaxWidths.get(i),
                alignment == null ? columnAlignments.get(i) : alignment));
            out.print(" |");
        }

        out.println();
    }

    private void printLine(PrintWriter out, int width)
    {
        for (int i = 0; i < width; i++)
        {
            out.print("-");
        }
        out.println();
    }

    private String pad(String string, int width, Alignment alignment)
    {
        StringBuilder result = new StringBuilder();

        if (alignment == RIGHT)
        {
            for (int i = 0; i < width - string.length(); i++)
            {
                result.append(" ");
            }
        }
        result.append(string);
        if (alignment == LEFT)
        {
            for (int i = 0; i < width - string.length(); i++)
            {
                result.append(" ");
            }
        }
        return result.toString();
    }
}
