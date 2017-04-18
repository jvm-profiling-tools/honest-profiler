package com.insightfullogic.honest_profiler.core.aggregation;

import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.getScenario;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.insightfullogic.honest_profiler.core.LeanProfileGenerator;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;

public class AggregationProfileTest
{
    @Test
    public void leanProfileConversion()
    {
        LeanProfileGenerator gen = getScenario();

        LeanProfile leanProfile = gen.getProfile();

        // Check initial state of ThreadNode ThreadInfo

        for (LeanThreadNode threadNode : leanProfile.getThreads().values())
        {
            assertNull(threadNode.getThreadInfo());
        }

        AggregationProfile profile = new AggregationProfile(gen.getProfile());

        // Test setting of ThreadInfo

        assertNotNull(leanProfile.getThreads().get(1L).getThreadInfo());
        assertNotNull(leanProfile.getThreads().get(2L).getThreadInfo());
        assertNotNull(leanProfile.getThreads().get(3L).getThreadInfo());
        assertNotNull(leanProfile.getThreads().get(4L).getThreadInfo());
        assertNotNull(leanProfile.getThreads().get(5L).getThreadInfo());
        assertNull(leanProfile.getThreads().get(6L).getThreadInfo());
    }
}
