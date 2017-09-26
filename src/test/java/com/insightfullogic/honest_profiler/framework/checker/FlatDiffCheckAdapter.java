package com.insightfullogic.honest_profiler.framework.checker;

import static org.junit.Assert.assertEquals;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.FlatDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;

/**
 * {@link CheckAdapter} for checking a {@link Flat} directly.
 */
public class FlatDiffCheckAdapter implements DiffCheckAdapter<String>
{
    private FlatDiff flat;

    public FlatDiffCheckAdapter(FlatDiff flat)
    {
        this.flat = flat;
    }

    @Override
    public ThreadGrouping getThreadGrouping()
    {
        return flat.getBaseAggregation().getGrouping().getThreadGrouping();
    }

    @Override
    public FrameGrouping getFrameGrouping()
    {
        return flat.getBaseAggregation().getGrouping().getFrameGrouping();
    }

    @Override
    public void assertSizeEquals(int expected)
    {
        assertEquals(msg("Size"), expected, flat.getData().size());
    }

    // Base Checks

    @Override
    public void assertBaseSelfCntEquals(String key, int expected)
    {
        assertEquals(msg(key + ": Slf #"), expected, getEntry(key).getBaseSelfCnt());
    }

    @Override
    public void assertBaseTotalCntEquals(String key, int expected)
    {
        assertEquals(msg(key + ": Tot #"), expected, getEntry(key).getBaseTotalCnt());
    }

    @Override
    public void assertBaseSelfTimeEquals(String key, long expected)
    {
        assertEquals(msg(key + ": Slf Time"), expected, getEntry(key).getBaseSelfTime());
    }

    @Override
    public void assertBaseTotalTimeEquals(String key, long expected)
    {
        assertEquals(msg(key + ": Tot Time"), expected, getEntry(key).getBaseTotalTime());
    }

    @Override
    public void assertBaseSelfCntPctEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Slf # %"), expected, getEntry(key).getBaseSelfCntPct(), .00001);
    }

    @Override
    public void assertBaseTotalCntPctEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Tot # %"), expected, getEntry(key).getBaseTotalCntPct(), .00001);
    }

    @Override
    public void assertBaseSelfTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg(key + ": Slf Time %"),
            expected,
            getEntry(key).getBaseSelfTimePct(),
            .00001);
    }

    @Override
    public void assertBaseTotalTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg(key + ": Tot Time %"),
            expected,
            getEntry(key).getBaseTotalTimePct(),
            .00001);
    }

    // New Checks

    @Override
    public void assertNewSelfCntEquals(String key, int expected)
    {
        assertEquals(msg(key + ": Slf #"), expected, getEntry(key).getNewSelfCnt());
    }

    @Override
    public void assertNewTotalCntEquals(String key, int expected)
    {
        assertEquals(msg(key + ": Tot #"), expected, getEntry(key).getNewTotalCnt());
    }

    @Override
    public void assertNewSelfTimeEquals(String key, long expected)
    {
        assertEquals(msg(key + ": Slf Time"), expected, getEntry(key).getNewSelfTime());
    }

    @Override
    public void assertNewTotalTimeEquals(String key, long expected)
    {
        assertEquals(msg(key + ": Tot Time"), expected, getEntry(key).getNewTotalTime());
    }

    @Override
    public void assertNewSelfCntPctEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Slf # %"), expected, getEntry(key).getNewSelfCntPct(), .00001);
    }

    @Override
    public void assertNewTotalCntPctEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Tot # %"), expected, getEntry(key).getNewTotalCntPct(), .00001);
    }

    @Override
    public void assertNewSelfTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg(key + ": Slf Time %"),
            expected,
            getEntry(key).getNewSelfTimePct(),
            .00001);
    }

    @Override
    public void assertNewTotalTimePctEquals(String key, double expected)
    {
        assertEquals(
            msg(key + ": Tot Time %"),
            expected,
            getEntry(key).getNewTotalTimePct(),
            .00001);
    }

    // Diff Checks

    @Override
    public void assertSelfCntDiffEquals(String key, int expected)
    {
        assertEquals(msg(key + ": Slf #"), expected, getEntry(key).getSelfCntDiff());
    }

    @Override
    public void assertTotalCntDiffEquals(String key, int expected)
    {
        assertEquals(msg(key + ": Tot #"), expected, getEntry(key).getTotalCntDiff());
    }

    @Override
    public void assertSelfTimeDiffEquals(String key, long expected)
    {
        assertEquals(msg(key + ": Slf Time"), expected, getEntry(key).getSelfTimeDiff());
    }

    @Override
    public void assertTotalTimeDiffEquals(String key, long expected)
    {
        assertEquals(msg(key + ": Tot Time"), expected, getEntry(key).getTotalTimeDiff());
    }

    @Override
    public void assertSelfCntPctDiffEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Slf # %"), expected, getEntry(key).getSelfCntPctDiff(), .00001);
    }

    @Override
    public void assertTotalCntPctDiffEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Tot # %"), expected, getEntry(key).getTotalCntPctDiff(), .00001);
    }

    @Override
    public void assertSelfTimePctDiffEquals(String key, double expected)
    {
        assertEquals(
            msg(key + ": Slf Time %"),
            expected,
            getEntry(key).getSelfTimePctDiff(),
            .00001);
    }

    @Override
    public void assertTotalTimePctDiffEquals(String key, double expected)
    {
        assertEquals(
            msg(key + ": Tot Time %"),
            expected,
            getEntry(key).getTotalTimePctDiff(),
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
            + flat.toString();
    }

    private DiffEntry getEntry(String key)
    {
        return flat.getData().stream().filter(entry -> key.equals(entry.getKey())).findFirst()
            .get();
    }
}
