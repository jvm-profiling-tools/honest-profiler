package com.insightfullogic.honest_profiler.framework;

import static com.insightfullogic.honest_profiler.framework.LogEventFactory.SCENARIOS;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;

/**
 * Utility class for constructing parameter collections for JUnit parameterized tests.
 */
public class ParameterUtil
{
    // Class Methods

    public static final Collection<Object[]> getScenarios()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(sc -> result.add(new Object[]
        { sc }));
        return result;
    }

    public static final Collection<Object[]> getScenariosAndFrameGroupings()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(sc -> asList(FrameGrouping.values()).forEach(fg -> result.add(new Object[]
        { sc, fg })));
        return result;
    }

    public static final Collection<Object[]> getScenariosAndGroupings()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(
            sc -> asList(ThreadGrouping.values())
                .forEach(tg -> asList(FrameGrouping.values()).forEach(fg -> result.add(new Object[]
            { sc, tg, fg }))));
        return result;
    }

    public static final Collection<Object[]> getDebugScenariosAndGroupings()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(
            sc -> asList(ThreadGrouping.values())
                .forEach(tg -> asList(FrameGrouping.values()).forEach(fg -> result.add(new Object[]
            { sc, tg, fg }))));
        return result.subList(0, 5);
    }

    // Instance Constructors

    private ParameterUtil()
    {
        // Private utility class constructor.
    }
}
