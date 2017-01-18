package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

public class FlatByFqmnAggregator
    implements Aggregator<AggregationProfile, String, AggregatedEntry<String>>
{
    @Override
    public Aggregation<String, AggregatedEntry<String>> aggregate(AggregationProfile input,
        LeanNode reference)
    {
        List<AggregatedEntry<String>> result = new ArrayList<>();
        Aggregation<String, AggregatedEntry<String>> aggregation = new Aggregation<>(
            result,
            reference);

        input.getFqmnLinks().values().forEach(link ->
        {
            NumericInfo sum = link.getSiblings().values().stream().flatMap(Collection::stream)
                .map(LeanNode::getData)
                .collect(NumericInfo::new, (x, y) -> x.add(y), (x, y) -> x.add(y));

            result.add(new AggregatedEntry<>(link.getFqmn(), sum, aggregation));
        });

        return aggregation;
    }
}
