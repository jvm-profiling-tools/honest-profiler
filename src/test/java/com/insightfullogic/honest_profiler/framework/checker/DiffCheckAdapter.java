package com.insightfullogic.honest_profiler.framework.checker;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.FlatDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;

/**
 * The {@link CheckAdapter} is provided to allow automated checking of a representation of a {@link FlatDiff} or
 * {@link TreeDiff} aggregation.
 * <p>
 * A representation could trivially be the {@link FlatDiff} or {@link TreeDiff} itself, but it could also be a rendered
 * table in the UI or console.
 * <p>
 * @param <T> the type of the representation
 */
public interface DiffCheckAdapter<T>
{
    /**
     * Returns the {@link ThreadGrouping} used to construct the aggregation.
     * <p>
     * @return the {@link ThreadGrouping} used to construct the aggregation
     */
    ThreadGrouping getThreadGrouping();

    /**
     * Returns the {@link FrameGrouping} used to construct the aggregation.
     * <p>
     * @return the {@link FrameGrouping} used to construct the aggregation
     */
    FrameGrouping getFrameGrouping();

    /**
     * Checks that the size of the represented aggregation conforms to the expected value.
     * <p>
     * @param expected the expected size of the aggregation
     */
    void assertSizeEquals(int expected);

    // Base Checks

    /**
     * Checks that the base self count of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected base self count of the aggregated item
     */
    void assertBaseSelfCntEquals(T key, int expected);

    /**
     * Checks that the base total count of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected base total count of the aggregated item
     */
    void assertBaseTotalCntEquals(T key, int expected);

    /**
     * Checks that the base self time of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected base self time of the aggregated item
     */
    void assertBaseSelfTimeEquals(T key, long expected);

    /**
     * Checks that the base total time of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected base total time of the aggregated item
     */
    void assertBaseTotalTimeEquals(T key, long expected);

    /**
     * Checks that the base self count percent of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected base self count percent of the aggregated item
     */
    void assertBaseSelfCntPctEquals(T key, double expected);

    /**
     * Checks that the base total count percent of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected base total count percent of the aggregated item
     */
    void assertBaseTotalCntPctEquals(T key, double expected);

    /**
     * Checks that the base self time percent of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected base self time percent of the aggregated item
     */
    void assertBaseSelfTimePctEquals(T key, double expected);

    /**
     * Checks that the base total time percent of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected base total time percent of the aggregated item
     */
    void assertBaseTotalTimePctEquals(T key, double expected);

    // New Checks

    /**
     * Checks that the base self count of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected base self count of the aggregated item
     */
    void assertNewSelfCntEquals(T key, int expected);

    /**
     * Checks that the base total count of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected base total count of the aggregated item
     */
    void assertNewTotalCntEquals(T key, int expected);

    /**
     * Checks that the base self time of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected base self time of the aggregated item
     */
    void assertNewSelfTimeEquals(T key, long expected);

    /**
     * Checks that the base total time of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected base total time of the aggregated item
     */
    void assertNewTotalTimeEquals(T key, long expected);

    /**
     * Checks that the base self count percent of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected base self count percent of the aggregated item
     */
    void assertNewSelfCntPctEquals(T key, double expected);

    /**
     * Checks that the base total count percent of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected base total count percent of the aggregated item
     */
    void assertNewTotalCntPctEquals(T key, double expected);

    /**
     * Checks that the base self time percent of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected base self time percent of the aggregated item
     */
    void assertNewSelfTimePctEquals(T key, double expected);

    /**
     * Checks that the base total time percent of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected base total time percent of the aggregated item
     */
    void assertNewTotalTimePctEquals(T key, double expected);

    // Diff Checks

    /**
     * Checks that the self count diff of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected self count diff of the aggregated item
     */
    void assertSelfCntDiffEquals(T key, int expected);

    /**
     * Checks that the total count diff of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected total count diff of the aggregated item
     */
    void assertTotalCntDiffEquals(T key, int expected);

    /**
     * Checks that the self time diff of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected self time diff of the aggregated item
     */
    void assertSelfTimeDiffEquals(T key, long expected);

    /**
     * Checks that the total time diff of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected total time diff of the aggregated item
     */
    void assertTotalTimeDiffEquals(T key, long expected);

    /**
     * Checks that the self count percent diff of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected self count percent diff of the aggregated item
     */
    void assertSelfCntPctDiffEquals(T key, double expected);

    /**
     * Checks that the total count percent diff of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected total count percent diff of the aggregated item
     */
    void assertTotalCntPctDiffEquals(T key, double expected);

    /**
     * Checks that the self time percent diff of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected self time percent diff of the aggregated item
     */
    void assertSelfTimePctDiffEquals(T key, double expected);

    /**
     * Checks that the total time percent diff of the item with the specified aggregation key in the represented
     * aggregation conforms to the expected value.
     * <p>
     * @param expected the expected total time percent diff of the aggregated item
     */
    void assertTotalTimePctDiffEquals(T key, double expected);
}
