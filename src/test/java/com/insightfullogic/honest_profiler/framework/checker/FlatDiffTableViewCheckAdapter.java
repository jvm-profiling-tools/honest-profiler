package com.insightfullogic.honest_profiler.framework.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.FlatDiff;

import javafx.scene.control.TableView;

/**
 * {@link CheckAdapter} for checking a {@link TableView} representing a {@link FlatDiff}.
 */
public class FlatDiffTableViewCheckAdapter implements DiffCheckAdapter<String>
{
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;
    private TableView<?> flatTable;
    private Map<String, Integer> keyToRowIndexMap;

    public FlatDiffTableViewCheckAdapter(ThreadGrouping threadGrouping,
                                         FrameGrouping frameGrouping,
                                         TableView<?> flatTable)
    {
        super();

        this.threadGrouping = threadGrouping;
        this.frameGrouping = frameGrouping;

        this.flatTable = flatTable;
        keyToRowIndexMap = new HashMap<>();

        int i = 0;

        while (!isEmpty((String)flatTable.getColumns().get(0).getCellData(i)))
        {
            keyToRowIndexMap.put((String)flatTable.getColumns().get(0).getCellData(i), i);
            i++;
        }
    }

    private boolean isEmpty(String string)
    {
        return string == null || string.isEmpty();
    }

    @Override
    public ThreadGrouping getThreadGrouping()
    {
        return threadGrouping;
    }

    @Override
    public FrameGrouping getFrameGrouping()
    {
        return frameGrouping;
    }

    @Override
    public void assertSizeEquals(int expected)
    {
        assertEquals(msg("Aggregation size"), expected, flatTable.getItems().size());
        assertEquals(msg("Aggregation size"), expected, keyToRowIndexMap.size());
    }

    @Override
    public void assertBaseSelfCntEquals(String key, int expected)
    {
        assertEquals(
            msg(key, "Base Slf #"),
            expected,
            flatTable.getColumns().get(7).getCellData(row(key)));
    }

    @Override
    public void assertBaseTotalCntEquals(String key, int expected)
    {
        assertEquals(
            msg(key, "Base Tot #"),
            expected,
            flatTable.getColumns().get(10).getCellData(row(key)));
    }

    @Override
    public void assertBaseSelfTimeEquals(String key, long expected)
    {
        assertEquals(
            msg(key, "Base Slf Time"),
            expected,
            flatTable.getColumns().get(19).getCellData(row(key)));
    }

    @Override
    public void assertBaseTotalTimeEquals(String key, long expected)
    {
        assertEquals(
            msg(key, "Base Tot Time"),
            expected,
            flatTable.getColumns().get(22).getCellData(row(key)));
    }

    @Override
    public void assertBaseSelfCntPctEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "Base Slf # %"),
            expected,
            flatTable.getColumns().get(1).getCellData(row(key)));
    }

    @Override
    public void assertBaseTotalCntPctEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "Base Tot # %"),
            expected,
            flatTable.getColumns().get(4).getCellData(row(key)));
    }

    @Override
    public void assertBaseSelfTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "Base Slf Time %"),
            expected,
            flatTable.getColumns().get(13).getCellData(row(key)));
    }

    @Override
    public void assertBaseTotalTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "Base Tot Time %"),
            expected,
            flatTable.getColumns().get(16).getCellData(row(key)));
    }

    // New Checks

    @Override
    public void assertNewSelfCntEquals(String key, int expected)
    {
        assertEquals(
            msg(key, "New Slf #"),
            expected,
            flatTable.getColumns().get(8).getCellData(row(key)));
    }

    @Override
    public void assertNewTotalCntEquals(String key, int expected)
    {
        assertEquals(
            msg(key, "New Tot #"),
            expected,
            flatTable.getColumns().get(11).getCellData(row(key)));
    }

    @Override
    public void assertNewSelfTimeEquals(String key, long expected)
    {
        assertEquals(
            msg(key, "New Slf Time"),
            expected,
            flatTable.getColumns().get(20).getCellData(row(key)));
    }

    @Override
    public void assertNewTotalTimeEquals(String key, long expected)
    {
        assertEquals(
            msg(key, "New Tot Time"),
            expected,
            flatTable.getColumns().get(23).getCellData(row(key)));
    }

    @Override
    public void assertNewSelfCntPctEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "New Slf # %"),
            expected,
            flatTable.getColumns().get(2).getCellData(row(key)));
    }

    @Override
    public void assertNewTotalCntPctEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "New Tot # %"),
            expected,
            flatTable.getColumns().get(5).getCellData(row(key)));
    }

    @Override
    public void assertNewSelfTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "New Slf Time %"),
            expected,
            flatTable.getColumns().get(14).getCellData(row(key)));
    }

    @Override
    public void assertNewTotalTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "New Tot Time %"),
            expected,
            flatTable.getColumns().get(17).getCellData(row(key)));
    }

    // Diff Checks

    @Override
    public void assertSelfCntDiffEquals(String key, int expected)
    {
        assertEquals(
            msg(key, "Slf Diff #"),
            expected,
            flatTable.getColumns().get(9).getCellData(row(key)));
    }

    @Override
    public void assertTotalCntDiffEquals(String key, int expected)
    {
        assertEquals(
            msg(key, "Tot Diff #"),
            expected,
            flatTable.getColumns().get(12).getCellData(row(key)));
    }

    @Override
    public void assertSelfTimeDiffEquals(String key, long expected)
    {
        assertEquals(
            msg(key, "Slf Time Diff"),
            expected,
            flatTable.getColumns().get(21).getCellData(row(key)));
    }

    @Override
    public void assertTotalTimeDiffEquals(String key, long expected)
    {
        assertEquals(
            msg(key, "Total Time Diff"),
            expected,
            flatTable.getColumns().get(24).getCellData(row(key)));
    }

    @Override
    public void assertSelfCntPctDiffEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "Slf # % Diff"),
            expected,
            flatTable.getColumns().get(3).getCellData(row(key)));
    }

    @Override
    public void assertTotalCntPctDiffEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "Tot # % Diff"),
            expected,
            flatTable.getColumns().get(6).getCellData(row(key)));
    }

    @Override
    public void assertSelfTimePctDiffEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "Slf Time % Diff"),
            expected,
            flatTable.getColumns().get(15).getCellData(row(key)));
    }

    @Override
    public void assertTotalTimePctDiffEquals(String key, double expected)
    {
        assertEquals(
            msg(key, "Tot Time % Diff"),
            expected,
            flatTable.getColumns().get(18).getCellData(row(key)));
    }

    private int row(String key)
    {
        Integer rowIndex = keyToRowIndexMap.get(key);
        assertNotNull("Row not found for key " + key, rowIndex);
        return keyToRowIndexMap.get(key);
    }

    private String msg(String value)
    {
        return value + " wrong (" + getThreadGrouping() + ", " + getFrameGrouping() + ")";
    }

    private String msg(String key, String value)
    {
        StringBuilder result = new StringBuilder();
        result.append(
            value
                + " wrong for key "
                + key
                + " ("
                + getThreadGrouping()
                + ", "
                + getFrameGrouping()
                + ")\n");
        for (int i = 0; i < 25; i++)
        {
            result.append(
                "Column "
                    + i
                    + " -> "
                    + flatTable.getColumns().get(i).getCellData(row(key))
                    + "\n");
        }
        return result.toString();
    }
}
