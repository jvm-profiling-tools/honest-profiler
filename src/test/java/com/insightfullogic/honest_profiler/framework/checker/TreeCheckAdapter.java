package com.insightfullogic.honest_profiler.framework.checker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

/**
 * {@link CheckAdapter} for checking a {@link Tree} directly.
 */
public class TreeCheckAdapter implements CheckAdapter<String[]>
{
    private Tree tree;

    public TreeCheckAdapter(Tree tree)
    {
        this.tree = tree;
    }

    @Override
    public ThreadGrouping getThreadGrouping()
    {
        return tree.getGrouping().getThreadGrouping();
    }

    @Override
    public FrameGrouping getFrameGrouping()
    {
        return tree.getGrouping().getFrameGrouping();
    }

    @Override
    public void assertSizeEquals(int expected)
    {
        assertEquals(msg("Size"), expected, tree.flatten().count());
    }

    @Override
    public void assertSelfCntEquals(String[] keys, int expected)
    {
        assertEquals(msg(keys + ": Slf #"), expected, getNode(keys).getSelfCnt());
    }

    @Override
    public void assertTotalCntEquals(String[] keys, int expected)
    {
        assertEquals(msg(keys + ": Tot #"), expected, getNode(keys).getTotalCnt());
    }

    @Override
    public void assertSelfTimeEquals(String[] keys, long expected)
    {
        assertEquals(msg(keys + ": Slf Time"), expected, getNode(keys).getSelfTime());
    }

    @Override
    public void assertTotalTimeEquals(String[] keys, long expected)
    {
        assertEquals(msg(keys + ": Tot Time"), expected, getNode(keys).getTotalTime());
    }

    @Override
    public void assertSelfCntPctEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Slf # %"), expected, getNode(keys).getSelfCntPct(), .00001);
    }

    @Override
    public void assertTotalCntPctEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Tot # %"), expected, getNode(keys).getTotalCntPct(), .00001);
    }

    @Override
    public void assertSelfTimePctEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Slf Time %"), expected, getNode(keys).getSelfTimePct(), .00001);
    }

    @Override
    public void assertTotalTimePctEquals(String[] keys, double expected)
    {
        assertEquals(msg(keys + ": Tot Time %"), expected, getNode(keys).getTotalTimePct(), .00001);
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

    private Node getNode(String... keys)
    {
        if (keys == null || keys.length == 0)
        {
            return null;
        }

        List<Node> children = tree.getData();
        Optional<Node> result = null;

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
