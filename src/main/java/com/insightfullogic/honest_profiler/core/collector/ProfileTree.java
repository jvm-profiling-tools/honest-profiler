package com.insightfullogic.honest_profiler.core.collector;

public final class ProfileTree {

    private final ProfileNode rootNode;

    public ProfileTree(ProfileNode rootNode) {
        this.rootNode = rootNode;
    }

    public ProfileNode getRootNode() {
        return rootNode;
    }

    @Override
    public String toString() {
        return "ProfileTree{" +
                rootNode +
                '}';
    }
}
