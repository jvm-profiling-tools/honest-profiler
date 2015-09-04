/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.Method;
import com.insightfullogic.honest_profiler.core.parser.StackFrame;
import com.insightfullogic.honest_profiler.core.parser.TraceStart;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.Map.Entry;
import static java.util.stream.Collectors.toList;

/**
 * NB: Stack trace elements come in the opposite way to the profile
 */
public class LogCollector implements LogEventListener {

    private static final Comparator<Entry<?,CallCounts>> sortBySelfCount = comparing((Entry<?, CallCounts> entry) -> entry.getValue().timeInvokingThis)
                                                                         .reversed();
    private static final Comparator<ProfileTree> sortBySampleCount = comparing(ProfileTree::getNumberOfSamples).reversed();

    public static final int NOT_AWAITING = -1;

    private final ProfileListener listener;

    private final Map<Long, Method> methodByMethodId;
    private final Map<Long, CallCounts> callCountsByMethodId;
    private final Map<StackFrame, CallCounts> callCountsByFrame;
    private final Map<Long, NodeCollector> treesByThreadId;
    private final Stack<StackFrame> reversalStack;

    private long currentThread;
    private NodeCollector currentTreeNode;

    private int traceCount;
    private boolean immediatelyEmitProfile;

    public LogCollector(ProfileListener listener, boolean immediatelyEmitProfile) {
        this.listener = listener;

        methodByMethodId = new HashMap<>();
        callCountsByMethodId = new HashMap<>();
        callCountsByFrame = new HashMap<>();
        treesByThreadId = new HashMap<>();
        reversalStack = new Stack<>();

        this.immediatelyEmitProfile = immediatelyEmitProfile;
        traceCount = 0;
        currentTreeNode = null;
    }

    @Override
    public void handle(TraceStart traceStart) {
        collectThreadDump();
        emitProfileIfNeeded();
        traceCount++;
        reversalStack.clear();
        currentThread = traceStart.getThreadId();
        currentTreeNode = null;
    }

    @Override
    public void handle(StackFrame stackFrame) {
        reversalStack.push(stackFrame);
    }

    private void collectThreadDump() {
        while (!reversalStack.empty()) {
            collectStackFrame(reversalStack.size() == 1, reversalStack.pop());
        }
    }

    private void collectStackFrame(boolean endOfTrace, StackFrame stackFrame) {
        long methodId = stackFrame.getMethodId();
        callCountsByMethodId.compute(methodId, (key, callCounts) -> {
            if (callCounts == null) {
                callCounts = new CallCounts(0, 0);
            }

            callCounts.timeAppeared++;
            if (endOfTrace)
                callCounts.timeInvokingThis++;
            return callCounts;
        });
        
        callCountsByFrame.compute(stackFrame, (key, callCounts) -> {
            if (callCounts == null) {
                callCounts = new CallCounts(0, 0);
            }

            callCounts.timeAppeared++;
            if (endOfTrace)
                callCounts.timeInvokingThis++;
            return callCounts;
        });

        if (currentTreeNode == null) {
            // might be null if the method hasn't been seen before
            // if this is the case then the handle(Method) will patch it
            currentTreeNode = treesByThreadId.compute(currentThread, (id, previous) -> {
                if (previous != null)
                    return previous.callAgain();

                return new NodeCollector(methodId);
            });
        } else {
            currentTreeNode = currentTreeNode.newChildCall(methodId);
        }
    }

    @Override
    public void handle(Method newMethod) {
        methodByMethodId.put(newMethod.getMethodId(), newMethod);
        emitProfileIfNeeded();
    }

    @Override
    public void endOfLog() {
        collectThreadDump();
        emitProfile();
    }

    private void emitProfileIfNeeded() {
        if (traceCount > 0 && immediatelyEmitProfile) {
            emitProfile();
        }
    }

    private void emitProfile() {
        List<FlatProfileEntry> flatProfileByMethod = buildFlatProfileByMethod();
        List<FlatProfileEntry> flatProfileByFrame = buildFlatProfileByFrame();
        List<ProfileTree> trees = buildTreeProfile();
        Profile profile = new Profile(traceCount, flatProfileByMethod, flatProfileByFrame, trees);
        listener.accept(profile);
    }

    private List<ProfileTree> buildTreeProfile() {
        return treesByThreadId.entrySet()
                            .stream()
                            .map(node -> {
                                final Long threadId = node.getKey();
                                final NodeCollector collector = node.getValue();
                                return new ProfileTree(threadId, collector.normalise(methodByMethodId::get), collector.getNumberOfVisits());
                            })
                            .sorted(sortBySampleCount)
                            .collect(toList());
    }

    private List<FlatProfileEntry> buildFlatProfileByMethod() {
        return callCountsByMethodId.entrySet()
                         .stream()
                         .sorted(sortBySelfCount)
                         .map(this::toFlatProfileEntry)
                         .collect(toList());
    }

    private FlatProfileEntry toFlatProfileEntry(Entry<Long, CallCounts> entry) {
        Method method = methodByMethodId.get(entry.getKey());
        final CallCounts callCounts = entry.getValue();
        double totalTimeShare = (double) callCounts.timeAppeared / traceCount;
        double selfTimeShare = (double) callCounts.timeInvokingThis / traceCount;
        return new FlatProfileEntry(method, totalTimeShare, selfTimeShare);
    }
    
    private List<FlatProfileEntry> buildFlatProfileByFrame() {
        return callCountsByFrame.entrySet()
                         .stream()
                         .sorted(sortBySelfCount)
                         .map(this::toFlatByFrameProfileEntry)
                         .collect(toList());
    }

    private FlatProfileEntry toFlatByFrameProfileEntry(Entry<StackFrame, CallCounts> entry) {
        final long methodId = entry.getKey().getMethodId();
        Method method = methodByMethodId.get(methodId);
        if (method == null) {
            method = new Method(methodId,"UNKOWN","UNKOWN",String.valueOf(methodId));
        }
        final CallCounts callCounts = entry.getValue();
        double totalTimeShare = (double) callCounts.timeAppeared / traceCount;
        double selfTimeShare = (double) callCounts.timeInvokingThis / traceCount;
        return new FlatProfileEntry(new FullFrameInfo(method, entry.getKey()), totalTimeShare, selfTimeShare);
    }

}
