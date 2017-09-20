package com.insightfullogic.honest_profiler.framework.scenario;

import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_07_1;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_07_2;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_08_1;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_08_2;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_08_3;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_08_4;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_10_1;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_10_2;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_10_3;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_10_4;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_10_5;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_10_6;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_07;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_08;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_10;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_07;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_08;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_10;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_11;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;

/**
 * A number of stack traces are emitted on a number of threads. Care has been taken to vary frame BCI and Line Nr, and
 * method ids and names in such a way that different aggregations are obtained for the different {@link ThreadGrouping}
 * and {@link FrameGrouping} combinations.
 */
public class Scenario08 extends SimplifiedLogScenario
{
    public Scenario08()
    {
        super("Scenario 08");

        addThreads(T_01, T_02, T_03, T_04, T_05, T_07, T_08, T_10, T_11);
        addMethods(M_01, M_02, M_03, M_04, M_05, M_07, M_08, M_10);

        // Thread 1 (5)
        addStack(T_01.getThreadId(), F_01, F_02, F_03, F_04, F_05);
        addStack(T_01.getThreadId(), F_01, F_02, F_03, F_04, F_05);
        addStack(T_01.getThreadId(), F_01, F_02, F_03, F_04, F_05);
        addStack(T_01.getThreadId(), F_01, F_02, F_03, F_04, F_05);
        addStack(T_01.getThreadId(), F_01, F_02, F_03, F_04, F_05);

        // Thread 2 (5)
        addStack(T_02.getThreadId(), F_05, F_04, F_03, F_02, F_01);
        addStack(T_02.getThreadId(), F_05, F_04, F_03, F_02, F_01);
        addStack(T_02.getThreadId(), F_05, F_04, F_03, F_02, F_01);
        addStack(T_02.getThreadId(), F_05, F_04, F_03, F_02, F_01);
        addStack(T_02.getThreadId(), F_05, F_04, F_03, F_02, F_01);

        // Thread 3 (10)
        addStack(T_03.getThreadId(), F_05, F_04, F_03, F_02, F_01);
        addStack(T_03.getThreadId(), F_05, F_04, F_03, F_02, F_01);
        addStack(T_03.getThreadId(), F_05, F_04, F_03, F_02, F_01);
        addStack(T_03.getThreadId(), F_05, F_04, F_03, F_02, F_01);
        addStack(T_03.getThreadId(), F_05, F_04, F_03, F_02, F_01);

        addStack(T_03.getThreadId(), F_01, F_02, F_03, F_04, F_05);
        addStack(T_03.getThreadId(), F_01, F_02, F_03, F_04, F_05);
        addStack(T_03.getThreadId(), F_01, F_02, F_03, F_04, F_05);
        addStack(T_03.getThreadId(), F_01, F_02, F_03, F_04, F_05);
        addStack(T_03.getThreadId(), F_01, F_02, F_03, F_04, F_05);

        // Thread 4 (11)
        addStack(T_04.getThreadId(), F_01, F_02, F_03, F_04, F_01);
        addStack(T_04.getThreadId(), F_01, F_02, F_03, F_04, F_02);
        addStack(T_04.getThreadId(), F_01, F_02, F_03, F_04, F_03);
        addStack(T_04.getThreadId(), F_01, F_02, F_03, F_04, F_04);
        addStack(T_04.getThreadId(), F_01, F_02, F_03, F_04, F_05);

        addStack(T_04.getThreadId(), F_01, F_02, F_03);
        addStack(T_04.getThreadId(), F_01, F_02, F_03);
        addStack(T_04.getThreadId(), F_01, F_02, F_03);

        addStack(T_04.getThreadId(), F_01, F_02, F_03, F_04, F_01);
        addStack(T_04.getThreadId(), F_01, F_02, F_03, F_04, F_02);
        addStack(T_04.getThreadId(), F_01, F_02);

        // Thread 5 (11)
        addStack(T_05.getThreadId(), F_01, F_02, F_03, F_04, F_01);
        addStack(T_05.getThreadId(), F_01, F_02, F_03, F_04, F_02);
        addStack(T_05.getThreadId(), F_01, F_02, F_03, F_04, F_03);
        addStack(T_05.getThreadId(), F_01, F_02, F_03, F_04, F_04);
        addStack(T_05.getThreadId(), F_01, F_02, F_03, F_04, F_05);

        addStack(T_05.getThreadId(), F_01, F_02, F_03);
        addStack(T_05.getThreadId(), F_01, F_02, F_03);
        addStack(T_05.getThreadId(), F_01, F_02, F_03);

        addStack(T_05.getThreadId(), F_01, F_02, F_03, F_04, F_01);
        addStack(T_05.getThreadId(), F_01, F_02, F_03, F_04, F_02);

        addStack(T_05.getThreadId(), F_01, F_02);

        // Thread 6 (No ThreadInfo) (11)
        addStack(6, F_01, F_02, F_03, F_04, F_01);
        addStack(6, F_01, F_02, F_03, F_04, F_02);
        addStack(6, F_01, F_02, F_03, F_04, F_03);
        addStack(6, F_01, F_02, F_03, F_04, F_04);
        addStack(6, F_01, F_02, F_03, F_04, F_05);

        addStack(6, F_01, F_02, F_03);
        addStack(6, F_01, F_02, F_03);
        addStack(6, F_01, F_02, F_03);

        addStack(6, F_02);

        addStack(6, F_01, F_02, F_03, F_04, F_01);
        addStack(6, F_01, F_02, F_03, F_04, F_02);

        // Thread 7 (6)
        addStack(T_07.getThreadId(), F_07_1);
        addStack(T_07.getThreadId(), F_07_1);
        addStack(T_07.getThreadId(), F_07_1);
        addStack(T_07.getThreadId(), F_07_2);
        addStack(T_07.getThreadId(), F_07_2);
        addStack(T_07.getThreadId(), F_07_2);

        // Thread 8 (14)
        addStack(T_08.getThreadId(), F_08_1);
        addStack(T_08.getThreadId(), F_08_1);
        addStack(T_08.getThreadId(), F_08_2);
        addStack(T_08.getThreadId(), F_08_2);
        addStack(T_08.getThreadId(), F_08_2);
        addStack(T_08.getThreadId(), F_08_3);
        addStack(T_08.getThreadId(), F_08_3);
        addStack(T_08.getThreadId(), F_08_3);
        addStack(T_08.getThreadId(), F_08_3);
        addStack(T_08.getThreadId(), F_08_4);
        addStack(T_08.getThreadId(), F_08_4);
        addStack(T_08.getThreadId(), F_08_4);
        addStack(T_08.getThreadId(), F_08_4);
        addStack(T_08.getThreadId(), F_08_4);

        // Thread 10 (Bci/Line Nr differentiation, Self + Total) (19)
        addStack(T_10.getThreadId(), F_10_1, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_1, F_02, F_01);

        addStack(T_10.getThreadId(), F_10_2, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_2, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_2, F_02, F_01);

        addStack(T_10.getThreadId(), F_10_3, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_3, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_3, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_3, F_02, F_01);

        addStack(T_10.getThreadId(), F_10_4, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_4, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_4, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_4, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_4, F_02, F_01);

        addStack(T_10.getThreadId(), F_10_5, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_5, F_02, F_01);

        addStack(T_10.getThreadId(), F_10_6, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_6, F_02, F_01);
        addStack(T_10.getThreadId(), F_10_6, F_02, F_01);

        // Thread 11 (Bci/Line Nr differentiation, Total only) (19)
        addStack(T_11.getThreadId(), F_03, F_10_1, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_1, F_02, F_01);

        addStack(T_11.getThreadId(), F_03, F_10_2, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_2, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_2, F_02, F_01);

        addStack(T_11.getThreadId(), F_03, F_10_3, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_3, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_3, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_3, F_02, F_01);

        addStack(T_11.getThreadId(), F_03, F_10_4, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_4, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_4, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_4, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_4, F_02, F_01);

        addStack(T_11.getThreadId(), F_03, F_10_5, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_5, F_02, F_01);

        addStack(T_11.getThreadId(), F_03, F_10_6, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_6, F_02, F_01);
        addStack(T_11.getThreadId(), F_03, F_10_6, F_02, F_01);

        end();
    }
}
