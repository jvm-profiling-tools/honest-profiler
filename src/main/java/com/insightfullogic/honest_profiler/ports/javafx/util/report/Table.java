package com.insightfullogic.honest_profiler.ports.javafx.util.report;

import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.LEFT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.Table.Alignment.RIGHT;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Simple table data structure which can be formatted nicely and written to a {@link PrintWriter}, with borders and
 * everyting.
 * <p>
 * The data is internally modeled as a {@link List} of rows which are themselves {@link List}s of String column values.
 * The input data consists of {@link List}s of {@link Object}s, which are transformed by formatting functions attached
 * to the columns, which transform the {@link Object} into a String.
 */
public class Table
{
    // Class Properties

    /** Enumeration of possible alignments of values in a column. */
    public static enum Alignment
    {
        LEFT, RIGHT
    }

    // Instance Properties

    private List<String> columnTitles;
    private List<Function<Object, String>> columnFormatters;
    private List<Alignment> columnAlignments;
    private List<Integer> columnValueMaxWidths;

    private List<List<String>> rows;

    // Instance Constructors

    /**
     * Construct an empty Table.
     */
    public Table()
    {
        columnTitles = new ArrayList<>();
        columnFormatters = new ArrayList<>();
        columnAlignments = new ArrayList<>();
        columnValueMaxWidths = new ArrayList<>();
        rows = new ArrayList<>();
    }

    /**
     * Add a column specification to the Table, consisting of a title (or header) for the column, a formatting
     * {@link Function} to format a data {@link Object} belonging to the column to a String, and an {@link Alignment}.
     * <p>
     * @param title the column header
     * @param formatter a {@link Function} which maps data {@link Object}s in this column to Strings
     * @param alignment the alignment for the column in the output
     */
    public void addColumn(String title, Function<Object, String> formatter, Alignment alignment)
    {
        columnTitles.add(title);
        columnFormatters.add(formatter);
        columnAlignments.add(alignment);
        columnValueMaxWidths.add(title.length());
    }

    /**
     * Adds a data row to the {@link Table}. The number of data {@link Object}s must match the number of columns which
     * were added to the Table. null data {@link Object}s are rendered as "&lt;NULL&gt;".
     * <p>
     * @param data The {@link Object}s in the row, one for each column
     * @throws RuntimeException if the number of data {@link Object}s doesn't match the number of columns in the Table
     */
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

    /**
     * Prints the formatted Table to the specified {@link PrintWriter}.
     * <p>
     * @param out the {@link PrintWriter} the Table is printed to
     */
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

    // Internal Helper Methods

    /**
     * Prints a row, using the {@link Alignment} as specified by the column definitions.
     * <p>
     * @param out the {@link PrintWriter} the Table is printed to
     * @param entries the {@link List} of String entries to be printed
     */
    private void printRow(PrintWriter out, List<String> entries)
    {
        printRow(out, entries, null);
    }

    /**
     * Prints a row, overriding the {@link Alignment} as specified by the column definitions if the specified
     * {@link Alignment} is not null.
     * <p>
     * @param out the {@link PrintWriter} the Table is printed to
     * @param entries the {@link List} of String entries to be printed
     * @param alignment an {@link Alignment} overriding the defined column alignments
     */
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

    /**
     * Prints a line of dashes.
     * <p>
     * @param out the {@link PrintWriter} the Table is printed to
     * @param width the number of dashes to be printed
     */
    private void printLine(PrintWriter out, int width)
    {
        for (int i = 0; i < width; i++)
        {
            out.print("-");
        }
        out.println();
    }

    /**
     * Returns a String constructed by padding the input String to the specified width and aligning the input according
     * to the specified {@link Alignment}.
     * <p>
     * @param string the input String
     * @param width the width of the resulting String
     * @param alignment the alignment of the input String in the result String
     * @return the constructed String
     */
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
