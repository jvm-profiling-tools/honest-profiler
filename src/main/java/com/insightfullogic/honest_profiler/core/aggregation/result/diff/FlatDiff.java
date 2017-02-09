package com.insightfullogic.honest_profiler.core.aggregation.result.diff;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;

/**
 * Diff which wraps provides the difference between two flat {@link Aggregation}s (containing {@link Entry}s) as a flat
 * {@link List} of {@link DiffEntry}s, which each wrap and provide the difference between corresponding {@link Entry}s.
 */
public class FlatDiff extends AbstractDiff<Entry, DiffEntry, Flat>
{
    // Instance Properties

    private Map<String, DiffEntry> data;

    // Instance Constructors

    /**
     * Empty constructor.
     */
    public FlatDiff()
    {
        data = new HashMap<>();
    }

    /**
     * Internal Copy constructor.
     * <p>
     * @param entries the {@link List} of {@link DiffEntry}s to be copied into this Diff
     */
    private FlatDiff(List<DiffEntry> entries)
    {
        data = new HashMap<>();
        entries.forEach(entry -> data.put(entry.getKey(), entry));
    }

    // Instance Accessors

    /**
     * Sets the Base and New {@link Flat}s, and calculates the diff contents.
     * <p>
     * @param baseFlat the Base {@link Flat}
     * @param newFlat the New {@link Flat}
     */
    public void set(Flat baseFlat, Flat newFlat)
    {
        super.setAggregations(baseFlat, newFlat);
        data.clear();

        baseFlat.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new DiffEntry(entry, null) : v.setBase(entry));
        });
        newFlat.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new DiffEntry(null, entry) : v.setNew(entry));
        });
    }

    /**
     * Returns the {@link DiffEntry}s from this Diff.
     * <p>
     * @return a {@link Collection} containing the {@link DiffEntry}s from this Diff
     */
    public Collection<DiffEntry> getData()
    {
        return data.values();
    }

    // AbstractDiff Implementation

    @Override
    public FlatDiff filter(FilterSpecification<DiffEntry> filterSpec)
    {
        return new FlatDiff(getData().stream().filter(filterSpec.getFilter()).collect(toList()));
    }
}
