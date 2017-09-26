package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;
import com.insightfullogic.honest_profiler.framework.ParameterUtil;
import com.insightfullogic.honest_profiler.framework.checker.TreeDiffCheckAdapter;
import com.insightfullogic.honest_profiler.framework.generator.TreeGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;

@RunWith(Parameterized.class)
public class TreeDiffAggregatorTest
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

    public TreeDiffAggregatorTest(SimplifiedLogScenario baseScenario,
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
        TreeGenerator baseGen;
        TreeGenerator newGen;

        baseGen = new TreeGenerator(threadGrouping, frameGrouping);
        baseScenario.executeAndEnd(baseGen);

        newGen = new TreeGenerator(threadGrouping, frameGrouping);
        newScenario.executeAndEnd(newGen);

        TreeDiff diff = new TreeDiff();
        diff.set(baseGen.getTree(), newGen.getTree());

        baseScenario.checkTreeDiffAggregation(newScenario, new TreeDiffCheckAdapter(diff));
    }
}
