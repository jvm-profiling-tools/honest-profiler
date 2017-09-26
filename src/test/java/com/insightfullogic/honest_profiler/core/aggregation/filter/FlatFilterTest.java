package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.ENTRY;
import static java.util.Arrays.asList;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.framework.ParameterUtil;
import com.insightfullogic.honest_profiler.framework.checker.FlatCheckAdapter;
import com.insightfullogic.honest_profiler.framework.generator.FlatGenerator;
import com.insightfullogic.honest_profiler.framework.scenario.FltScenario;
import com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter;
import com.insightfullogic.honest_profiler.framework.scenario.SimplifiedLogScenario;

@RunWith(Parameterized.class)
public class FlatFilterTest
{
    @Parameters(name = "{0} : <{1},{2}> {3}")
    public static Collection<Object[]> data()
    {
        return ParameterUtil.getFilterScenarios();
        // return ParameterUtil.getScenariosAndGroupings();
    }

    // Instance Properties

    private SimplifiedLogScenario scenario;
    private ThreadGrouping threadGrouping;
    private FrameGrouping frameGrouping;
    private FltScenario<?> filterScenario;

    // Instance Constructors

    public FlatFilterTest(SimplifiedLogScenario scenario,
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
        FlatGenerator gen = new FlatGenerator(threadGrouping, frameGrouping);
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

    private <U> void check(FlatGenerator gen, Target target, Comparison comparison, U value,
        ScenarioStraightFilter filter)
    {
        try
        {
            Flat filtered = gen.filter(flatFilter(target, comparison, value));
            scenario.checkFlatAggregation(new FlatCheckAdapter(filtered), filter);
        }
        catch (AssertionError ae)
        {
            throw new AssertionError("Failure : Unfiltered =\n" + gen.getFlat(), ae);
        }
    }

    private <U> FilterSpecification<Entry> flatFilter(Target target, Comparison comparison, U value)
    {
        return new FilterSpecification<Entry>(
            ENTRY,
            false,
            asList(new FilterItem<Entry, U>(target, comparison, value)));
    }
}
