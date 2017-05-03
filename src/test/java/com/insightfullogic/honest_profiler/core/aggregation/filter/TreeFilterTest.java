package com.insightfullogic.honest_profiler.core.aggregation.filter;

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
import static com.insightfullogic.honest_profiler.framework.AggregationUtil.nano;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.SCENARIOS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.framework.generator.TreeGenerator;

public class TreeFilterTest
{
    @Test
    public void testTreeFiltering()
    {
        TreeGenerator gen = new TreeGenerator(BY_ID, BY_FQMN);
        SCENARIOS.get(7).executeAndEnd(gen);

        check(gen, SELF_COUNT, EQUALS_NR, 99, 0, entry -> entry.getSelfCnt() == 99);
        check(gen, SELF_COUNT, EQUALS_NR, 14, 1, entry -> entry.getSelfCnt() == 14);
        check(gen, SELF_COUNT, EQUALS_NR, 3, 3, entry -> entry.getSelfCnt() == 3);

        check(gen, SELF_COUNT, GT, 14, 2, entry -> entry.getSelfCnt() > 14);
        check(gen, SELF_COUNT, GE, 14, 3, entry -> entry.getSelfCnt() >= 14);
        check(gen, SELF_COUNT, LT, 14, 28, entry -> entry.getSelfCnt() < 19);
        check(gen, SELF_COUNT, LE, 14, 28, entry -> entry.getSelfCnt() <= 19);

        check(gen, TOTAL_COUNT, EQUALS_NR, 0, 0, entry -> entry.getTotalCnt() == 0);
        check(gen, TOTAL_COUNT, EQUALS_NR, 6, 1, entry -> entry.getTotalCnt() == 6);

        check(gen, TOTAL_COUNT, GT, 14, 2, entry -> entry.getTotalCnt() > 14);
        check(gen, TOTAL_COUNT, GE, 14, 3, entry -> entry.getTotalCnt() >= 14);
        check(gen, TOTAL_COUNT, LT, 14, 25, entry -> entry.getTotalCnt() < 14);
        check(gen, TOTAL_COUNT, LE, 14, 26, entry -> entry.getTotalCnt() <= 14);

        check(gen, SELF_TIME, EQUALS_NR, nano(99), 0, entry -> entry.getSelfTime() == nano(99));
        check(gen, SELF_TIME, EQUALS_NR, nano(14), 1, entry -> entry.getSelfTime() == nano(14));
        check(gen, SELF_TIME, EQUALS_NR, nano(3), 3, entry -> entry.getSelfTime() == nano(3));

        check(gen, SELF_TIME, GT, nano(14), 2, entry -> entry.getSelfTime() > nano(14));
        check(gen, SELF_TIME, GE, nano(14), 3, entry -> entry.getSelfTime() >= nano(14));
        check(gen, SELF_TIME, LT, nano(14), 28, entry -> entry.getSelfTime() < nano(14));
        check(gen, SELF_TIME, LE, nano(14), 28, entry -> entry.getSelfTime() <= nano(14));

        check(gen, TOTAL_TIME, EQUALS_NR, nano(0), 0, entry -> entry.getTotalTime() == 0);
        check(gen, TOTAL_TIME, EQUALS_NR, nano(6), 1, entry -> entry.getTotalTime() == nano(6));

        check(gen, TOTAL_TIME, GT, nano(14), 2, entry -> entry.getTotalTime() > nano(14));
        check(gen, TOTAL_TIME, GE, nano(14), 3, entry -> entry.getTotalTime() >= nano(14));
        check(gen, TOTAL_TIME, LT, nano(14), 25, entry -> entry.getTotalTime() < nano(14));
        check(gen, TOTAL_TIME, LE, nano(14), 26, entry -> entry.getTotalTime() <= nano(14));

        check(gen, KEY, CONTAINS, "blah", 0, entry -> entry.getKey().contains("blah"));

        check(gen, KEY, CONTAINS, "Test", 0, entry -> entry.getKey().contains("Test"));
        check(gen, KEY, CONTAINS, "Qu", 9, entry -> entry.getKey().contains("Qu"));
        check(
            gen,
            KEY,
            STARTS_WITH,
            "com.test",
            28,
            entry -> entry.getKey().startsWith("com.test"));
        check(gen, KEY, ENDS_WITH, "baz()", 20, entry -> entry.getKey().endsWith("baz()"));
        check(
            gen,
            KEY,
            EQUALS_STR,
            "com.test.Fnord.fnord()",
            19,
            entry -> entry.getKey().equals("com.test.Fnord.fnord()"));
        check(
            gen,
            KEY,
            MATCHES,
            ".*om.+Fno.*",
            19,
            entry -> entry.getKey().matches(".*om.+Fno.*"));
        check(gen, KEY, NOT_CONTAINS, "Baz", 28, entry -> !entry.getKey().contains("Baz"));
        check(gen, KEY, NOT_ENDS_WITH, "qux()", 28, entry -> !entry.getKey().endsWith("qux()"));
        check(gen, KEY, NOT_STARTS_WITH, "com", 10, entry -> !entry.getKey().startsWith("com"));
    }

    private <U> void check(TreeGenerator gen, Target target, Comparison comparison, U value,
        int filteredCount, Predicate<Entry> predicate)
    {
        Tree filtered = gen.filter(treeFilter(target, comparison, value));

        assertLeavesSatisfy(filtered, filteredCount, predicate);
    }

    private <U> void assertLeavesSatisfy(Tree tree, int count, Predicate<Entry> predicate)
    {
        List<Node> nodes = tree.flatten().filter(node -> node.getChildren().size() == 0)
            .collect(toList());

        Assert.assertEquals("Unexpected number of leaf Nodes", count, nodes.size());
        for (Node entry : nodes)
        {
            assertTrue(predicate.apply(entry));
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
