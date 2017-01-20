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
 *
 * @param <K> the type of the key
 */
public class FlatDiff<K> extends AbstractDiff<K, Entry<K>, DiffEntry<K>>
{
    // Instance Properties

    private Map<K, DiffEntry<K>> data;

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
     *
     * @param entries the {@link List} of {@link DiffEntry}s to be copied into this Diff
     */
    private FlatDiff(List<DiffEntry<K>> entries)
    {
        data = new HashMap<>();
        entries.forEach(entry -> data.put(entry.getKey(), entry));
    }

    // Instance Accessors

    /**
     * Sets a {@link Flat} as Base or New. Provided as convenience for {@link #setBase(Flat)} and {@link #setNew(Flat)},
     * to make it possible for the calling code to avoid the if-construction.
     *
     * @param aggregation the {@link Flat} to be set as Base or New in this Diff.
     * @param isBase a boolean indicating whether the {@link Flat} has to be set as Base.
     */
    public void set(Flat<K> aggregation, boolean isBase)
    {
        if (isBase)
        {
            setBase(aggregation);
        }
        else
        {
            setNew(aggregation);
        }
    }

    /**
     * Sets a {@link Flat} as Base in this Diff.
     *
     * @param aggregation the {@link Flat} to be set as Base
     */
    public void setBase(Flat<K> aggregation)
    {
        super.setBaseAggregation(aggregation);
        aggregation.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new DiffEntry<>(entry, null) : v.setBase(entry));
        });
    }

    /**
     * Sets a {@link Flat} as New in this Diff.
     *
     * @param aggregation the {@link Flat} to be set as New
     */
    public void setNew(Flat<K> aggregation)
    {
        super.setNewAggregation(aggregation);
        aggregation.getData().forEach(entry ->
        {
            data.compute(
                entry.getKey(),
                (k, v) -> v == null ? new DiffEntry<>(null, entry) : v.setNew(entry));
        });
    }

    /**
     * Returns the {@link DiffEntry}s from this Diff.
     *
     * @return a {@link Collection} containing the {@link DiffEntry}s from this Diff
     */
    public Collection<DiffEntry<K>> getData()
    {
        return data.values();
    }

    // AbstractDiff Implementation

    @Override
    public FlatDiff<K> filter(FilterSpecification<DiffEntry<K>> filterSpec)
    {
        return new FlatDiff<>(getData().stream().filter(filterSpec.getFilter()).collect(toList()));
    }
}
