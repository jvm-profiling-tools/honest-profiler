package com.insightfullogic.honest_profiler.framework.checker;

import static java.util.stream.Collectors.summingInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

/**
 * {@link CheckAdapter} for checking a {@link TreeTableView} representing a {@link Tree}.
 */
public class TreeTableViewCheckAdapter implements CheckAdapter<String[]>
{
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;
    private TreeTableView<Node> treeTable;
    private Map<String, Integer> keyToRowIndexMap;

    public TreeTableViewCheckAdapter(ThreadGrouping threadGrouping,
                                     FrameGrouping frameGrouping,
                                     TreeTableView<Node> treeTable)
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

    @Override
    public void assertSelfCntEquals(String[] key, int expected)
    {
        assertEquals(msg("Slf #"), expected, treeTable.getColumns().get(5).getCellData(row(key)));
    }

    @Override
    public void assertTotalCntEquals(String[] key, int expected)
    {
        assertEquals(msg("Tot #"), expected, treeTable.getColumns().get(4).getCellData(row(key)));
    }

    @Override
    public void assertSelfTimeEquals(String[] key, long expected)
    {
        assertEquals(
            msg("Slf Time"),
            expected,
            treeTable.getColumns().get(8).getCellData(row(key)));
    }

    @Override
    public void assertTotalTimeEquals(String[] key, long expected)
    {
        assertEquals(
            msg("Tot Time"),
            expected,
            treeTable.getColumns().get(6).getCellData(row(key)));
    }

    @Override
    public void assertSelfCntPctEquals(String[] key, double expected)
    {
        assertEquals(msg("Slf # %"), expected, treeTable.getColumns().get(3).getCellData(row(key)));
    }

    @Override
    public void assertTotalCntPctEquals(String[] key, double expected)
    {
        assertEquals(msg("Tot # %"), expected, treeTable.getColumns().get(1).getCellData(row(key)));
        assertEquals(msg("Tot # %"), expected, treeTable.getColumns().get(2).getCellData(row(key)));
    }

    @Override
    public void assertSelfTimePctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Slf Time %"),
            expected,
            treeTable.getColumns().get(9).getCellData(row(key)));
    }

    @Override
    public void assertTotalTimePctEquals(String[] key, double expected)
    {
        assertEquals(
            msg("Tot Time %"),
            expected,
            treeTable.getColumns().get(7).getCellData(row(key)));
    }

    private int getSize()
    {
        TreeItem<Node> root = treeTable.getRoot();
        assertNotNull("Root Null", root);

        return root.getChildren().stream().collect(summingInt(child -> getSize(child)));
    }

    private int getSize(TreeItem<Node> item)
    {
        int size = 1;
        size += item.getChildren().stream().collect(summingInt(child -> getSize(child)));
        return size;
    }

    private int row(String[] key)
    {
        return treeTable.getRow(getItem(key));
    }

    private TreeItem<Node> getItem(String[] key)
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

        TreeItem<Node> result = treeTable.getRoot();

        assertNotNull("Item with key " + key[0] + " not found.", result);

        for (int i = 0; i < key.length; i++)
        {
            int itemIndex = 0;
            TreeItem<Node> child = null;
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
