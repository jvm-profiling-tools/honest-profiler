package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.FlatDiff;
import com.insightfullogic.honest_profiler.framework.ParameterUtil;
import com.insightfullogic.honest_profiler.framework.checker.FlatDiffCheckAdapter;
import com.insightfullogic.honest_profiler.framework.generator.FlatGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;

@RunWith(Parameterized.class)
public class FlatDiffAggregatorTest
{
    @Parameters(name = "{0} <-> {1} : <{1},{2}>")
    public static Collection<Object[]> data()
    {
        return ParameterUtil.getDiffScenariosAndGroupings();
    }

    // Instance Properties

    private SimplifiedLogScenario baseScenario;
    private SimplifiedLogScenario newScenario;
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;

    // Instance Constructors

    public FlatDiffAggregatorTest(SimplifiedLogScenario baseScenario,
                                  SimplifiedLogScenario newScenario,
                                  ThreadGrouping threadGrouping,
                                  FrameGrouping frameGrouping)
    {
        this.baseScenario = baseScenario;
        this.newScenario = newScenario;
        this.threadGrouping = threadGrouping;
        this.frameGrouping = frameGrouping;
    }

    // Actual Test Method

    @Test
    public void testScenario()
    {
        FlatGenerator baseGen;
        FlatGenerator newGen;

        baseGen = new FlatGenerator(threadGrouping, frameGrouping);
        baseScenario.executeAndEnd(baseGen);

        newGen = new FlatGenerator(threadGrouping, frameGrouping);
        newScenario.executeAndEnd(newGen);

        FlatDiff diff = new FlatDiff();
        diff.set(baseGen.getFlat(), newGen.getFlat());

        baseScenario.checkFlatDiffAggregation(newScenario, new FlatDiffCheckAdapter(diff));
    }
}
