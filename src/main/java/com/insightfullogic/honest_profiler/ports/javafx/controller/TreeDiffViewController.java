package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.STRING;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_PARENT_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLLAPSEALLALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPANDALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_TREEDIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandPartial;

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.diff.NodeDiff;
import com.insightfullogic.honest_profiler.ports.javafx.model.diff.TreeProfileDiff;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.model.task.CopyAndFilterProfile;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTreeTableCell;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class TreeDiffViewController extends ProfileDiffViewController<Profile>
{
    @FXML
    private Button filterButton;
    @FXML
    private Button expandAllButton;
    @FXML
    private Button collapseAllButton;
    @FXML
    private TextField quickFilterText;
    @FXML
    private Button quickFilterButton;
    @FXML
    private TreeTableView<NodeDiff> diffTree;
    @FXML
    private TreeTableColumn<NodeDiff, String> methodColumn;
    @FXML
    private TreeTableColumn<NodeDiff, String> baseSelfPct;
    @FXML
    private TreeTableColumn<NodeDiff, String> newSelfPct;
    @FXML
    private TreeTableColumn<NodeDiff, String> selfPctDiff;
    @FXML
    private TreeTableColumn<NodeDiff, String> baseTotalPct;
    @FXML
    private TreeTableColumn<NodeDiff, String> newTotalPct;
    @FXML
    private TreeTableColumn<NodeDiff, String> totalPctDiff;
    @FXML
    private TreeTableColumn<NodeDiff, String> baseSelfCnt;
    @FXML
    private TreeTableColumn<NodeDiff, String> newSelfCnt;
    @FXML
    private TreeTableColumn<NodeDiff, String> selfCntDiff;
    @FXML
    private TreeTableColumn<NodeDiff, String> baseTotalCnt;
    @FXML
    private TreeTableColumn<NodeDiff, String> newTotalCnt;
    @FXML
    private TreeTableColumn<NodeDiff, String> totalCntDiff;
    @FXML
    private TreeTableColumn<NodeDiff, String> baseParentCnt;
    @FXML
    private TreeTableColumn<NodeDiff, String> newParentCnt;
    @FXML
    private TreeTableColumn<NodeDiff, String> parentCntDiff;

    private TreeProfileDiff diff;

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(
            profileContext -> profileContext.profileProperty(),
            filterButton,
            quickFilterButton,
            quickFilterText);

        diff = new TreeProfileDiff();
    }

    @Override
    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        super.setProfileContexts(baseContext, newContext);

        initializeTable();
    }

    // Initialization Helper Methods

    private void initializeTable()
    {
        methodColumn.setCellValueFactory(
            data -> new ReadOnlyStringWrapper(
                data.getValue() == null ? null : data.getValue().getValue().getName()));
        methodColumn.setCellFactory(col -> new MethodNameTreeTableCell<>());

        cfgPctCol(baseSelfPct, NodeDiff::getBaseSelfPct, baseCtx(), getText(COLUMN_SELF_PCT));
        cfgPctCol(newSelfPct, NodeDiff::getNewSelfPct, newCtx(), getText(COLUMN_SELF_PCT));
        cfgPctDiffCol(selfPctDiff, NodeDiff::getSelfPctDiff, getText(COLUMN_SELF_PCT_DIFF));

        cfgPctCol(baseTotalPct, NodeDiff::getBaseSelfPct, baseCtx(), getText(COLUMN_TOTAL_PCT));
        cfgPctCol(newTotalPct, NodeDiff::getNewSelfPct, newCtx(), getText(COLUMN_TOTAL_PCT));
        cfgPctDiffCol(totalPctDiff, NodeDiff::getSelfPctDiff, getText(COLUMN_TOTAL_PCT_DIFF));

        cfgCntCol(baseSelfCnt, NodeDiff::getBaseSelfCnt, baseCtx(), getText(COLUMN_SELF_CNT));
        cfgCntCol(newSelfCnt, NodeDiff::getNewSelfCnt, newCtx(), getText(COLUMN_SELF_CNT));
        cfgCntDiffCol(selfCntDiff, NodeDiff::getSelfCntDiff, getText(COLUMN_SELF_CNT_DIFF));

        cfgCntCol(baseTotalCnt, NodeDiff::getBaseTotalCnt, baseCtx(), getText(COLUMN_TOTAL_CNT));
        cfgCntCol(newTotalCnt, NodeDiff::getNewTotalCnt, newCtx(), getText(COLUMN_TOTAL_CNT));
        cfgCntDiffCol(totalCntDiff, NodeDiff::getTotalCntDiff, getText(COLUMN_TOTAL_CNT_DIFF));

        cfgCntCol(baseParentCnt, NodeDiff::getBaseParentCnt, baseCtx(), getText(COLUMN_PARENT_CNT));
        cfgCntCol(newParentCnt, NodeDiff::getNewParentCnt, newCtx(), getText(COLUMN_PARENT_CNT));
        cfgCntDiffCol(parentCntDiff, NodeDiff::getParentCntDiff, getText(COLUMN_PARENT_CNT));
    }

    private void updateDiff(Profile profile, boolean base)
    {
        CopyAndFilterProfile task = new CopyAndFilterProfile(profile, getAdjustedProfileFilter());
        task.setOnSucceeded(state ->
        {
            // No need to worry about concurrency here, since this (the code for
            // onSucceeded()) will be executed on the FX thread. So even though
            // in the diff 2 tasks might execute concurrently during refresh(),
            // the resulting update calls in this if-statement won't execute
            // concurrently.
            if (base)
            {
                diff.updateForBase(task.getValue());
                diffTree.setRoot(new DiffTreeItem(diff));
                expandPartial(diffTree.getRoot(), 2);
            }
            else
            {
                diff.updateForNew(task.getValue());
                diffTree.setRoot(new DiffTreeItem(diff));
                expandPartial(diffTree.getRoot(), 2);
            }
        });
        appCtx().getExecutorService().execute(task);
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(expandAllButton, INFO_BUTTON_EXPANDALL);
        info(collapseAllButton, INFO_BUTTON_COLLAPSEALLALL);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(diffTree, INFO_TABLE_TREEDIFF);
    }

    @Override
    protected void initializeHandlers()
    {
        expandAllButton.setOnAction(event -> expandFully(diffTree.getRoot()));
        collapseAllButton.setOnAction(
            event -> diffTree.getRoot().getChildren().stream().forEach(TreeUtil::collapseFully));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        diff = new TreeProfileDiff();
        updateDiff(getBaseTarget(), true);
        updateDiff(getNewTarget(), false);
    }

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return new FilterType[]
        { STRING };
    }

    // Helper Classes

    private class DiffTreeItem extends TreeItem<NodeDiff>
    {
        public DiffTreeItem(TreeProfileDiff profileDiff)
        {
            super(null);
            for (NodeDiff child : profileDiff.getChildren())
            {
                getChildren().add(new DiffTreeItem(child));
            }
        }

        public DiffTreeItem(NodeDiff diff)
        {
            super(diff);
            for (NodeDiff child : diff.getChildren())
            {
                getChildren().add(new DiffTreeItem(child));
            }
        }
    }
}
