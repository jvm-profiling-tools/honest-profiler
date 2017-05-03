package com.insightfullogic.honest_profiler.framework.scenario;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.parser.LogEvent;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.framework.LeanLogCollectorDriver;

/**
 * A scenario holds a series of {@link LogEvent}s which will be fed to a {@link LeanLogCollectorDriver} when executed.
 */
public class LogScenario
{
    // Instance Properties

    private String name;
    private List<LogEvent> eventList;

    // Instance Constructors

    /**
     * Constructs an empty scenario.
     * <p>
     * @param name the name of the scenario
     */
    protected LogScenario(String name)
    {
        this.name = name;
        eventList = new ArrayList<>();
    }

    // Instance Accessors

    /**
     * Returns the name of the scenario.
     * <p>
     * @return the name of the scenario
     */
    public String getName()
    {
        return name;
    }

    // Log Event Addition

    /**
     * Adds {@link Method} events to the scenario.
     * <p>
     * @param methods the {@link Method} events to be added.
     */
    protected void addMethods(Method... methods)
    {
        eventList.addAll(asList(methods));
    }

    /**
     * Adds {@link ThreadMeta} events to the scenario.
     * <p>
     * @param methods the {@link ThreadMeta} events to be added.
     */
    protected void addThreads(ThreadMeta... threads)
    {
        eventList.addAll(asList(threads));
    }

    /**
     * Adds a {@link TraceStart} event to the scenario.
     * <p>
     * @param start the {@link TraceStart} event to be added
     */
    protected void addStart(TraceStart start)
    {
        eventList.add(start);
    }

    /**
     * Adds a {@link TraceStart} event to the scenario as well as a series of {@link StackFrame} events representing a
     * stack trace. Please note that the first {@link StackFrame} actually represents the "bottom" of the trace, i.e.
     * the actual method being executed. The last {@link StackFrame} represents the ancestor method called directly by
     * the {@link Thread}.
     *
     * @param start the {@link TraceStart} event to be added
     * @param frames the stack trace to be added
     */
    protected void addStack(TraceStart start, StackFrame... frames)
    {
        eventList.add(start);
        eventList.addAll(asList(frames));
    }

    // Execution

    /**
     * Executes the scenario by feeding the contained log events to the specified {@link LeanLogCollectorDriver}.
     * <p>
     * @param driver the {@link LeanLogCollectorDriver} which will process the log events
     */
    public void execute(LeanLogCollectorDriver driver)
    {
        eventList.forEach(driver::handle);
    }

    /**
     * Executes the scenario by feeding the contained log events to the specified {@link LeanLogCollectorDriver}, and
     * triggering the "end-of-log" event at the end.
     * <p>
     * @param driver the {@link LeanLogCollectorDriver} which will process the log events
     */
    public void executeAndEnd(LeanLogCollectorDriver driver)
    {
        execute(driver);
        driver.endOfLog();
    }

    // Object Implementation

    @Override
    public String toString()
    {
        return name;
    }
}
