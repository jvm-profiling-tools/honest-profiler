package com.insightfullogic.honest_profiler.framework.checker;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;

/**
 * The {@link CheckAdapter} is provided to allow automated checking of a representation of a {@link Flat} or
 * {@link Tree} aggregation.
 * <p>
 * A representation could trivially be the {@link Flat} or {@link Tree} itself, but it could also be a rendered table in
 * the UI or console.
 * <p>
 * @param <T> the type of the representation
 */
public interface CheckAdapter<T>
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

    /**
     * Checks that the self count of the item with the specified aggregation key in the represented aggregation conforms
     * to the expected value.
     * <p>
     * @param expected the expected self count of the aggregated item
     */
    void assertSelfCntEquals(T key, int expected);

    /**
     * Checks that the total count of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected total count of the aggregated item
     */
    void assertTotalCntEquals(T key, int expected);

    /**
     * Checks that the self time of the item with the specified aggregation key in the represented aggregation conforms
     * to the expected value.
     * <p>
     * @param expected the expected self time of the aggregated item
     */
    void assertSelfTimeEquals(T key, long expected);

    /**
     * Checks that the total time of the item with the specified aggregation key in the represented aggregation conforms
     * to the expected value.
     * <p>
     * @param expected the expected total time of the aggregated item
     */
    void assertTotalTimeEquals(T key, long expected);

    /**
     * Checks that the self count percent of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected self count percent of the aggregated item
     */
    void assertSelfCntPctEquals(T key, double expected);

    /**
     * Checks that the total count percent of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected total count percent of the aggregated item
     */
    void assertTotalCntPctEquals(T key, double expected);

    /**
     * Checks that the self time percent of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected self time percent of the aggregated item
     */
    void assertSelfTimePctEquals(T key, double expected);

    /**
     * Checks that the total time percent of the item with the specified aggregation key in the represented aggregation
     * conforms to the expected value.
     * <p>
     * @param expected the expected total time percent of the aggregated item
     */
    void assertTotalTimePctEquals(T key, double expected);
}
