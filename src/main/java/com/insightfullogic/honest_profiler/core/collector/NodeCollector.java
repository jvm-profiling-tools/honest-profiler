package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class NodeCollector {

    private final Map<Long, NodeCollector> childrenByMethodId;

    private long methodId;

    public int getNumberOfVisits() {
        return visits;
    }

    private int visits;

    public NodeCollector(long methodId) {
        this(methodId, 1);
    }

    public NodeCollector(long methodId, int visits) {
        this.methodId = methodId;
        this.visits = visits;
        childrenByMethodId = new HashMap<>();
    }

    public Stream<NodeCollector> children() {
        return childrenByMethodId.values().stream();
    }

    public List<NodeCollector> getChildren() {
        return new ArrayList<>(childrenByMethodId.values());
    }

    public NodeCollector newChildCall(long methodId) {
        return childrenByMethodId.compute(methodId, (id, prev) ->
            prev == null ? new NodeCollector(id)
                         : prev.callAgain()
        );
    }

    public NodeCollector callAgain() {
        visits++;
        return this;
    }

    // Only gets called on a root node
    public ProfileNode normalise(Function<Long, Method> nameRegistry) {
        return normaliseBy(visits, nameRegistry);
    }

    private ProfileNode normaliseBy(int parentVisits, Function<Long, Method> nameRegistry) {
        Method method = nameRegistry.apply(methodId);

        double timeShare = (double) visits / parentVisits;

        List<ProfileNode> children
            = childrenByMethodId.values()
                                .stream()
                                .map(child -> child.normaliseBy(parentVisits, nameRegistry))
                                .collect(toList());

        return new ProfileNode(method, timeShare, children);
    }

}
