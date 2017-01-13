package com.insightfullogic.honest_profiler.core.aggregation;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanNode;
import com.insightfullogic.honest_profiler.core.profiles.lean.NumericInfo;

/**
 * Wrapper for LeanNode with extra links.
 */
public class LinkedLeanNode
{
    private final LeanNode wrapped;
    private final FqmnLink fqmnLink;

    private final LinkedLeanNode parent;
    private final List<LinkedLeanNode> children;

    public LinkedLeanNode(LeanNode wrapped, LinkedLeanNode parent, FqmnLink fqmnLink)
    {
        this.wrapped = wrapped;
        this.fqmnLink = fqmnLink;

        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public LeanNode getLeanNode()
    {
        return wrapped;
    }

    public NumericInfo getData()
    {
        return wrapped.getData();
    }

    public FqmnLink getFqmnLink()
    {
        return fqmnLink;
    }

    public LinkedLeanNode getParent()
    {
        return parent;
    }

    public List<LinkedLeanNode> getChildren()
    {
        return children;
    }

    public void addChild(LinkedLeanNode child)
    {
        children.add(child);
    }
}
