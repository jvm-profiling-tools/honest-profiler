package com.insightfullogic.honest_profiler.core.aggregation;

import static com.insightfullogic.honest_profiler.framework.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.SCENARIOS;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo;
import com.insightfullogic.honest_profiler.framework.generator.LeanProfileGenerator;

public class AggregationProfileTest
{
    @Test
    public void checkThreadInfoLogic()
    {
        LeanProfileGenerator gen = new LeanProfileGenerator();
        SCENARIOS.get(7).executeAndEnd(gen);
        LeanProfile leanProfile = gen.getProfile();

        // Check initial state of ThreadNode ThreadInfo

        for (LeanThreadNode threadNode : leanProfile.getThreads().values())
        {
            assertNull(threadNode.getThreadInfo());
        }

        AggregationProfile profile = new AggregationProfile(leanProfile);
        Map<Long, LeanThreadNode> threads = profile.getSource().getThreads();

        // Check ThreadInfo has been updated

        assertNotNull("Missing ThreadInfo for thread 1", threads.get(1L).getThreadInfo());
        assertNotNull("Missing ThreadInfo for thread 2", threads.get(2L).getThreadInfo());
        assertNotNull("Missing ThreadInfo for thread 3", threads.get(3L).getThreadInfo());
        assertNotNull("Missing ThreadInfo for thread 4", threads.get(4L).getThreadInfo());
        assertNotNull("Missing ThreadInfo for thread 5", threads.get(5L).getThreadInfo());
        assertNull("There should be no ThreadInfo for thread 6", threads.get(6L).getThreadInfo());
    }

    @Test
    public void checkGlobalAggregation()
    {
        LeanProfileGenerator gen = new LeanProfileGenerator();
        SCENARIOS.get(7).executeAndEnd(gen);
        AggregationProfile profile = new AggregationProfile(gen.getProfile());
        NumericInfo global = profile.getGlobalData();

        assertEquals("Global Self Count should be 0", 0, global.getSelfCnt());
        assertEquals("Global Total Count wrong", 111, global.getTotalCnt());
        assertEquals("Global Self Time should be 0", ZERO, global.getSelfTime());
        assertEquals("Global Total Time wrong", valueOf(nano(111)), global.getTotalTime());
    }
}
