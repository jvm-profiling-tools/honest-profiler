package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.aggregator.FlatProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.aggregator.TreeProfileAggregator;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanThreadNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo;

/**
 * An AggregationProfile is a wrapper for a {@link LeanProfile} which provides methods for creating {@link Aggregation}s
 * on the wrapped {@link LeanProfile}. The class caches these {@link Aggregation}s.
 * <p>
 * It also calculates the "global aggregated data", i.e. the result of aggregating all data from the profile.
 */
public class AggregationProfile
{
    // Instance Properties

    private static final FlatProfileAggregator flatAggregator = new FlatProfileAggregator();
    private static final TreeProfileAggregator treeAggregator = new TreeProfileAggregator();

    private final LeanProfile source;

    private NumericInfo global;

    private Map<CombinedGrouping, Flat> cachedFlats;
    private Map<CombinedGrouping, Tree> cachedTrees;

    // Instance Constructors

    /**
     * Constructor which specifies the wrapped {@link LeanProfile}.
     * <p>
     * @param source the wrapped {@link LeanProfile}
     */
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

        // Calculate the overall aggregation for the LeanProfile
        global = aggregateGlobal();
    }

    // Instance Accessors

    /**
     * Returns the wrapped {@link LeanProfile}.
     * <p>
     * @return the wrapped {@link LeanProfile}
     */
    public LeanProfile getSource()
    {
        return source;
    }

    /**
     * Returns the global aggregated data for the wrapped {@link LeanProfile}.
     * <p>
     * @return the global aggregated data for the wrapped {@link LeanProfile}
     */
    public NumericInfo getGlobalData()
    {
        return global;
    }

    /**
     * Returns the {@link Flat} {@link Aggregation} obtained by aggregating the {@link LeanProfile} using the provided
     * {@link CombinedGrouping}.
     * <p>
     * @param grouping the {@link CombinedGrouping} which determines the aggregation key while aggregating
     * @return the resulting {@link Flat} aggregation
     */
    public Flat getFlat(CombinedGrouping grouping)
    {
        return cachedFlats.computeIfAbsent(grouping, g -> flatAggregator.aggregate(this, g));
    }

    /**
     * Returns the {@link Tree} {@link Aggregation} obtained by aggregating the {@link LeanProfile} using the provided
     * {@link CombinedGrouping}.
     * <p>
     * @param grouping the {@link CombinedGrouping} which determines the aggregation key while aggregating
     * @return the resulting {@link Tree} aggregation
     */
    public Tree getTree(CombinedGrouping grouping)
    {
        return cachedTrees.computeIfAbsent(grouping, g -> treeAggregator.aggregate(this, g));
    }

    // Helper Methods

    /**
     * Calculate the global aggregation of the {@link LeanProfile}. This is obtained by summing the {@link NumericInfo}
     * from all {@link LeanThreadNode}s in the {@link LeanProfile}.
     * <p>
     * The {@link LeanThreadNode}s, which are the root {@link Node}s in the {@link LeanProfile}, are themselves already
     * aggregations of all their descendant Frame {@link LeanNode}s, so we don't need to descend any further.
     * <p>
     * @return the {@link NumericInfo} containing the aggregated data
     */
    private NumericInfo aggregateGlobal()
    {
        return source.getThreads().values().stream().collect(
            // Supplier, which creates a new NumericInfo which will contain the results
            NumericInfo::new,
            // Accumulator, adds the data from a LeanNode to the Accumulator
            (data, node) -> data.add(node.getData()),
            // Combiner, combines two NumericInfos
            (data1, data2) -> data1.add(data2));
    }
}
