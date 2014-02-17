package com.insightfullogic.honest_profiler.collector;

import com.insightfullogic.honest_profiler.log.Method;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public final class ProfileTreeNode {

    private final List<ProfileTreeNode> children;
    private final Method method;
    private final double timeShare;

    public ProfileTreeNode(Method method, double timeShare) {
        this(method, timeShare, emptyList());
    }
    public ProfileTreeNode(Method method, double timeShare, List<ProfileTreeNode> children) {
        this.method = method;
        this.timeShare = timeShare;
        this.children = children;
    }

    public Stream<ProfileTreeNode> children() {
        return children.stream();
    }

    public List<ProfileTreeNode> getChildren() {
        return children;
    }

    public double getTimeShare() {
        return timeShare;
    }

    public Method getMethod() {
        return method;
    }

}
