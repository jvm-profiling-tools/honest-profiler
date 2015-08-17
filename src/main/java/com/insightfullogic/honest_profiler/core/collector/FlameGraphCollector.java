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

public class FlameGraphCollector implements LogEventListener
{
    private Map<Long, Method> methods = new HashMap<>();
    private Map<List<Long>, Long> flameGraph = new HashMap<>();
    private List<Long> currentTrace;
    private static Method unknownMethod = new Method(-1, "<unknown>", "unknown.Unknown", "unknown");

    @Override
    public void handle(TraceStart traceStart)
    {
        addCurrentTrace();
        currentTrace = new ArrayList<>();
    }

    @Override
    public void handle(StackFrame stackFrame)
    {
        currentTrace.add(stackFrame.getMethodId());
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

    public FlameGraph toData() throws Exception
    {
        Map<List<Method>, Long> converted = new HashMap<List<Method>, Long>(flameGraph.size());

        for (Map.Entry<List<Long>, Long> entry : flameGraph.entrySet())
        {
            List<Long> methodIds = entry.getKey();
            List<Method> trace = new ArrayList<Method>(methodIds.size());

            for (Long methodId : methodIds)
            {
                Method method = methods.get(methodId);

                if (method == null)
                    method = unknownMethod;

                trace.add(method);
            }

            converted.put(trace, entry.getValue());
        }

        return new FlameGraph(converted);
    }

    public static FlameGraph readFlamegraph(LogSource source) throws Exception
    {
        FlameGraphCollector collector = new FlameGraphCollector();

        Monitor.consumeFile(source, collector);

        return collector.toData();
    }

    private void addCurrentTrace()
    {
        if (currentTrace == null || currentTrace.size() == 0)
            return;

        Long entry = flameGraph.get(currentTrace);

        flameGraph.put(currentTrace, entry == null ? 1 : entry + 1);
    }
}
