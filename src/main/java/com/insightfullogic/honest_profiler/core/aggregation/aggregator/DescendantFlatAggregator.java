package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Aggregator which takes a {@link Node}, and aggregates the values of the {@link Node} and its descendants into a
 * {@link Flat}.
 */
public class DescendantFlatAggregator implements SubAggregator<Node, Entry>
{
    /**
     * This method aggregates all descendants of a {@link Node} into a {@link Flat}, using the original
     * {@link CombinedGrouping} from the {@link Tree} the {@link Node} belongs to.
     *
     * @see SubAggregator#aggregate(Object, LeanNode)
     */
    @Override
    public Flat aggregate(Node parent)
    {
        Aggregation<Keyed<String>> aggregation = parent.getAggregation();
        AggregationProfile source = aggregation.getSource();
        CombinedGrouping grouping = aggregation.getGrouping();

        Flat result = new Flat(source, grouping);

        result.getData().addAll(
            parent.flattenDescendants().collect(
                groupingBy(
                    Node::getKey,
                    of(
                        // Supplier, creates an empty Entry
                        () ->
                        {
                            Entry entry = new Entry(aggregation);
                            entry.setReference(source.getGlobalData());
                            return entry;
                        },
                        // Accumulator, adds an Entry to the accumulator Entry
                        (x, y) -> x.combine(y),
                        // Combiner, combines to entries
                        (x, y) -> x.combine(y)
                    )
                )
            ).values());

        return result;
    }
}