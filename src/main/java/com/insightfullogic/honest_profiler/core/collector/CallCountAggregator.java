/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class CallCountAggregator<T>
{
    private static final Comparator<Entry<?, CallCounts>> sortBySelfCount
        = comparing((Entry<?, CallCounts> entry) -> entry.getValue().getTimeInvokingThis()).reversed();

    private final Map<T, CallCounts> callCountsByKey = new HashMap<>();
    private final Map<Long, Method> methodByMethodId;
    private final Function<T, Long> getMethodId;

    CallCountAggregator(
        final Map<Long, Method> methodByMethodId, final Function<T, Long> getMethodId)
    {
        this.methodByMethodId = methodByMethodId;
        this.getMethodId = getMethodId;
    }

    void onFrameAppearance(final T key, final boolean endOfTrace)
    {
        callCountsByKey
            .computeIfAbsent(key, ignore -> new CallCounts())
            .onAppearance(endOfTrace);
    }

    public List<FlatProfileEntry> aggregate(final int traceCount)
    {
        return callCountsByKey
            .entrySet()
            .stream()
            .sorted(sortBySelfCount)
            .map((entry) -> toFlatProfileEntry(entry.getKey(), entry.getValue(), traceCount))
            .collect(toList());
    }

    private FlatProfileEntry toFlatProfileEntry(
        final T key, final CallCounts callCounts, final int traceCount)
    {
        int totalCount = callCounts.getTimeAppeared();
        int selfCount = callCounts.getTimeInvokingThis();

        Method method;
        final Long methodId = getMethodId.apply(key);
        method = methodByMethodId.get(methodId);
        if (method == null)
        {
            method = new Method(methodId, "UNKNOWN", "UNKNOWN", String.valueOf(methodId));
        }
        Frame frame;
        if (key instanceof StackFrame)
        {
            frame = new FullFrame(method, (StackFrame) key);
        }
        else
        {
            frame = method;
        }
        return new FlatProfileEntry(frame, totalCount, selfCount, traceCount);
    }
}
