package com.insightfullogic.honest_profiler.framework.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

/**
 * {@link CheckAdapter} for checking a {@link Tree} directly.
 */
public class TreeDiffCheckAdapter implements DiffCheckAdapter<String[]>
{
    private TreeDiff tree;

    public TreeDiffCheckAdapter(TreeDiff tree)
    {
        this.tree = tree;
    }

    @Override
    public ThreadGrouping getThreadGrouping()
    {
        return tree.getBaseAggregation().getGrouping().getThreadGrouping();
    }

    @Override
    public FrameGrouping getFrameGrouping()
    {
        return tree.getBaseAggregation().getGrouping().getFrameGrouping();
    }

    @Override
    public void assertSizeEquals(int expected)
    {
        assertEquals(msg("Size"), expected, tree.flatten().count());
    }

    // Base Checks

    @Override
    public void assertBaseSelfCntEquals(String[] keys, int expected)
    {
        assertEquals(msg(keys + ": Slf #"), expected, getNode(keys).getBaseSelfCnt());
    }

    @Override
    public void assertBaseTotalCntEquals(String[] keys, int expected)
    {
        assertEquals(msg(keys + ": Tot #"), expected, getNode(keys).getBaseTotalCnt());
    }

    @Override
    public void assertBaseSelfTimeEquals(String[] keys, long expected)
    {
        assertEquals(msg(keys + ": Slf Time"), expected, getNode(keys).getBaseSelfTime());
    }

    @Override
    public void assertBaseTotalTimeEquals(String[] keys, long expected)
    {
        assertEquals(msg(keys + ": Tot Time"), expected, getNode(keys).getBaseTotalTime());
    }

    @Override
    public void assertBaseSelfCntPctEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Slf # %"), expected, getNode(keys).getBaseSelfCntPct(), .00001);
    }

    @Override
    public void assertBaseTotalCntPctEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Tot # %"), expected, getNode(keys).getBaseTotalCntPct(), .00001);
    }

    @Override
    public void assertBaseSelfTimePctEquals(String[] keys, double expected)
    {
        assertEquals(
            msg(keys + ": Slf Time %"),
            expected,
            getNode(keys).getBaseSelfTimePct(),
            .00001);
    }

    @Override
    public void assertBaseTotalTimePctEquals(String[] keys, double expected)
    {
        assertEquals(
            msg(keys + ": Tot Time %"),
            expected,
            getNode(keys).getBaseTotalTimePct(),
            .00001);
    }

    // New Checks

    @Override
    public void assertNewSelfCntEquals(String[] keys, int expected)
    {
        assertEquals(msg(keys + ": Slf #"), expected, getNode(keys).getNewSelfCnt());
    }

    @Override
    public void assertNewTotalCntEquals(String[] keys, int expected)
    {
        assertEquals(msg(keys + ": Tot #"), expected, getNode(keys).getNewTotalCnt());
    }

    @Override
    public void assertNewSelfTimeEquals(String[] keys, long expected)
    {
        assertEquals(msg(keys + ": Slf Time"), expected, getNode(keys).getNewSelfTime());
    }

    @Override
    public void assertNewTotalTimeEquals(String[] keys, long expected)
    {
        assertEquals(msg(keys + ": Tot Time"), expected, getNode(keys).getNewTotalTime());
    }

    @Override
    public void assertNewSelfCntPctEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Slf # %"), expected, getNode(keys).getNewSelfCntPct(), .00001);
    }

    @Override
    public void assertNewTotalCntPctEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Tot # %"), expected, getNode(keys).getNewTotalCntPct(), .00001);
    }

    @Override
    public void assertNewSelfTimePctEquals(String[] keys, double expected)
    {
        assertEquals(
            msg(keys + ": Slf Time %"),
            expected,
            getNode(keys).getNewSelfTimePct(),
            .00001);
    }

    @Override
    public void assertNewTotalTimePctEquals(String[] keys, double expected)
    {
        assertEquals(
            msg(keys + ": Tot Time %"),
            expected,
            getNode(keys).getNewTotalTimePct(),
            .00001);
    }

    // Diff Checks

    @Override
    public void assertSelfCntDiffEquals(String[] keys, int expected)
    {
        assertEquals(msg(keys + ": Slf #"), expected, getNode(keys).getSelfCntDiff());
    }

    @Override
    public void assertTotalCntDiffEquals(String[] keys, int expected)
    {
        assertEquals(msg(keys + ": Tot #"), expected, getNode(keys).getTotalCntDiff());
    }

    @Override
    public void assertSelfTimeDiffEquals(String[] keys, long expected)
    {
        assertEquals(msg(keys + ": Slf Time"), expected, getNode(keys).getSelfTimeDiff());
    }

    @Override
    public void assertTotalTimeDiffEquals(String[] keys, long expected)
    {
        assertEquals(msg(keys + ": Tot Time"), expected, getNode(keys).getTotalTimeDiff());
    }

    @Override
    public void assertSelfCntPctDiffEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Slf # %"), expected, getNode(keys).getSelfCntPctDiff(), .00001);
    }

    @Override
    public void assertTotalCntPctDiffEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Tot # %"), expected, getNode(keys).getTotalCntPctDiff(), .00001);
    }

    @Override
    public void assertSelfTimePctDiffEquals(String[] keys, double expected)
    {
        assertEquals(
            msg(keys + ": Slf Time %"),
            expected,
            getNode(keys).getSelfTimePctDiff(),
            .00001);
    }

    @Override
    public void assertTotalTimePctDiffEquals(String[] keys, double expected)
    {
        assertEquals(
            msg(keys + ": Tot Time %"),
            expected,
            getNode(keys).getTotalTimePctDiff(),
            .00001);
    }

    private String msg(String value)
    {
        return value
            + " wrong ("
            + getThreadGrouping()
            + ", "
            + getFrameGrouping()
            + ")\n"
            + tree.toString();
    }

    private DiffNode getNode(String... keys)
    {
        if (keys == null || keys.length == 0)
        {
            return null;
        }

        List<DiffNode> children = tree.getData();
        Optional<DiffNode> result = null;

        for (String key : keys)
        {
            result = children.stream().filter(node -> key.equals(node.getKey())).findFirst();

            if (!result.isPresent())
            {
                fail(
                    "Node not found in Tree : Keys = "
                        + Arrays.toString(keys)
                        + "\n"
                        + tree.toString());
                return null;
            }

            children = result.get().getChildren();
        }
        return result.get();
    }
}
