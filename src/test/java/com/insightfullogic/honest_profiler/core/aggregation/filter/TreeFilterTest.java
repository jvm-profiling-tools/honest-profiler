package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.ENTRY;
import static com.insightfullogic.honest_profiler.framework.ParameterUtil.getFilterScenarios;
import static java.util.Arrays.asList;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.framework.checker.TreeCheckAdapter;
import com.insightfullogic.honest_profiler.framework.generator.TreeGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.FltScenario;
import com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;

@RunWith(Parameterized.class)
public class TreeFilterTest
{
    @Parameters(name = "{0} : <{1},{2}> {3}")
    public static Collection<Object[]> data()
    {
        return getFilterScenarios();
    }

    // Instance Properties

    private SimplifiedLogScenario scenario;
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;
    private FltScenario<?> filterScenario;

    // Instance Constructors

    public TreeFilterTest(SimplifiedLogScenario scenario,
                          ThreadGrouping threadGrouping,
                          FrameGrouping frameGrouping,
                          FltScenario<?> filterScenario)
    {
        this.scenario = scenario;
        this.threadGrouping = threadGrouping;
        this.frameGrouping = frameGrouping;
        this.filterScenario = filterScenario;
    }

    // Actual Test Method

    @Test
    public void testScenario()
    {
        TreeGenerator gen = new TreeGenerator(threadGrouping, frameGrouping);
        scenario.executeAndEnd(gen);

        try
        {
            check(
                gen,
                filterScenario.getTarget(),
                filterScenario.getComparison(),
                filterScenario.getValue(),
                filterScenario.getScenarioFilter());
        }
        catch (AssertionError ae)
        {
            throw new AssertionError(filterScenario.toString() + " Failed ->", ae);
        }
    }

    private <U> void check(TreeGenerator gen, Target target, Comparison comparison, U value,
        ScenarioStraightFilter filter)
    {
        try
        {
            Tree filtered = gen.filter(treeFilter(target, comparison, value));
            scenario.checkTreeAggregation(new TreeCheckAdapter(filtered), filter);
        }
        catch (AssertionError ae)
        {
            throw new AssertionError("Failure : Unfiltered =\n" + gen.getTree(), ae);
        }
    }

    private <U> FilterSpecification<Node> treeFilter(Target target, Comparison comparison, U value)
    {
        return new FilterSpecification<Node>(
            ENTRY,
            false,
            asList(new FilterItem<>(target, comparison, value)));
    }
}
