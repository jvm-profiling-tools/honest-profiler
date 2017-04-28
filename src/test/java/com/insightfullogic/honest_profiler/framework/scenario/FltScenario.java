package com.insightfullogic.honest_profiler.framework.scenario;

import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.CONTAINS;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.ENDS_WITH;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.EQUALS_NR;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.EQUALS_STR;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.GE;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.GT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.LE;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.LT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.MATCHES;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.NOT_CONTAINS;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.NOT_ENDS_WITH;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.NOT_STARTS_WITH;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison.STARTS_WITH;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.KEY;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_COUNT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.SELF_TIME;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_COUNT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.Target.TOTAL_TIME;
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter.keyFlt;
import static com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter.selfCntFlt;
import static com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter.selfTimeFlt;
import static com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter.totalCntFlt;
import static com.insightfullogic.honest_profiler.framework.scenario.ScenarioStraightFilter.totalTimeFlt;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison;
import com.insightfullogic.honest_profiler.core.aggregation.filter.Target;

public class FltScenario<T>
{
    private Target target;
    private Comparison comparison;
    private T value;

    private ScenarioStraightFilter scenarioFilter;

    private static final List<Integer> testNrs = asList(0, 5, 10, 19, 48, 72, 99, 2000);
    private static final List<String> testStrs = asList(
        "blah",
        "Test",
        "Qu",
        "All Thre",
        "com.test",
        "baz()",
        ")",
        "com.test.Fnord.fnord()");

    private static final List<FltScenario<?>> LIST = new ArrayList<>();

    private static final <V> void add(Target t, Comparison c, V v, ScenarioStraightFilter f)
    {
        LIST.add(new FltScenario<>(t, c, v, f));
    }

    static
    {
        testNrs.forEach(i ->
        {
            add(SELF_COUNT, EQUALS_NR, i, selfCntFlt(value -> value.intValue() == i));
            add(SELF_COUNT, GT, i, selfCntFlt(value -> value.intValue() > i));
            add(SELF_COUNT, LT, i, selfCntFlt(value -> value.intValue() < i));
            add(SELF_COUNT, GE, i, selfCntFlt(value -> value.intValue() >= i));
            add(SELF_COUNT, LE, i, selfCntFlt(value -> value.intValue() <= i));

            add(TOTAL_COUNT, EQUALS_NR, i, totalCntFlt(value -> value.longValue() == i));
            add(TOTAL_COUNT, GT, i, totalCntFlt(value -> value > i));
            add(TOTAL_COUNT, LT, i, totalCntFlt(value -> value < i));
            add(TOTAL_COUNT, GE, i, totalCntFlt(value -> value >= i));
            add(TOTAL_COUNT, LE, i, totalCntFlt(value -> value <= i));

            add(SELF_TIME, EQUALS_NR, n(i), selfTimeFlt(value -> value == n(i)));
            add(SELF_TIME, GT, n(i), selfTimeFlt(value -> value > n(i)));
            add(SELF_TIME, LT, n(i), selfTimeFlt(value -> value < n(i)));
            add(SELF_TIME, GE, n(i), selfTimeFlt(value -> value >= n(i)));
            add(SELF_TIME, LE, n(i), selfTimeFlt(value -> value <= n(i)));

            add(TOTAL_TIME, EQUALS_NR, n(i), totalTimeFlt(value -> value == n(i)));
            add(TOTAL_TIME, GT, n(i), totalTimeFlt(value -> value > n(i)));
            add(TOTAL_TIME, LT, n(i), totalTimeFlt(value -> value < n(i)));
            add(TOTAL_TIME, GE, n(i), totalTimeFlt(value -> value >= n(i)));
            add(TOTAL_TIME, LE, n(i), totalTimeFlt(value -> value <= n(i)));
        });

        testStrs.forEach(s ->
        {
            add(KEY, CONTAINS, s, keyFlt(value -> value.contains(s)));
            add(KEY, STARTS_WITH, s, keyFlt(value -> value.startsWith(s)));
            add(KEY, ENDS_WITH, s, keyFlt(value -> value.endsWith(s)));
            add(KEY, EQUALS_STR, s, keyFlt(value -> value.equals(s)));
            add(KEY, NOT_CONTAINS, s, keyFlt(value -> !value.contains(s)));
            add(KEY, NOT_STARTS_WITH, s, keyFlt(value -> !value.startsWith(s)));
            add(KEY, NOT_ENDS_WITH, s, keyFlt(value -> !value.endsWith(s)));
        });

        add(KEY, MATCHES, ".*om.+Fno.*", keyFlt(value -> value.matches(".*om.+Fno.*")));
    }

    private static final long n(int i)
    {
        return nano(i);
    }

    public static final List<FltScenario<?>> getScenarios()
    {
        return LIST;
    }

    public FltScenario(Target target,
                       Comparison comparison,
                       T value,
                       ScenarioStraightFilter scenarioFilter)
    {
        super();
        this.target = target;
        this.comparison = comparison;
        this.value = value;
        this.scenarioFilter = scenarioFilter;
    }

    public Target getTarget()
    {
        return target;
    }

    public Comparison getComparison()
    {
        return comparison;
    }

    public T getValue()
    {
        return value;
    }

    public ScenarioStraightFilter getScenarioFilter()
    {
        return scenarioFilter;
    }

    @Override
    public String toString()
    {
        return "<" + target + " " + comparison + " " + value + ">";
    }
}
