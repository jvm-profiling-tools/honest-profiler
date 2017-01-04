package com.insightfullogic.honest_profiler.ports.javafx.model.diff;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;

public class ThreadNodeDiff implements TreeNodeDiff
{
    private String name;
    private int baseSampleCount;
    private int newSampleCount;
    private List<MethodNodeDiff> children;
    private Map<String, MethodNodeDiff> childrenByName;

    public ThreadNodeDiff(ProfileTree baseTree, ProfileTree newTree)
    {
        this.name = newTree == null ? nameForThread(baseTree) : nameForThread(newTree);

        this.children = new ArrayList<>();
        this.childrenByName = new HashMap<>();

        if (baseTree != null)
        {
            updateForBase(baseTree);
        }

        if (newTree != null)
        {
            updateForNew(newTree);
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    public int getBaseSampleCount()
    {
        return baseSampleCount;
    }

    public int getNewSampleCount()
    {
        return newSampleCount;
    }

    @Override
    public List<MethodNodeDiff> getChildren()
    {
        return children;
    }

    public ThreadNodeDiff updateForBase(ProfileTree tree)
    {
        baseSampleCount = tree.getNumberOfSamples();
        updateChildrenForBase(asList(tree.getRootNode()));
        return this;
    }

    public ThreadNodeDiff updateForNew(ProfileTree tree)
    {
        newSampleCount = tree.getNumberOfSamples();
        updateChildrenForNew(asList(tree.getRootNode()));
        return this;
    }

    private void updateChildrenForBase(List<ProfileNode> baseChildren)
    {
        baseChildren.forEach(
            child -> childrenByName.compute(
                child.getFrameInfo().getFullName(),
                (name, diff) -> diff == null ? newDiff(child, true)
                    : diff.updateForBase(child)));
    }

    private void updateChildrenForNew(List<ProfileNode> newChildren)
    {
        newChildren.forEach(
            child -> childrenByName.compute(
                child.getFrameInfo().getFullName(),
                (name, diff) -> diff == null ? newDiff(child, false)
                    : diff.updateForNew(child)));
    }

    private MethodNodeDiff newDiff(ProfileNode node, boolean base)
    {
        MethodNodeDiff result = base ? new MethodNodeDiff(node, null)
            : new MethodNodeDiff(null, node);
        children.add(result);
        return result;
    }

    private String nameForThread(ProfileTree tree)
    {
        return tree.getThreadName() == null || tree.getThreadName().isEmpty() ? "<UNKNOWN>"
            : tree.getThreadName();
    }

    @Override
    public double getBaseSelfPct()
    {
        return 0;
    }

    @Override
    public double getNewSelfPct()
    {
        return 0;
    }

    @Override
    public double getSelfPctDiff()
    {
        return 0;
    }

    @Override
    public double getBaseTotalPct()
    {
        return 0;
    }

    @Override
    public double getNewTotalPct()
    {
        return 0;
    }

    @Override
    public double getTotalPctDiff()
    {
        return 0;
    }

    @Override
    public int getBaseSelfCount()
    {
        return 0;
    }

    @Override
    public int getNewSelfCount()
    {
        return 0;
    }

    @Override
    public int getSelfCountDiff()
    {
        return 0;
    }

    @Override
    public int getBaseTotalCount()
    {
        return 0;
    }

    @Override
    public int getNewTotalCount()
    {
        return 0;
    }

    @Override
    public int getTotalCountDiff()
    {
        return 0;
    }

    @Override
    public int getBaseParentCount()
    {
        return 0;
    }

    @Override
    public int getNewParentCount()
    {
        return 0;
    }

    @Override
    public int getParentCountDiff()
    {
        return 0;
    }
}
