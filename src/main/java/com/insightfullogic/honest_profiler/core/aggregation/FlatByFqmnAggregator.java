package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

public class FlatByFqmnAggregator implements Aggregator<AggregationProfile, List<AggregatedEntry>>
{
    @Override
    public List<AggregatedEntry> aggregate(AggregationProfile input, NumericInfo reference)
    {
        List<AggregatedEntry> result = new ArrayList<>();

        input.getFqmnLinks().values().forEach(link ->
        {
            NumericInfo sum = link.getSiblings().values().stream().flatMap(Collection::stream)
                .map(LinkedLeanNode::getData)
                .collect(NumericInfo::new, (x, y) -> x.add(y), (x, y) -> x.add(y));

            result.add(new AggregatedEntry(link.getFqmn(), sum, reference));
        });

        return result;
    }
}
