package com.insightfullogic.honest_profiler.core.aggregation.result.straight;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.Keyed;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo;

/**
 * Lowest-level aggregation data item. It associates an aggregation key with data residing in a {@link NumericInfo}.
 * Additionally it keeps track of all {@link LeanNode}s which were aggregated into the Entry.
 */
public class Entry implements Keyed<String>
{
    // Instance Properties

    private Aggregation<? extends Keyed<String>> aggregation;
    private String key;
    private NumericInfo data;
    private List<LeanNode> aggregatedNodes;
    private NumericInfo reference;

    // Instance Constructors

    /**
     * Constructor which sets the link to the containing {@link Aggregation}.
     * <p>
     * @param <T> the type of the data items contained in the {@link Aggregation}
     * @param aggregation the containing {@link Aggregation}
     */
    public <T extends Keyed<String>> Entry(Aggregation<T> aggregation)
    {
        this.data = new NumericInfo();
        this.aggregation = aggregation;
        this.aggregatedNodes = new ArrayList<>();
    }

    /**
     * Constructor which sets the aggregation key and the link to the containing {@link Aggregation}.
     * <p>
     * @param <T> the type of the data items contained in the {@link Aggregation}
     * @param key the aggregation key
     * @param aggregation the containing {@link Aggregation}
     */
    public <T extends Keyed<String>> Entry(String key, Aggregation<T> aggregation)
    {
        this(aggregation);
        this.key = key;
    }

    // Instance Accessors

    /**
     * Returns the containing {@link Aggregation}.
     * <p>
     * @param <T> the type of the data items contained in the {@link Aggregation}
     * @return the containing {@link Aggregation}
     */
    @SuppressWarnings("unchecked")
    public <T extends Keyed<String>> Aggregation<T> getAggregation()
    {
        return (Aggregation<T>)aggregation;
    }

    /**
     * Sets the reference data used for calculating percentages.
     * <p>
     * @param reference the reference data used for calculating percentages
     */
    public void setReference(NumericInfo reference)
    {
        this.reference = reference;
    }

    @Override
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the aggregation key.
     * <p>
     * @param key the aggregation key
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Returns the contained numeric data.
     * <p>
     * @return the contained numeric data
     */
    public NumericInfo getData()
    {
        return data;
    }

    /**
     * Returns a list of all the {@link LeanNode}s whose values were aggregated into this Entry.
     * <p>
     * @return the list of {@link LeanNode}s aggregated into this Entry
     */
    public List<LeanNode> getAggregatedNodes()
    {
        return this.aggregatedNodes;
    }

    /**
     * Return the aggregated self time in nanoseconds, i.e. the sum of the times spent in the methods aggregated by this
     * Entry.
     * <p>
     * @return the aggregated self time in nanoseconds
     */
    public long getSelfTime()
    {
        return data.getSelfTime().longValue();
    }

    /**
     * Return the aggregated self time in nanoseconds, i.e. the sum of the times spent in the methods aggregated by this
     * Entry, or methods called by the aggregated methods.
     * <p>
     * @return the aggregated total time in nanoseconds
     */
    public long getTotalTime()
    {
        return data.getTotalTime().longValue();
    }

    /**
     * Return the aggregated number of samples the aggregated {@link LeanNode}s were seen in as leaf.
     * <p>
     * @return the aggregated number of samples the aggregated {@link LeanNode}s were seen in as leaf
     */
    public int getSelfCnt()
    {
        return data.getSelfCnt();
    }

    /**
     * Return the aggregated number of samples the aggregated {@link LeanNode}s were seen in as leaf or intermediate
     * frame.
     * <p>
     * @return the aggregated number of samples the aggregated {@link LeanNode}s were seen in as leaf or intermediate
     *         frame
     */
    public int getTotalCnt()
    {
        return data.getTotalCnt();
    }

    /**
     * Returns the self time divided by the reference total time.
     * <p>
     * @return the self time divided by the reference total time
     */
    public double getSelfTimePct()
    {
        return aggregation == null ? 0
            : data.getSelfTime().doubleValue() / reference.getTotalTime().longValue();
    }

    /**
     * Returns the total time divided by the reference total time.
     * <p>
     * @return the total time divided by the reference total time
     */
    public double getTotalTimePct()
    {
        return aggregation == null ? 0
            : data.getTotalTime().doubleValue() / reference.getTotalTime().longValue();
    }

    /**
     * Returns the self sample count divided by the reference total sample count.
     * <p>
     * @return the self sample count divided by the reference total sample count
     */
    public double getSelfCntPct()
    {
        return aggregation == null ? 0 : data.getSelfCnt() / (double)reference.getTotalCnt();
    }

    /**
     * Returns the total sample count divided by the reference total sample count.
     * <p>
     * @return the total sample count divided by the reference total sample count
     */
    public double getTotalCntPct()
    {
        return aggregation == null ? 0 : data.getTotalCnt() / (double)reference.getTotalCnt();
    }

    /**
     * Returns the reference total sample count.
     * <p>
     * @return the reference total sample count
     */
    public int getRefCnt()
    {
        return aggregation == null ? 0 : reference.getTotalCnt();
    }

    // Aggregation Methods

    /**
     * Aggregates a {@link LeanNode} into this Entry.
     * <p>
     * @param node the {@link LeanNode} to be aggregated
     */
    public void add(LeanNode node)
    {
        aggregatedNodes.add(node);
        data.add(node.getData());
    }

    /**
     * Combines (i.e. aggregates) another Entry into this one.
     * <p>
     * @param other the other Entry to be combined into this one
     * @return this Entry
     */
    public Entry combine(Entry other)
    {
        key = other.key;
        aggregatedNodes.addAll(other.aggregatedNodes);
        data.add(other.data);
        reference = other.reference;
        return this;
    }

    // Copy Methods

    /**
     * Copies the contents of this Entry into another one.
     * <p>
     * @param other the other Entry into which the contents of this Entry will be copied
     */
    protected void copyInto(Entry other)
    {
        other.aggregation = aggregation;
        other.key = key;
        other.data = data.copy();
        other.aggregatedNodes = new ArrayList<>(aggregatedNodes);
        other.reference = reference;
    }

    // Object Implementation

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("entry[");
        result.append(key);
        result.append(":");
        result.append(data);
        result.append("]");
        return result.toString();
    }
}
