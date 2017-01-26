package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.FqmnLink;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;

/**
 * Aggregator which takes an {@link AggregationProfile}, and uses the data to aggregate the values into a list of
 * {@link Entry}s aggregated by FQMN.
 */
public class FlatByFqmnAggregator implements Aggregator<AggregationProfile, String, Entry<String>>
{
    /**
     * The method uses the {@link FqmnLink} structure calculated in the {@link AggregationProfile}, which groups
     * together all {@link LeanNode}s with the same FQMN. For each {@link FqmnLink} an {@link Entry} is emitted.
     *
     * @see Aggregator#aggregate(Object, LeanNode)
     */
    @Override
    public Flat<String> aggregate(AggregationProfile input, LeanNode reference)
    {
        List<Entry<String>> result = new ArrayList<>();
        Flat<String> aggregation = new Flat<>(result, reference);

        input.getFqmnLinks().values().forEach(link ->
        {
            Entry<String> entry = link.getSiblings().values().stream().flatMap(Collection::stream)
                .collect(
                    () -> new Entry<String>(link.getFqmn(), aggregation),
                    (x, y) -> x.add(y),
                    (x, y) -> x.combine(y));

            result.add(entry);
        });

        return aggregation;
    }
}
