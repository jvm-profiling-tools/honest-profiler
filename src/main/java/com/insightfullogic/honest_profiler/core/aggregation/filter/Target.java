package com.insightfullogic.honest_profiler.core.aggregation.filter;

import static com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType.INTEGER;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType.LONG;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType.PERCENT;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType.SHARE;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType.STRING;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;

/**
 * A Target describes a value inside an aggregation item. It specifies a displayable name and the type of the value. A
 * Target also provides an extractor, which is a {@link Function} for extracting the Target value from an aggregation
 * item.
 */
public enum Target
{
    KEY("Key", STRING),
    SELF_TIME("Self Time", LONG),
    TOTAL_TIME("Total Time", LONG),
    SELF_COUNT("Self Count", INTEGER),
    TOTAL_COUNT("Total Count", INTEGER),
    SELF_TIME_PCT("Self Time %", SHARE),
    TOTAL_TIME_PCT("Total Time %", SHARE),
    SELF_COUNT_PCT("Self Count %", SHARE),
    TOTAL_COUNT_PCT("Total Count %", SHARE),
    BASE_SELF_TIME("Base Self Time", LONG),
    BASE_TOTAL_TIME("Base Total Time", LONG),
    BASE_SELF_COUNT("Base Self Count", INTEGER),
    BASE_TOTAL_COUNT("Base Total Count", INTEGER),
    BASE_SELF_TIME_PCT("Base Self Time %", SHARE),
    BASE_TOTAL_TIME_PCT("Base Total Time %", SHARE),
    BASE_SELF_COUNT_PCT("Base Self Count %", SHARE),
    BASE_TOTAL_COUNT_PCT("Base Total Count %", SHARE),
    NEW_SELF_TIME("New Self Time", LONG),
    NEW_TOTAL_TIME("New Total Time", LONG),
    NEW_SELF_COUNT("New Self Count", INTEGER),
    NEW_TOTAL_COUNT("New Total Count", INTEGER),
    NEW_SELF_TIME_PCT("New Self Time %", SHARE),
    NEW_TOTAL_TIME_PCT("New Total Time %", SHARE),
    NEW_SELF_COUNT_PCT("New Self Count %", SHARE),
    NEW_TOTAL_COUNT_PCT("New Total Count %", SHARE),
    SELF_TIME_DIFF("Self Time Diff", LONG),
    TOTAL_TIME_DIFF("Total Time Diff", LONG),
    SELF_COUNT_DIFF("Self Count Diff", INTEGER),
    TOTAL_COUNT_DIFF("Total Count Diff", INTEGER),
    SELF_TIME_PCT_DIFF("Self Time % Diff", PERCENT),
    TOTAL_TIME_PCT_DIFF("Total Time % Diff", PERCENT),
    SELF_COUNT_PCT_DIFF("Self Count % Diff", PERCENT),
    TOTAL_COUNT_PCT_DIFF("Total Count % Diff", PERCENT);

    // Class Properties

    /**
     * These maps are used for mapping the Targets to the corresponding extractors for each of the aggregation item
     * types.
     */
    private static Map<Target, Function<Entry, ?>> entryExtractors = new HashMap<>();
    private static Map<Target, Function<DiffEntry, ?>> diffEntryExtractors = new HashMap<>();

    // Class Constructors

