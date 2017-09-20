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
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_07;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_08;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_07;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_08;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;

public class Scenario07 extends SimplifiedLogScenario
{
    /**
     * Several stack traces are emitted on two threads. The BCI and Line Numbers of the frames are constructed so as to
     * trigger different aggregations for different {@link FrameGrouping}s.
     */
    public Scenario07()
    {
        super("Scenario 07");

        addThreads(T_07, T_08);
        addMethods(M_01, M_02, M_03, M_04, M_05, M_07, M_08);

        addStack(T_07.getThreadId(), F_07_1, F_02, F_01);
        addStack(T_07.getThreadId(), F_07_1, F_02, F_01);
        addStack(T_07.getThreadId(), F_07_2, F_02, F_01);
        addStack(T_07.getThreadId(), F_07_2, F_02, F_01);
        addStack(T_07.getThreadId(), F_07_2, F_02, F_01);
        addStack(T_07.getThreadId(), F_05, F_07_1, F_04, F_03);
        addStack(T_07.getThreadId(), F_05, F_07_1, F_04, F_03);
        addStack(T_07.getThreadId(), F_05, F_07_2, F_04, F_03);
        addStack(T_07.getThreadId(), F_05, F_07_2, F_04, F_03);
        addStack(T_07.getThreadId(), F_05, F_07_2, F_04, F_03);

        addStack(T_08.getThreadId(), F_08_1, F_02, F_01);
        addStack(T_08.getThreadId(), F_08_1, F_02, F_01);
        addStack(T_08.getThreadId(), F_08_2, F_02, F_01);
        addStack(T_08.getThreadId(), F_08_2, F_02, F_01);
        addStack(T_08.getThreadId(), F_08_2, F_02, F_01);
        addStack(T_08.getThreadId(), F_05, F_08_3, F_04, F_03);
        addStack(T_08.getThreadId(), F_05, F_08_3, F_04, F_03);
        addStack(T_08.getThreadId(), F_05, F_08_4, F_04, F_03);
        addStack(T_08.getThreadId(), F_05, F_08_4, F_04, F_03);
        addStack(T_08.getThreadId(), F_05, F_08_4, F_04, F_03);

        end();
    }
}
