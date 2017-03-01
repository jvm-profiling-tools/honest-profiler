package com.insightfullogic.honest_profiler.ports.javafx.model.diff;

import java.util.List;

public interface NodeDiff
{
    String getName();

    List<? extends NodeDiff> getChildren();

    double getBaseSelfPct();

    double getNewSelfPct();

    double getSelfPctDiff();

    double getBaseTotalPct();

    double getNewTotalPct();

    double getTotalPctDiff();

    int getBaseSelfCnt();

    int getNewSelfCnt();

    int getSelfCntDiff();

    int getBaseTotalCnt();

    int getNewTotalCnt();

    int getTotalCntDiff();

    int getBaseParentCnt();

    int getNewParentCnt();

    int getParentCntDiff();
}
