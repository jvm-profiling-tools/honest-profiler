package com.insightfullogic.honest_profiler.framework.checker;

import static java.util.stream.Collectors.summingInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

/**
 * {@link CheckAdapter} for checking a {@link TreeTableView} representing a {@link Tree}.
 */
public class TreeDiffTableViewCheckAdapter implements DiffCheckAdapter<String[]>
{
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;
    private TreeTableView<DiffNode> treeTable;
    private Map<String, Integer> keyToRowIndexMap;

    public TreeDiffTableViewCheckAdapter(ThreadGrouping threadGrouping,
                                         FrameGrouping frameGrouping,
                                         TreeTableView<DiffNode> treeTable)
    {
        super();

        this.threadGrouping = threadGrouping;
        this.frameGrouping = frameGrouping;

        this.treeTable = treeTable;
        keyToRowIndexMap = new HashMap<>();

        int i = 0;

        while (!isEmpty((String)treeTable.getColumns().get(0).getCellData(i)))
        {
            keyToRowIndexMap.put((String)treeTable.getColumns().get(0).getCellData(i), i);
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
        assertEquals(msg("Aggregation size"), expected, getSize());
    }

    // Base Checks

    @Override
    public void assertBaseSelfCntEquals(String[] key, int expected)
    {
        assertEquals(
            msg("Base Slf #"),
            expected,
            treeTable.getColumns().get(7).getCellData(row(key)));
    }

    @Override
    public void assertBaseTotalCntEquals(String[] key, int expected)
    {
        assertEquals(
            msg("Base Tot#"),
            expected,
            treeTable.getColumns().get(10).getCellData(row(key)));
    }

    @Override
    public void assertBaseSelfTimeEquals(String[] key, long expected)
    {
        assertEquals(
            msg("Base Slf Time"),
            expected,
            treeTable.getColumns().get(19).getCellData(row(key)));
    }

    @Override
    public void assertBaseTotalTimeEquals(String[] key, long expected)
    {
        assertEquals(
            msg("Base TotTime"),
            expected,
            treeTable.getColumns().get(22).getCellData(row(key)));
    }

    @Override
    public void assertBaseSelfCntPctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Base Slf # %"),
            expected,
            treeTable.getColumns().get(1).getCellData(row(key)));
    }

    @Override
    public void assertBaseTotalCntPctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Base Tot# %"),
            expected,
            treeTable.getColumns().get(4).getCellData(row(key)));
    }

    @Override
    public void assertBaseSelfTimePctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Base Slf Time %"),
            expected,
            treeTable.getColumns().get(13).getCellData(row(key)));
    }

    @Override
    public void assertBaseTotalTimePctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Base Tot Time %"),
            expected,
            treeTable.getColumns().get(16).getCellData(row(key)));
    }

    // New Checks

    @Override
    public void assertNewSelfCntEquals(String[] key, int expected)
    {
        assertEquals(
            msg("New Slf #"),
            expected,
            treeTable.getColumns().get(8).getCellData(row(key)));
    }

    @Override
    public void assertNewTotalCntEquals(String[] key, int expected)
    {
        assertEquals(
            msg("New Tot #"),
            expected,
            treeTable.getColumns().get(11).getCellData(row(key)));
    }

    @Override
    public void assertNewSelfTimeEquals(String[] key, long expected)
    {
        assertEquals(
            msg("New Slf Time"),
            expected,
            treeTable.getColumns().get(20).getCellData(row(key)));
    }

    @Override
    public void assertNewTotalTimeEquals(String[] key, long expected)
    {
        assertEquals(
            msg("New Tot Time"),
            expected,
            treeTable.getColumns().get(23).getCellData(row(key)));
    }

    @Override
    public void assertNewSelfCntPctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("New Slf # %"),
            expected,
            treeTable.getColumns().get(2).getCellData(row(key)));
    }

    @Override
    public void assertNewTotalCntPctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("New Tot # %"),
            expected,
            treeTable.getColumns().get(5).getCellData(row(key)));
    }

    @Override
    public void assertNewSelfTimePctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("New Slf Time %"),
            expected,
            treeTable.getColumns().get(14).getCellData(row(key)));
    }

    @Override
    public void assertNewTotalTimePctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("New Tot Time %"),
            expected,
            treeTable.getColumns().get(17).getCellData(row(key)));
    }

    // Diff Checks

    @Override
    public void assertSelfCntDiffEquals(String[] key, int expected)
    {
        assertEquals(
            msg("Slf # Diff"),
            expected,
            treeTable.getColumns().get(9).getCellData(row(key)));
    }

    @Override
    public void assertTotalCntDiffEquals(String[] key, int expected)
    {
        assertEquals(
            msg("Tot # Diff"),
            expected,
            treeTable.getColumns().get(12).getCellData(row(key)));
    }

    @Override
    public void assertSelfTimeDiffEquals(String[] key, long expected)
    {
        assertEquals(
            msg("Slf Time Diff"),
            expected,
            treeTable.getColumns().get(21).getCellData(row(key)));
    }

    @Override
    public void assertTotalTimeDiffEquals(String[] key, long expected)
    {
        assertEquals(
            msg("Tot Time Diff"),
            expected,
            treeTable.getColumns().get(24).getCellData(row(key)));
    }

    @Override
    public void assertSelfCntPctDiffEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Slf # % Diff"),
            expected,
            treeTable.getColumns().get(3).getCellData(row(key)));
    }

    @Override
    public void assertTotalCntPctDiffEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Tot # % Diff"),
            expected,
            treeTable.getColumns().get(6).getCellData(row(key)));
    }

    @Override
    public void assertSelfTimePctDiffEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Slf Time % Diff"),
            expected,
            treeTable.getColumns().get(15).getCellData(row(key)));
    }

    @Override
    public void assertTotalTimePctDiffEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Tot Time % Diff"),
            expected,
            treeTable.getColumns().get(18).getCellData(row(key)));
    }

    private int getSize()
    {
        TreeItem<DiffNode> root = treeTable.getRoot();
        assertNotNull("Root Null", root);

        return root.getChildren().stream().collect(summingInt(child -> getSize(child)));
    }

    private int getSize(TreeItem<DiffNode> item)
    {
        int size = 1;
        size += item.getChildren().stream().collect(summingInt(child -> getSize(child)));
        return size;
    }

    private int row(String[] key)
    {
        return treeTable.getRow(getItem(key));
    }

    private TreeItem<DiffNode> getItem(String[] key)
    {
        // int itemIndex = 0;
        //
        // TreeItem<Node> result = treeTable.getTreeItem(itemIndex);
        // while (result != null
        // && result.getValue() != null
        // && result.getValue().getKey().equals(key[0]))
        // {
        // result = treeTable.getTreeItem(++itemIndex);
        // }

        TreeItem<DiffNode> result = treeTable.getRoot();

        assertNotNull("Item with key " + key[0] + " not found.", result);

        for (int i = 0; i < key.length; i++)
        {
            int itemIndex = 0;
            TreeItem<DiffNode> child = null;
            while (child == null && itemIndex < result.getChildren().size())
            {
                child = result.getChildren().get(itemIndex++);
                if (child.getValue().getKey().equals(key[i]))
                {
                    result = child;
                }
                else
                {
                    child = null;
                }
            }
            assertNotNull("Item at level " + i + " with key " + key[i] + " not found.", child);
        }

        return result;
    }

    private String msg(String value)
    {
        return value + " wrong (" + getThreadGrouping() + ", " + getFrameGrouping() + ")";
    }
}
