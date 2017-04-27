package com.insightfullogic.honest_profiler.framework.scenario;

import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_02;

public class Scenario05 extends SimplifiedLogScenario
{
    /**
     * One stack trace consisting of one frame is emitted on two threads.
     */
    public Scenario05()
    {
        super("Scenario 05");

        addThreads(T_01, T_02);
        addMethods(M_01);
        addStack(1, F_01);
        addStack(2, F_01);
        end();
    }
}
