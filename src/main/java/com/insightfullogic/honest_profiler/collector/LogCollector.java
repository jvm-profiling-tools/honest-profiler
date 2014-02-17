package com.insightfullogic.honest_profiler.collector;

import com.insightfullogic.honest_profiler.log.EventListener;
import com.insightfullogic.honest_profiler.log.Method;
import com.insightfullogic.honest_profiler.log.StackFrame;
import com.insightfullogic.honest_profiler.log.TraceStart;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.Map.Entry;
import static java.util.stream.Collectors.toList;

public class LogCollector implements EventListener {

    private static final Comparator<Entry<Long,Integer>> sortByCount = comparing((Entry<Long, Integer> entry) -> entry.getValue())
                                                                      .reversed();

    private final ProfileListener listener;
    private final Map<Long, Method> methodNames;
    private final Map<Long, Integer> callCounts;
    private final Map<Long, NodeCollector> treesByThread;

    private long currentThread;
    private NodeCollector currentTreeNode;

    private int traceCount;
    private boolean logComplete;

    public LogCollector(ProfileListener listener) {
        this.listener = listener;
        traceCount = 0;
        logComplete = false;
        currentTreeNode = null;

        methodNames = new HashMap<>();
        callCounts = new HashMap<>();
        treesByThread = new HashMap<>();
    }

    @Override
    public void handle(TraceStart traceStart) {
        traceCount++;
        currentThread = traceStart.getThreadId();
        currentTreeNode = null;
    }

    @Override
    public void handle(StackFrame stackFrame) {
        long methodId = stackFrame.getMethodId();
        callCounts.compute(methodId, (key, previous) -> previous == null ? 1: previous + 1);

        if (currentTreeNode == null) {
            // might be null if the method hasn't been seen before
            // if this is the case then the handle(Method) will patch it
            // TODO: be more conservative about ordering
            currentTreeNode = treesByThread.compute(currentThread, (id, previous) -> {
                if (previous != null)
                    return previous.callAgain();

                Method method = methodNames.get(stackFrame.getMethodId());
                return new NodeCollector(method);
            });
        } else {
            currentTreeNode = currentTreeNode.newChildCall(stackFrame.getMethodId(), methodNames::get);
        }
    }

    @Override
    public void handle(Method newMethod) {
        methodNames.put(newMethod.getMethodId(), newMethod);
        currentTreeNode.setMethod(newMethod);
    }

    @Override
    public void endOfLog() {
        List<FlatProfileEntry> flatProfile = buildFlatProfile();
        List<ProfileTree> trees = buildTreeProfile();
        Profile profile = new Profile(traceCount, flatProfile, trees);
        listener.accept(profile);
        logComplete = true;
    }

    private List<ProfileTree> buildTreeProfile() {
        return treesByThread.values()
                            .stream().map(node -> new ProfileTree(node.normalise()))
                            .collect(toList());
    }

    private List<FlatProfileEntry> buildFlatProfile() {
        return callCounts.entrySet()
                         .stream()
                         .sorted(sortByCount)
                         .map(this::toFlatProfileEntry)
                         .collect(toList());
    }

    private FlatProfileEntry toFlatProfileEntry(Entry<Long, Integer> entry) {
        Method method = methodNames.get(entry.getKey());
        double timeShare = (double) entry.getValue() / traceCount;
        return new FlatProfileEntry(method, timeShare);
    }

    public boolean isLogComplete() {
        return logComplete;
    }

}
