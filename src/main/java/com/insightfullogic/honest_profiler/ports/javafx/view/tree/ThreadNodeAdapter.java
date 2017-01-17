package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedNode;

import javafx.scene.control.TreeItem;

public class ThreadNodeAdapter extends TreeItem<AggregatedNode>
{
    private final String threadId;
    private final String threadName;
    private final MethodNodeAdapter adapter;
    private long nrOfSamples;

    public ThreadNodeAdapter(String threadId, String threadName, long nrOfSamples)
    {
        super();

        this.threadId = threadId;
        this.threadName = threadName;
        this.nrOfSamples = nrOfSamples;
        adapter = new MethodNodeAdapter();
        setExpanded(true);
        getChildren().add(adapter);
    }

    public void update(AggregatedNode tree)
    {
        nrOfSamples = tree.getData().getTotalCnt();
        adapter.update(tree.getChildren().get(0));
    }

    public String getThreadId()
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
