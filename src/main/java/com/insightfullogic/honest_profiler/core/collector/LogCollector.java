package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.ProfileListener;
import com.insightfullogic.honest_profiler.core.parser.EventListener;
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
public class LogCollector implements EventListener {

    private static final Comparator<Entry<Long,CallCounts>> sortByCount = comparing((Entry<Long, CallCounts> entry) -> entry.getValue().timeInvokingThis)
                                                                         .reversed();
    public static final int NOT_AWAITING = -1;

    private final ProfileListener listener;

    private final Map<Long, Method> methodNames;
    private final Map<Long, CallCounts> callCounts;
    private final Map<Long, NodeCollector> treesByThread;
    private final Stack<StackFrame> reversalStack;

    private long currentThread;
    private NodeCollector currentTreeNode;
    private int expectedNumberOfFrames;

    private int traceCount;
    private boolean logComplete;
    private boolean continuous;

    public LogCollector(ProfileListener listener, boolean continuous) {
        this.listener = listener;

        methodNames = new HashMap<>();
        callCounts = new HashMap<>();
        treesByThread = new HashMap<>();
        reversalStack = new Stack<>();

        this.continuous = continuous;
        traceCount = 0;
        logComplete = false;
        currentTreeNode = null;
        expectedNumberOfFrames = NOT_AWAITING;
    }

    @Override
    public void handle(TraceStart traceStart) {
        traceCount++;
        expectedNumberOfFrames = traceStart.getNumberOfFrames();
        currentThread = traceStart.getThreadId();
        currentTreeNode = null;
    }

    @Override
    public void handle(StackFrame stackFrame) {
        reversalStack.push(stackFrame);
        if (expectedNumberOfFrames == reversalStack.size()) {
            collectThreadDump();
        }
    }

    private void collectThreadDump() {
        while (!reversalStack.empty()) {
            collectStackFrame(reversalStack.size() == 1, reversalStack.pop());
        }
        expectedNumberOfFrames = NOT_AWAITING;
        if (continuous) {
            emitProfile();
        }
    }

    private void collectStackFrame(boolean endOfTrace, StackFrame stackFrame) {
        long methodId = stackFrame.getMethodId();
        callCounts.compute(methodId, (key, callCounts) -> {
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
            currentTreeNode = treesByThread.compute(currentThread, (id, previous) -> {
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
        methodNames.put(newMethod.getMethodId(), newMethod);
        if (expectedNumberOfFrames == NOT_AWAITING && continuous) {
            emitProfile();
        }
    }

    @Override
    public void endOfLog() {
        emitProfile();
        logComplete = true;
    }

    private void emitProfile() {
        List<FlatProfileEntry> flatProfile = buildFlatProfile();
        List<ProfileTree> trees = buildTreeProfile();
        Profile profile = new Profile(traceCount, flatProfile, trees);
        listener.accept(profile);
    }

    private List<ProfileTree> buildTreeProfile() {
        return treesByThread.values()
                            .stream().map(node -> new ProfileTree(node.normalise(methodNames::get)))
                            .collect(toList());
    }

    private List<FlatProfileEntry> buildFlatProfile() {
        return callCounts.entrySet()
                         .stream()
                         .sorted(sortByCount)
                         .map(this::toFlatProfileEntry)
                         .collect(toList());
    }

    private FlatProfileEntry toFlatProfileEntry(Entry<Long, CallCounts> entry) {
        Method method = methodNames.get(entry.getKey());
        final CallCounts callCounts = entry.getValue();
        double totalTimeShare = (double) callCounts.timeAppeared / traceCount;
        double selfTimeShare = (double) callCounts.timeInvokingThis / traceCount;
        return new FlatProfileEntry(method, totalTimeShare, selfTimeShare);
    }

    public boolean isLogComplete() {
        return logComplete;
    }

}
