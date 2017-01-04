package com.insightfullogic.honest_profiler.ports.javafx.model.diff;

import java.util.List;

public interface TreeNodeDiff
{
    String getName();

    List<? extends TreeNodeDiff> getChildren();

    double getBaseSelfPct();

    double getNewSelfPct();

    double getSelfPctDiff();

    double getBaseTotalPct();

    double getNewTotalPct();

    double getTotalPctDiff();

    int getBaseSelfCount();

    int getNewSelfCount();

    int getSelfCountDiff();

    int getBaseTotalCount();

    int getNewTotalCount();

    int getTotalCountDiff();

    int getBaseParentCount();

    int getNewParentCount();

    int getParentCountDiff();
}
