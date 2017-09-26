package com.insightfullogic.honest_profiler.framework.checker;

import static org.junit.Assert.assertEquals;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;

/**
 * {@link CheckAdapter} for checking a {@link Flat} directly.
 */
public class FlatCheckAdapter implements CheckAdapter<String>
{
    private Flat flat;

    public FlatCheckAdapter(Flat flat)
    {
        this.flat = flat;
    }

    @Override
    public ThreadGrouping getThreadGrouping()
    {
        return flat.getGrouping().getThreadGrouping();
    }

    @Override
    public FrameGrouping getFrameGrouping()
    {
        return flat.getGrouping().getFrameGrouping();
    }

    @Override
    public void assertSizeEquals(int expected)
    {
        assertEquals(msg("Size"), expected, flat.getData().size());
    }

    @Override
    public void assertSelfCntEquals(String key, int expected)
    {
        assertEquals(msg(key + ": Slf #"), expected, getEntry(key).getSelfCnt());
    }

    @Override
    public void assertTotalCntEquals(String key, int expected)
    {
        assertEquals(msg(key + ": Tot #"), expected, getEntry(key).getTotalCnt());
    }

    @Override
    public void assertSelfTimeEquals(String key, long expected)
    {
        assertEquals(msg(key + ": Slf Time"), expected, getEntry(key).getSelfTime());
    }

    @Override
    public void assertTotalTimeEquals(String key, long expected)
    {
        assertEquals(msg(key + ": Tot Time"), expected, getEntry(key).getTotalTime());
    }

    @Override
    public void assertSelfCntPctEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Slf # %"), expected, getEntry(key).getSelfCntPct(), .00001);
    }

    @Override
    public void assertTotalCntPctEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Tot # %"), expected, getEntry(key).getTotalCntPct(), .00001);
    }

    @Override
    public void assertSelfTimePctEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Slf Time %"), expected, getEntry(key).getSelfTimePct(), .00001);
    }

    @Override
    public void assertTotalTimePctEquals(String key, double expected)
    {
        assertEquals(msg(key + ": Tot Time %"), expected, getEntry(key).getTotalTimePct(), .00001);
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

    private Entry getEntry(String key)
    {
        return flat.getData().stream().filter(entry -> key.equals(entry.getKey())).findFirst()
            .get();
    }
}
