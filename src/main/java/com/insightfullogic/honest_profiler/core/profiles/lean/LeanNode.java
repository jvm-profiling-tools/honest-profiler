package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.util.HashMap;
import java.util.Map;

public class LeanNode
{
    private final NumericInfo data;
    private final Map<FrameInfo, LeanNode> children;

    public LeanNode(long nanos)
    {
        data = new NumericInfo(nanos);
        children = new HashMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param source the source SlimNode which is being copied
     */
    private LeanNode(LeanNode source)
    {
        this.data = source.data.copy();
        this.children = new HashMap<>();
        source.children.forEach((key, value) -> this.children.put(key.copy(), value.copy()));
    }

    public LeanNode update(long nanos, FrameInfo child)
    {
        data.update(nanos, child == null);

        if (child == null)
        {
            return null;
        }

        return children
            .compute(child, (k, v) -> v == null ? new LeanNode(nanos) : v.update(nanos, child));
    }

    public LeanNode copy()
    {
        return new LeanNode(this);
    }
}
