package com.insightfullogic.honest_profiler.collector;

public final class ProfileTree {

    private final ProfileTreeNode rootNode;

    public ProfileTree(ProfileTreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public ProfileTreeNode getRootNode() {
        return rootNode;
    }

}
