package com.insightfullogic.honest_profiler.ports.javafx.model.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MethodNodeDiff implements NodeDiff
{
    private String name;

    private IntegerProperty baseSelfCount = new SimpleIntegerProperty();
    private IntegerProperty newSelfCount = new SimpleIntegerProperty();
    private IntegerProperty baseTotalCount = new SimpleIntegerProperty();
    private IntegerProperty newTotalCount = new SimpleIntegerProperty();
    private IntegerProperty baseParentCount = new SimpleIntegerProperty();
    private IntegerProperty newParentCount = new SimpleIntegerProperty();

    private List<MethodNodeDiff> children;
    private Map<String, MethodNodeDiff> childrenByName;

    public MethodNodeDiff(ProfileNode baseNode, ProfileNode newNode)
    {
        this.name = newNode == null ? baseNode.getFrameInfo().getFullName()
            : newNode.getFrameInfo().getFullName();

        children = new ArrayList<>();
        childrenByName = new HashMap<>();

        if (baseNode != null)
        {
            updateForBase(baseNode);
        }

        if (newNode != null)
        {
            updateForNew(newNode);
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public List<MethodNodeDiff> getChildren()
    {
        return children;
    }

    @Override
    public double getBaseSelfPct()
    {
        return getBaseParentCnt() == 0 ? 0 : getBaseSelfCnt() / (double) getBaseParentCnt();
    }

    @Override
    public double getNewSelfPct()
    {
        return getNewParentCnt() == 0 ? 0 : getNewSelfCnt() / (double) getNewParentCnt();
    }

    @Override
    public double getSelfPctDiff()
    {
        return getNewSelfPct() - getBaseSelfPct();
    }

    @Override
    public double getBaseTotalPct()
    {
        return getBaseParentCnt() == 0 ? 0 : getBaseTotalCnt() / (double) getBaseParentCnt();
    }

    @Override
    public double getNewTotalPct()
    {
        return getNewParentCnt() == 0 ? 0 : getNewTotalCnt() / (double) getNewParentCnt();
    }

    @Override
    public double getTotalPctDiff()
    {
        return getNewTotalPct() - getBaseTotalPct();
    }

    @Override
    public int getBaseSelfCnt()
    {
        return baseSelfCount.get();
    }

    @Override
    public int getNewSelfCnt()
    {
        return newSelfCount.get();
    }

    @Override
    public int getSelfCntDiff()
    {
        return getNewSelfCnt() - getBaseSelfCnt();
    }

    @Override
    public int getBaseTotalCnt()
    {
        return baseTotalCount.get();
    }

    @Override
    public int getNewTotalCnt()
    {
        return newTotalCount.get();
    }

    @Override
    public int getTotalCntDiff()
    {
        return getNewTotalCnt() - getBaseTotalCnt();
    }

    @Override
    public int getBaseParentCnt()
    {
        return baseParentCount.get();
    }

    @Override
    public int getNewParentCnt()
    {
        return newParentCount.get();
    }

    @Override
    public int getParentCntDiff()
    {
        return getNewParentCnt() - getBaseParentCnt();
    }

    public MethodNodeDiff updateForBase(ProfileNode baseNode)
    {
        baseSelfCount.set(baseNode == null ? 0 : baseNode.getSelfCount());
        baseTotalCount.set(baseNode == null ? 0 : baseNode.getTotalCount());
        baseParentCount.set(baseNode == null ? 0 : baseNode.getParentCount());
        updateChildrenForBase(baseNode.getChildren());
        return this;
    }

    public MethodNodeDiff updateForNew(ProfileNode newNode)
    {
        newSelfCount.set(newNode == null ? 0 : newNode.getSelfCount());
        newTotalCount.set(newNode == null ? 0 : newNode.getTotalCount());
        newParentCount.set(newNode == null ? 0 : newNode.getParentCount());
        updateChildrenForNew(newNode.getChildren());
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
}
