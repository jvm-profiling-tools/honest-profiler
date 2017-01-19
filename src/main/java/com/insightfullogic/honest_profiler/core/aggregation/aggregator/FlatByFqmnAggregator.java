package com.insightfullogic.honest_profiler.core.aggregation.aggregator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

public class FlatByFqmnAggregator implements Aggregator<AggregationProfile, String, Entry<String>>
{
    @Override
    public Flat<String> aggregate(AggregationProfile input, LeanNode reference)
    {
        List<Entry<String>> result = new ArrayList<>();
        Flat<String> aggregation = new Flat<>(result, reference);

        input.getFqmnLinks().values().forEach(link ->
        {
            NumericInfo sum = link.getSiblings().values().stream().flatMap(Collection::stream)
                .map(LeanNode::getData)
                .collect(NumericInfo::new, (x, y) -> x.add(y), (x, y) -> x.add(y));

            result.add(new Entry<>(link.getFqmn(), sum, aggregation));
        });

        return aggregation;
    }
}
