package com.insightfullogic.honest_profiler.framework.scenario;

import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_02;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_03;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_04;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_05;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_01;

public class Scenario03 extends SimplifiedLogScenario
{
    /**
     * Two identical stack traces consisting of five frames are emitted on one thread.
     */
    public Scenario03()
    {
        super("Scenario 03");

        addThreads(T_01);
        addMethods(M_01, M_02, M_03, M_04, M_05);
        addStack(1, F_01, F_02, F_03, F_04, F_05);
        addStack(1, F_01, F_02, F_03, F_04, F_05);
        end();
    }
}
