package com.insightfullogic.honest_profiler.ports.javafx.framework;

import static com.insightfullogic.honest_profiler.framework.AggregationUtil.nano;
import static org.junit.Assert.assertEquals;

import javafx.scene.control.TableView;

public class HPFXAssert
{
    public static final void assertFlatViewContains(TableView<?> table, String key, int selfCnt,
        int totalCnt, long refCnt)
    {
        assertFlatViewContains(table, key, selfCnt, totalCnt, selfCnt, totalCnt, refCnt, refCnt);
    }

    public static final void assertFlatViewContains(TableView<?> table, String key, int selfCnt,
        int totalCnt, int selfTime, int totalTime, long refCnt, long refTime)
    {
        int i = 0;
        while (!key.equals(table.getColumns().get(0).getCellData(i)))
        {
            i++;
        }

        // Method
        assertEquals(key, table.getColumns().get(0).getCellData(i));
        // Self % Graphical
        assertEquals(((double)selfCnt) / refCnt, table.getColumns().get(1).getCellData(i));
        // Self # %
        assertEquals(((double)selfCnt) / refCnt, table.getColumns().get(2).getCellData(i));
        // Total # %
        assertEquals(((double)totalCnt) / refCnt, table.getColumns().get(3).getCellData(i));
        // Self #
        assertEquals(selfCnt, table.getColumns().get(4).getCellData(i));
        // Total #
        assertEquals(totalCnt, table.getColumns().get(5).getCellData(i));
        // Self Time
        assertEquals(nano(selfTime), table.getColumns().get(6).getCellData(i));
        // Self Time %
        assertEquals(((double)selfTime) / refTime, table.getColumns().get(7).getCellData(i));
        // Total Time
        assertEquals(nano(totalTime), table.getColumns().get(8).getCellData(i));
        // Total Time %
        assertEquals(((double)totalTime) / refTime, table.getColumns().get(9).getCellData(i));
    }
}
