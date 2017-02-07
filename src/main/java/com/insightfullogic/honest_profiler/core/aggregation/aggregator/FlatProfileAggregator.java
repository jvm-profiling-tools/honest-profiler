package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;

/**
 * Aggregator which takes an {@link AggregationProfile}, and uses the data to aggregate the values into a {@link Flat}
 * aggregation.
 *
 * NOTE : The {@link LeanThreadNode}s are also aggregated, so the resulting {@link Flat} will contain {@link Entry}s
 * aggregating the thread-level data as well !
 */
public class FlatProfileAggregator implements ProfileAggregator<Entry>
{
    /**
     * Aggregates an {@link AggregationProfile} into a {@link Flat}. The {@link CombinedGrouping} specifies which
     * {@link LeanNode}s are aggregated together.
     *
     * @see ProfileAggregator#aggregate(AggregationProfile, CombinedGrouping)
     */
    @Override
    public Flat aggregate(AggregationProfile input, CombinedGrouping grouping)
    {
        // Prepare result.
        Flat result = new Flat(input, grouping);

        LeanProfile source = input.getSource();

        // Flatten all LeanNodes into a Stream, then collect it into a Map where the key is calculated by the groupings,
        // and the value is the aggregation of the LeanNodes corresponding to the key.
        Map<String, Entry> entryMap = source.getThreads().values().stream()
            .flatMap(LeanNode::flatten).filter(node -> !node.isThreadNode()).collect(
                groupingBy(
                    // Group LeanNodes by calculated key
                    node -> grouping.apply(input, node),
                    // Downstream collector, collects LeanNodes in a single group
                    of(
                        // Supplier, creates an empty Entry
                        () -> new Entry(result),
                        // Accumulator, aggregates a LeanNode into the Entry accumulator
                        (entry, leanNode) -> entry.add(leanNode),
                        // Combiner, combines two Entries with the same key
                        (entry1, entry2) -> entry1.combine(entry2))));

        // Add the aggregated Entries to the result list, after setting their key and reference. The key is the
        // Map.Entry key, the Entry is the Map.Entry value.
        result.getData().addAll(entryMap.entrySet().stream().map(mapEntry ->
        {
            Entry entry = mapEntry.getValue();
            entry.setKey(mapEntry.getKey());
            // By default, the reference for Entries is the global, over-thread aggregation.
            entry.setReference(input.getGlobalData());
            return entry;
        }).collect(toList()));

        return result;
    }
}
