package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;

import javafx.scene.control.TreeItem;

public class ThreadNodeAdapter extends TreeItem<ProfileNode>
{
    private final long threadId;
    private final String threadName;
    private final MethodNodeAdapter adapter;
    private long nrOfSamples;

    public ThreadNodeAdapter(long threadId, String threadName, long nrOfSamples)
    {
        super();

        this.threadId = threadId;
        this.threadName = threadName;
        this.nrOfSamples = nrOfSamples;
        adapter = new MethodNodeAdapter();
        setExpanded(true);
        getChildren().add(adapter);
    }

    public void update(ProfileTree tree)
    {
        nrOfSamples = tree.getNumberOfSamples();
        adapter.update(tree.getRootNode());
    }

    public long getThreadId()
    {
        return threadId;
    }

    public String getThreadName()
    {
        return threadName;
    }

    public long getNrOfSamples()
    {
        return nrOfSamples;
    }

    public int getDepth()
    {
        return adapter.getDepth() + 1;
    }
}
