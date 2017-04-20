package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static com.insightfullogic.honest_profiler.core.FlatGenerator.assertAggregationSizeEquals;
import static com.insightfullogic.honest_profiler.core.aggregation.AggregationUtil.nano;
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
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping.BY_FQMN;
import static com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping.BY_ID;
import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.ENTRY;
import static com.insightfullogic.honest_profiler.core.collector.lean.LogEventFactory.applyScenario09;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.insightfullogic.honest_profiler.core.FlatGenerator;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;

public class FlatFilterTest
{
    @Test
    public void testFlatFiltering()
    {
        FlatGenerator gen = new FlatGenerator(BY_ID, BY_FQMN);
        applyScenario09(gen);

        check(gen, SELF_COUNT, EQUALS_NR, 99, 0, entry -> entry.getSelfCnt() == 99);
        check(gen, SELF_COUNT, EQUALS_NR, 0, 1, entry -> entry.getSelfCnt() == 0);
        check(gen, SELF_COUNT, EQUALS_NR, 48, 1, entry -> entry.getSelfCnt() == 48);
        check(gen, SELF_COUNT, EQUALS_NR, 19, 2, entry -> entry.getSelfCnt() == 19);

        check(gen, SELF_COUNT, GT, 19, 1, entry -> entry.getSelfCnt() > 19);
        check(gen, SELF_COUNT, GE, 19, 3, entry -> entry.getSelfCnt() >= 19);
        check(gen, SELF_COUNT, LT, 19, 3, entry -> entry.getSelfCnt() < 19);
        check(gen, SELF_COUNT, LE, 19, 5, entry -> entry.getSelfCnt() <= 19);

        check(gen, TOTAL_COUNT, EQUALS_NR, 0, 0, entry -> entry.getTotalCnt() == 0);
        check(gen, TOTAL_COUNT, EQUALS_NR, 72, 1, entry -> entry.getTotalCnt() == 72);

        check(gen, TOTAL_COUNT, GT, 72, 2, entry -> entry.getTotalCnt() > 72);
        check(gen, TOTAL_COUNT, GE, 72, 3, entry -> entry.getTotalCnt() >= 72);
        check(gen, TOTAL_COUNT, LT, 72, 3, entry -> entry.getTotalCnt() < 72);
        check(gen, TOTAL_COUNT, LE, 72, 4, entry -> entry.getTotalCnt() <= 72);

        check(gen, SELF_TIME, EQUALS_NR, nano(99), 0, entry -> entry.getSelfTime() == nano(99));
        check(gen, SELF_TIME, EQUALS_NR, nano(0), 1, entry -> entry.getSelfTime() == nano(0));
        check(gen, SELF_TIME, EQUALS_NR, nano(19), 2, entry -> entry.getSelfTime() == nano(19));
        check(gen, SELF_TIME, EQUALS_NR, nano(48), 1, entry -> entry.getSelfTime() == nano(48));

        check(gen, SELF_TIME, GT, nano(19), 1, entry -> entry.getSelfTime() > nano(19));
        check(gen, SELF_TIME, GE, nano(19), 3, entry -> entry.getSelfTime() >= nano(19));
        check(gen, SELF_TIME, LT, nano(19), 3, entry -> entry.getSelfTime() < nano(19));
        check(gen, SELF_TIME, LE, nano(19), 5, entry -> entry.getSelfTime() <= nano(19));

        check(gen, TOTAL_TIME, EQUALS_NR, nano(0), 0, entry -> entry.getTotalTime() == 0);
        check(gen, TOTAL_TIME, EQUALS_NR, nano(72), 1, entry -> entry.getTotalTime() == nano(72));

        check(gen, TOTAL_TIME, GT, nano(72), 2, entry -> entry.getTotalTime() > nano(72));
        check(gen, TOTAL_TIME, GE, nano(72), 3, entry -> entry.getTotalTime() >= nano(72));
        check(gen, TOTAL_TIME, LT, nano(72), 3, entry -> entry.getTotalTime() < nano(72));
        check(gen, TOTAL_TIME, LE, nano(72), 4, entry -> entry.getTotalTime() <= nano(72));

        check(gen, KEY, CONTAINS, "blah", 0, entry -> entry.getKey().contains("blah"));

        check(gen, KEY, CONTAINS, "Test", 0, entry -> entry.getKey().contains("Test"));
        check(gen, KEY, CONTAINS, "Qu", 2, entry -> entry.getKey().contains("Qu"));
        check(gen, KEY, STARTS_WITH, "com.test", 6, entry -> entry.getKey().startsWith("com.test"));
        check(gen, KEY, ENDS_WITH, "baz()", 1, entry -> entry.getKey().endsWith("baz()"));
        check(
            gen,
            KEY,
            EQUALS_STR,
            "com.test.Fnord.fnord()",
            1,
            entry -> entry.getKey().equals("com.test.Fnord.fnord()"));
        check(
            gen,
            KEY,
            MATCHES,
            ".*om.+Fno.*",
            1,
            entry -> entry.getKey().matches(".*om.+Fno.*"));
        check(gen, KEY, NOT_CONTAINS, "Baz", 5, entry -> !entry.getKey().contains("Baz"));
        check(gen, KEY, NOT_ENDS_WITH, "qux()", 5, entry -> !entry.getKey().endsWith("qux()"));
        check(gen, KEY, NOT_STARTS_WITH, "com", 0, entry -> !entry.getKey().startsWith("com"));
    }

    private <U> void check(FlatGenerator gen, Target target, Comparison comparison, U value,
        int filteredSize, Predicate<Entry> predicate)
    {
        Flat filtered = gen.filter(flatFilter(target, comparison, value));

        assertAggregationSizeEquals(filtered, filteredSize);
        assertAllEntriesSatisfy(filtered, predicate);
    }

    private <U> void assertAllEntriesSatisfy(Flat flat, Predicate<Entry> predicate)
    {
        for (Entry entry : flat.getData())
        {
            assertTrue(predicate.apply(entry));
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
