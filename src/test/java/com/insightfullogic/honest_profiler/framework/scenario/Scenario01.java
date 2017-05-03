package com.insightfullogic.honest_profiler.framework.scenario;

import static com.insightfullogic.honest_profiler.framework.LogEventFactory.F_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.M_01;
import static com.insightfullogic.honest_profiler.framework.LogEventFactory.T_01;

/**
 * One stack trace consisting of one frame is emitted on one thread.
 */
public class Scenario01 extends SimplifiedLogScenario
{
    public Scenario01()
    {
        super("Scenario 01");

        addThreads(T_01);
        addMethods(M_01);
        addStack(1, F_01);
        end();
    }
}