    static
    {
        entryExtractors.put(SELF_TIME_PCT, Entry::getSelfTimePct);
        entryExtractors.put(SELF_COUNT_PCT, Entry::getSelfCntPct);
        entryExtractors.put(TOTAL_TIME_PCT, Entry::getTotalTimePct);
        entryExtractors.put(TOTAL_COUNT_PCT, Entry::getTotalCntPct);
        entryExtractors.put(SELF_COUNT, Entry::getSelfCnt);
        entryExtractors.put(TOTAL_COUNT, Entry::getTotalCnt);
        entryExtractors.put(SELF_TIME, Entry::getSelfTime);
        entryExtractors.put(TOTAL_TIME, Entry::getTotalTime);
        entryExtractors.put(KEY, Entry::getKey);

        diffEntryExtractors.put(BASE_SELF_TIME_PCT, DiffEntry::getBaseSelfTimePct);
        diffEntryExtractors.put(BASE_SELF_COUNT_PCT, DiffEntry::getBaseSelfCntPct);
        diffEntryExtractors.put(BASE_TOTAL_TIME_PCT, DiffEntry::getBaseTotalTimePct);
        diffEntryExtractors.put(BASE_TOTAL_COUNT_PCT, DiffEntry::getBaseTotalCntPct);
        diffEntryExtractors.put(NEW_SELF_TIME_PCT, DiffEntry::getNewSelfTimePct);
        diffEntryExtractors.put(NEW_SELF_COUNT_PCT, DiffEntry::getNewSelfCntPct);
        diffEntryExtractors.put(NEW_TOTAL_TIME_PCT, DiffEntry::getNewTotalTimePct);
        diffEntryExtractors.put(NEW_TOTAL_COUNT_PCT, DiffEntry::getNewTotalCntPct);
        diffEntryExtractors.put(SELF_TIME_PCT_DIFF, DiffEntry::getSelfTimePctDiff);
        diffEntryExtractors.put(SELF_COUNT_PCT_DIFF, DiffEntry::getSelfCntPctDiff);
        diffEntryExtractors.put(TOTAL_TIME_PCT_DIFF, DiffEntry::getTotalTimePctDiff);
        diffEntryExtractors.put(TOTAL_COUNT_PCT_DIFF, DiffEntry::getTotalCntPctDiff);
        diffEntryExtractors.put(SELF_TIME_PCT_DIFF, DiffEntry::getSelfTimePctDiff);
        diffEntryExtractors.put(SELF_COUNT_PCT_DIFF, DiffEntry::getSelfCntPctDiff);
        diffEntryExtractors.put(TOTAL_TIME_PCT_DIFF, DiffEntry::getTotalTimePctDiff);
        diffEntryExtractors.put(TOTAL_COUNT_PCT_DIFF, DiffEntry::getTotalCntPctDiff);
        diffEntryExtractors.put(BASE_SELF_COUNT, DiffEntry::getBaseSelfCnt);
        diffEntryExtractors.put(BASE_TOTAL_COUNT, DiffEntry::getBaseTotalCnt);
        diffEntryExtractors.put(NEW_SELF_COUNT, DiffEntry::getNewSelfCnt);
        diffEntryExtractors.put(NEW_TOTAL_COUNT, DiffEntry::getNewTotalCnt);
        diffEntryExtractors.put(SELF_COUNT_DIFF, DiffEntry::getSelfCntDiff);
        diffEntryExtractors.put(TOTAL_COUNT_DIFF, DiffEntry::getTotalCntDiff);
        diffEntryExtractors.put(SELF_COUNT_DIFF, DiffEntry::getSelfCntDiff);
        diffEntryExtractors.put(TOTAL_COUNT_DIFF, DiffEntry::getTotalCntDiff);
        diffEntryExtractors.put(BASE_SELF_TIME, DiffEntry::getBaseSelfTime);
        diffEntryExtractors.put(BASE_TOTAL_TIME, DiffEntry::getBaseTotalTime);
        diffEntryExtractors.put(NEW_SELF_TIME, DiffEntry::getNewSelfTime);
        diffEntryExtractors.put(NEW_TOTAL_TIME, DiffEntry::getNewTotalTime);
        diffEntryExtractors.put(SELF_TIME_DIFF, DiffEntry::getSelfTimeDiff);
        diffEntryExtractors.put(TOTAL_TIME_DIFF, DiffEntry::getTotalTimeDiff);
        diffEntryExtractors.put(SELF_TIME_DIFF, DiffEntry::getSelfTimeDiff);
        diffEntryExtractors.put(TOTAL_TIME_DIFF, DiffEntry::getTotalTimeDiff);
        diffEntryExtractors.put(KEY, DiffEntry::getKey);
    }

    // Instance Properties

    private String name;
    private ValueType type;

    // Instance Constructors

    /**
     * Trivial private constructor specifying the displayable name and type for the Target value.
     * <p>
     * @param name the displayable name of the Target value
     * @param type the type of the target value
     */
    private Target(String name, ValueType type)
    {
        this.name = name;
        this.type = type;
    }

    // Instance Accessors

    /**
     * Returns the {@link ValueType} of the Target value.
     * <p>
     * @return the {@link ValueType} of the Target value
     */
    public ValueType getType()
    {
        return type;
    }

    // Extractor Methods

    /**
     * Returns the extractor {@link Function} which can extract the Target value from the aggregation items with the
     * specified {@link ValueType}.
     * <p>
     * @param <T> the type of the input of the extractor {@link Function}
     * @param <U> the type of the result of the extractor {@link Function}
     * @param type the {@link ValueType} of the aggregation items the extractor will accept
     * @return the extractor {@link Function} for extracting the Target value
     */
    @SuppressWarnings("unchecked")
    public <T, U> Function<T, U> getExtractor(ItemType type)
    {
        switch (type)
        {
            case ENTRY:
                return (Function<T, U>)entryExtractors.get(this);
            case DIFFENTRY:
                return (Function<T, U>)diffEntryExtractors.get(this);
            default:
                break;
        }
        throw new RuntimeException("Unknown Item Type " + type);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
