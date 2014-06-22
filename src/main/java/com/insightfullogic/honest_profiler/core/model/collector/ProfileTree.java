package com.insightfullogic.honest_profiler.core.model.collector;

public final class ProfileTree {

    private final ProfileNode rootNode;

    public ProfileTree(ProfileNode rootNode) {
        this.rootNode = rootNode;
    }

    public ProfileNode getRootNode() {
        return rootNode;
    }

}
