package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Aggregator which takes a {@link Node}, and aggregates the values of the {@link Node} and its descendants into a list
 * of {@link Entry}s.
 */
public class NodeDescendantAggregator implements Aggregator<Node<String>, String, Entry<String>>
{
    /**
     * This method aggregates a {@link Node} and all its all descendants.
     *
     * @see Aggregator#aggregate(Object, LeanNode)
     */
    @Override
    public Flat<String> aggregate(Node<String> parent, LeanNode reference)
    {
        List<Entry<String>> result = new ArrayList<>();
        Flat<String> aggregation = new Flat<>(result, reference);

        result.addAll(
            parent.flatten().collect(
                groupingBy(
                    Node::getKey,
                    of(
                        // Supplier
                        () -> new Entry<String>(aggregation),
                        // Accumulator
                        (x, y) -> x.combine(y),
                        // Combiner
                        (x, y) -> x.combine(y)
                    )
                )
            ).values());

        return aggregation;
    }
}
