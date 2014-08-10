package com.insightfullogic.honest_profiler.core.collector;

public final class ProfileTree {

    private final int numberOfSamples;
    private final long threadId;
    private final ProfileNode rootNode;

    public ProfileTree(long threadId, ProfileNode rootNode, int numberOfSamples) {
        this.threadId = threadId;
        this.rootNode = rootNode;
        this.numberOfSamples = numberOfSamples;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public ProfileNode getRootNode() {
        return rootNode;
    }

    public long getThreadId() {
        return threadId;
    }

    @Override
    public String toString() {
        return "ProfileTree{" +
                rootNode +
                '}';
    }
}
