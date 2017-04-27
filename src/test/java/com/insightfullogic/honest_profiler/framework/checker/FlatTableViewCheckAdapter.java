package com.insightfullogic.honest_profiler.framework.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;

import javafx.scene.control.TableView;

/**
 * {@link CheckAdapter} for checking a {@link TableView} representing a {@link Flat}.
 */
public class FlatTableViewCheckAdapter implements CheckAdapter<String>
{
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;
    private TableView<?> flatTable;
    private Map<String, Integer> keyToRowIndexMap;

    public FlatTableViewCheckAdapter(ThreadGrouping threadGrouping,
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
    public void assertSelfCntEquals(String key, int expected)
    {
        assertEquals(msg("Slf #"), expected, flatTable.getColumns().get(4).getCellData(row(key)));
    }

    @Override
    public void assertTotalCntEquals(String key, int expected)
    {
        assertEquals(msg("Tot #"), expected, flatTable.getColumns().get(5).getCellData(row(key)));
    }

    @Override
    public void assertSelfTimeEquals(String key, long expected)
    {
        assertEquals(
            msg("Slf Time"),
            expected,
            flatTable.getColumns().get(6).getCellData(row(key)));
    }

    @Override
    public void assertTotalTimeEquals(String key, long expected)
    {
        assertEquals(
            msg("Total Time"),
            expected,
            flatTable.getColumns().get(8).getCellData(row(key)));
    }

    @Override
    public void assertSelfCntPctEquals(String key, double expected)
    {
        assertEquals(msg("Slf # %"), expected, flatTable.getColumns().get(1).getCellData(row(key)));
        assertEquals(msg("Slf # %"), expected, flatTable.getColumns().get(2).getCellData(row(key)));
    }

    @Override
    public void assertTotalCntPctEquals(String key, double expected)
    {
        assertEquals(msg("Tot # %"), expected, flatTable.getColumns().get(3).getCellData(row(key)));
    }

    @Override
    public void assertSelfTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg("Slf Time %"),
            expected,
            flatTable.getColumns().get(7).getCellData(row(key)));
    }

    @Override
    public void assertTotalTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg("Tot Time %"),
            expected,
            flatTable.getColumns().get(9).getCellData(row(key)));
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
}
