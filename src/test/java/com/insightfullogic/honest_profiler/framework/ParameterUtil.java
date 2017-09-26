package com.insightfullogic.honest_profiler.framework;

import static com.insightfullogic.honest_profiler.framework.LogEventFactory.SCENARIOS;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.framework.scenario.FltScenario;

/**
 * Utility class for constructing parameter collections for JUnit parameterized tests.
 */
public class ParameterUtil
{
    public static List<Object[]> getScenarios()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(sc -> result.add(new Object[]
        { sc }));
        return result;
    }

    public static List<Object[]> getScenariosAndFrameGroupings()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(sc -> asList(FrameGrouping.values()).forEach(fg -> result.add(new Object[]
        { sc, fg })));
        return result;
    }

    public static List<Object[]> getScenariosAndGroupings()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(
            sc -> asList(ThreadGrouping.values())
                .forEach(tg -> asList(FrameGrouping.values()).forEach(fg -> result.add(new Object[]
            { sc, tg, fg }))));
        return result;
    }

    public static List<Object[]> getDiffScenariosAndFrameGroupings()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(
            sc -> asList(FrameGrouping.values()).forEach(fg ->
            {
                result.add(new Object[]
                { sc, SCENARIOS.get(6), fg });
                result.add(new Object[]
                { sc, SCENARIOS.get(7), fg });
            }));
        return result;
    }

    public static List<Object[]> getDiffScenariosAndGroupings()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(
            sc -> asList(ThreadGrouping.values())
                .forEach(tg -> asList(FrameGrouping.values()).forEach(fg ->
                {
                    result.add(new Object[]
                    { sc, SCENARIOS.get(6), tg, fg });
                    result.add(new Object[]
                    { sc, SCENARIOS.get(7), tg, fg });
                })));
        return result;
    }

    public static List<Object[]> getFilterScenarios()
    {
        List<Object[]> result = new ArrayList<>();
        SCENARIOS.forEach(
            sc -> asList(ThreadGrouping.values()).forEach(
                tg -> asList(FrameGrouping.values())
                    .forEach(fg -> FltScenario.getScenarios().forEach(fsc -> result.add(new Object[]
                { sc, tg, fg, fsc })))));
        return result;
    }

    private ParameterUtil()
    {
        // Private utility class constructor.
    }
}
