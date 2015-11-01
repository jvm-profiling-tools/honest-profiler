/**
 * Copyright (c) 2015 Richard Warburton (richard.warburton@gmail.com)
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.Monitor;
import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.sources.LogSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class FlameGraphCollector implements LogEventListener
{
    private final Map<Long, Method> methods = new HashMap<>();
    private final FlameGraph flameGraph = new FlameGraph();

    private FlameTrace trace;
    private List<Long> lastMethodIds = new ArrayList<>();
    private List<Long> currentMethodIds = new ArrayList<>();

    private static Method unknownMethod = new Method(-1, "<unknown>", "unknown.Unknown", "unknown");

    @Override
    public void handle(TraceStart traceStart)
    {
        addCurrentTrace();
        lastMethodIds = currentMethodIds;
        currentMethodIds = new ArrayList<>();
    }

    @Override
    public void handle(StackFrame stackFrame)
    {
        currentMethodIds.add(stackFrame.getMethodId());
    }

    @Override
    public void handle(Method newMethod)
    {
        methods.put(newMethod.getMethodId(), newMethod);
    }

    @Override
    public void endOfLog()
    {
        addCurrentTrace();
    }

    public FlameGraph collect()
    {
        return flameGraph;
    }

    public static FlameGraph readFlamegraph(LogSource source) throws Exception
    {
        FlameGraphCollector collector = new FlameGraphCollector();

        Monitor.consumeFile(source, collector);

        return collector.collect();
    }

    private void addCurrentTrace()
    {
        if (currentMethodIds == null || currentMethodIds.size() == 0)
            return;

        if (lastMethodIds.equals(currentMethodIds))
        {
            trace.incrementWeight();
            return;
        }

        List<Method> methods = currentMethodIds
            .stream()
            .map(method -> this.methods.getOrDefault(method, unknownMethod))
            .collect(toList());

        trace = new FlameTrace(methods, 1);
        flameGraph.onNewTrace(trace);
    }
}
