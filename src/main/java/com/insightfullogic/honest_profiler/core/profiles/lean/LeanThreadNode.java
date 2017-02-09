package com.insightfullogic.honest_profiler.core.profiles.lean;

import com.insightfullogic.honest_profiler.core.profiles.lean.info.FrameInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.NumericInfo;
import com.insightfullogic.honest_profiler.core.profiles.lean.info.ThreadInfo;

/**
 * Subclass of {@link LeanNode} which stores {@link ThreadInfo} metadata and aggregated {@link NumericInfo} for a thread
 * in a {@link LeanProfile}. The root nodes in the {@link LeanProfile} trees are {@link LeanThreadNode}s. All other
 * {@link LeanNode}s are instances of the {@link LeanNode} superclass.
 */
public class LeanThreadNode extends LeanNode
{
    // Instance Properties

    private ThreadInfo threadInfo;

    // Instance Constructors

    /**
     * Empty constructor.
     */
    public LeanThreadNode()
    {
        super((FrameInfo)null, null);
    }

    /**
     * Copy constructor.
     * <p>
     * @param source the {@link LeanThreadNode} being copied.
     */
    private LeanThreadNode(LeanThreadNode source)
    {
        super(source, null);
        this.threadInfo = source.threadInfo;
    }

    // Instance Accessors

    @Override
    public boolean isThreadNode()
    {
        return true;
    }

    /**
     * Returns the {@link ThreadInfo} metadata for the represented thread.
     * <p>
     * @return the {@link ThreadInfo} metadata for the represented thread
     */
    public ThreadInfo getThreadInfo()
    {
        return threadInfo;
    }

    /**
     * Sets the {@link ThreadInfo} metadata for the represented thread.
     * <p>
     * @param threadInfo the {@link ThreadInfo} metadata for the represented thread
     */
    public void setThreadInfo(ThreadInfo threadInfo)
    {
        this.threadInfo = threadInfo;
    }

    // Copy Methods

    public LeanThreadNode copy()
    {
        return new LeanThreadNode(this);
    }
}
