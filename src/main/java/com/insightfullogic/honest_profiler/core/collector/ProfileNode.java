package com.insightfullogic.honest_profiler.core.collector;

import com.insightfullogic.honest_profiler.core.parser.Method;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public final class ProfileNode {

    private final List<ProfileNode> children;
    private final Method method;
    private final double totalTimeShare;
    private final double selfTimeShare;

    public ProfileNode(Method method, double totalTimeShare) {
        this(method, totalTimeShare, emptyList());
    }
    public ProfileNode(Method method, double totalTimeShare, List<ProfileNode> children) {
        this.method = method;
        this.children = children;
        this.totalTimeShare = totalTimeShare;
        this.selfTimeShare = totalTimeShare - children.stream()
                                                      .mapToDouble(ProfileNode::getTotalTimeShare)
                                                      .sum();
    }

    public Stream<ProfileNode> children() {
        return children.stream();
    }

    public List<ProfileNode> getChildren() {
        return children;
    }

    public double getTotalTimeShare() {
        return totalTimeShare;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "PN{" + totalTimeShare + " " + method.getMethodName() + children + '}';
    }

    public double getSelfTimeShare() {
        return selfTimeShare;
    }
}
