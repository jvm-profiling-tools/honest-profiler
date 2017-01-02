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

import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;
import com.insightfullogic.honest_profiler.core.parser.ThreadMeta;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * NB: Stack trace elements come in the opposite way to the profile
 */
public class LogCollector implements LogEventListener
{
    private static final Comparator<ProfileTree> sortBySampleCount
        = comparing(ProfileTree::getNumberOfSamples).reversed();

    private final ProfileListener listener;

    private final Map<Long, Method> methodByMethodId;
    private final CallCountAggregator<Long> callCountsByMethodId;
    private final CallCountAggregator<StackFrame> callCountsByFrame;
    private final Map<Long, NodeCollector> treesByThreadId;
    private final Map<Long, ThreadMeta> metaByThreadId;
    private final Stack<StackFrame> reversalStack;

    private long currentThread;
    private NodeCollector currentTreeNode;

    private int traceCount;
    private boolean immediatelyEmitProfile;

    public LogCollector(final ProfileListener listener, final boolean immediatelyEmitProfile)
    {
        this.listener = listener;

        methodByMethodId = new HashMap<>();
        metaByThreadId = new HashMap<>();
        callCountsByMethodId = new CallCountAggregator<>(methodByMethodId, id -> id);
        callCountsByFrame = new CallCountAggregator<>(methodByMethodId, StackFrame::getMethodId);
        treesByThreadId = new HashMap<>();
        reversalStack = new Stack<>();

        this.immediatelyEmitProfile = immediatelyEmitProfile;
        traceCount = 0;
        currentTreeNode = null;
    }

    @Override
    public void handle(TraceStart traceStart)
    {
        collectThreadDump();
        emitProfileIfNeeded();
        traceCount++;
        reversalStack.clear();
        currentThread = traceStart.getThreadId();
        currentTreeNode = null;
    }

    @Override
    public void handle(StackFrame stackFrame)
    {
        reversalStack.push(stackFrame);
    }

    private void collectThreadDump()
    {
        while (!reversalStack.empty())
        {
            collectStackFrame(reversalStack.size() == 1, reversalStack.pop());
        }
    }

    private void collectStackFrame(boolean endOfTrace, StackFrame stackFrame)
    {
        long methodId = stackFrame.getMethodId();

        callCountsByMethodId.onFrameAppearance(methodId, endOfTrace);
        callCountsByFrame.onFrameAppearance(stackFrame, endOfTrace);

        if (currentTreeNode == null)
        {
            // might be null if the method hasn't been seen before
            // if this is the case then the handle(Method) will patch it
            currentTreeNode = treesByThreadId.compute(currentThread, (id, previous) -> {
                if (previous != null)
                    return previous.callAgain();

                return new NodeCollector(methodId);
            });
        }
        else
        {
            currentTreeNode = currentTreeNode.newChildCall(methodId);
        }
    }

    @Override
    public void handle(Method newMethod)
    {
        methodByMethodId.put(newMethod.getMethodId(), newMethod);
        emitProfileIfNeeded();
    }

    @Override
    public void handle(ThreadMeta newThreadMeta)
    {
        metaByThreadId.merge(
            newThreadMeta.getThreadId(),
            newThreadMeta,
            (oldMeta, newMeta) -> oldMeta.update(newMeta));
    }

    @Override
    public void endOfLog()
    {
        collectThreadDump();
        emitProfile();
    }

    private void emitProfileIfNeeded()
    {
        if (traceCount > 0 && immediatelyEmitProfile)
        {
            emitProfile();
        }
    }

    private void emitProfile()
    {
        listener.accept(
            new Profile(
                traceCount,
                callCountsByMethodId.aggregate(traceCount),
                callCountsByFrame.aggregate(traceCount),
                buildTreeProfile()));
    }

    private List<ProfileTree> buildTreeProfile()
    {
        return treesByThreadId.entrySet()
            .stream()
            .map(node -> {
                final Long threadId = node.getKey();
                final ThreadMeta meta = metaByThreadId.get(threadId);
                final NodeCollector collector = node.getValue();
                if (meta == null) {
                    return new ProfileTree(threadId, collector.normalise(methodByMethodId::get), collector.getNumberOfVisits());
                }
                return new ProfileTree(meta, collector.normalise(methodByMethodId::get), collector.getNumberOfVisits());
            })
            .sorted(sortBySampleCount)
            .collect(toList());
    }

}
