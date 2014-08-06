package com.insightfullogic.honest_profiler.core.collector;

public final class ProfileTree {

    private final int numberOfSamples;

    private ProfileNode rootNode;

    public ProfileTree(ProfileNode rootNode, int numberOfSamples) {
        this.rootNode = rootNode;
        this.numberOfSamples = numberOfSamples;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
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
