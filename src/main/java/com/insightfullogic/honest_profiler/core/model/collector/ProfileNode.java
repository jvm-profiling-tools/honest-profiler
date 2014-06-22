package com.insightfullogic.honest_profiler.core.model.collector;

import com.insightfullogic.honest_profiler.core.model.parser.Method;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public final class ProfileNode {

    private final List<ProfileNode> children;
    private final Method method;
    private final double timeShare;

    public ProfileNode(Method method, double timeShare) {
        this(method, timeShare, emptyList());
    }
    public ProfileNode(Method method, double timeShare, List<ProfileNode> children) {
        this.method = method;
        this.timeShare = timeShare;
        this.children = children;
    }

    public Stream<ProfileNode> children() {
        return children.stream();
    }

    public List<ProfileNode> getChildren() {
        return children;
    }

    public double getTimeShare() {
        return timeShare;
    }

    public Method getMethod() {
        return method;
    }

}
