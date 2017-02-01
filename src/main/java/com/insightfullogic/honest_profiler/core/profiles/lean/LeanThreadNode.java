package com.insightfullogic.honest_profiler.core.profiles.lean;

public class LeanThreadNode extends LeanNode
{
    private ThreadInfo threadInfo;

    public LeanThreadNode()
    {
        super((FrameInfo)null, null);
    }

    private LeanThreadNode(LeanThreadNode source)
    {
        super(source, null);
        this.threadInfo = source.threadInfo;
    }

    public ThreadInfo getThreadInfo()
    {
        return threadInfo;
    }

    public void setThreadInfo(ThreadInfo threadInfo)
    {
        this.threadInfo = threadInfo;
    }

    @Override
    public LeanThreadNode copy()
    {
        return new LeanThreadNode(this);
    }
}
