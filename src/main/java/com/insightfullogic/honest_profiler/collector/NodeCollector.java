package com.insightfullogic.honest_profiler.collector;

import com.insightfullogic.honest_profiler.log.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class NodeCollector {

    private final Map<Long, NodeCollector> childrenByMethodId;

    private Method method;
    private int visits;

    public NodeCollector(Method method) {
        this(method, 1);
    }

    public NodeCollector(Method method, int visits) {
        this.method = method;
        this.visits = visits;
        childrenByMethodId = new HashMap<>();
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Stream<NodeCollector> children() {
        return childrenByMethodId.values().stream();
    }

    public List<NodeCollector> getChildren() {
        return new ArrayList<>(childrenByMethodId.values());
    }

    public Method getMethod() {
        return method;
    }

    public NodeCollector newChildCall(long methodId, Function<Long, Method> nameRegistry) {
        return childrenByMethodId.compute(methodId, (id, prev) ->
            prev == null ? new NodeCollector(nameRegistry.apply(id))
                         : prev.callAgain()
        );
    }

    public NodeCollector callAgain() {
        visits++;
        return this;
    }

    // Only gets called on a root node
    public ProfileTreeNode normalise() {
        return normaliseBy(visits);
    }

    private ProfileTreeNode normaliseBy(int parentVisits) {
        double timeShare = (double) visits / parentVisits;

        List<ProfileTreeNode> children
            = childrenByMethodId.values()
                                .stream()
                                .map(child -> child.normaliseBy(visits))
                                .collect(toList());

        return new ProfileTreeNode(method, timeShare, children);
    }

}
