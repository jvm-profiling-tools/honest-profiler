package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.TreeProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo;

/**
 * Class which lazily calculates aggregated views.
 */
public class AggregationProfile
{
    private static final FlatProfileAggregator flatAggregator = new FlatProfileAggregator();
    private static final TreeProfileAggregator treeAggregator = new TreeProfileAggregator();

    private final LeanProfile source;

    private final NumericInfo global;

    private Map<CombinedGrouping, Flat> cachedFlats;
    private Map<CombinedGrouping, Tree> cachedTrees;

    public AggregationProfile(LeanProfile source)
    {
        this.source = source;
        global = new NumericInfo();
        cachedFlats = new HashMap<>();
        cachedTrees = new HashMap<>();

        // ThreadInfo objects are stored separately in the LeanLogCollector (to avoid the assumption that a ThreadMeta
        // will always be emitted before the first sample for the thread comes in), so we put them into the root
        // LeanThreadNodes here.
        source.getThreads().forEach((id, node) -> node.setThreadInfo(source.getThreadInfo(id)));

        aggregateGlobal();
    }

    public LeanProfile getSource()
    {
        return source;
    }

    public NumericInfo getGlobalData()
    {
        return global;
    }

    public Flat getFlat(CombinedGrouping grouping)
    {
        return cachedFlats.computeIfAbsent(grouping, g -> flatAggregator.aggregate(this, g));
    }

    public Tree getTree(CombinedGrouping grouping)
    {
        return cachedTrees.computeIfAbsent(grouping, g -> treeAggregator.aggregate(this, g));
    }

    private void aggregateGlobal()
    {
        NumericInfo aggregated = source.getThreads().values().stream().collect(
            NumericInfo::new,
            (data, node) -> data.add(node.getData()),
            (data1, data2) -> data1.add(data2));
        global.add(aggregated);
    }
}
