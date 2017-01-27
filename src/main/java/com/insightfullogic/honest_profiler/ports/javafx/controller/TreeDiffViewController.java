package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.DIFFNODE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_TIME;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_TIME_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_TIME_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_TIME_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_TIME;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_TIME_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_TIME_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_TIME_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLLAPSEALLALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPANDALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_TREEDIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandPartial;

import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.DiffNodeTreeItem;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class TreeDiffViewController
    extends AbstractProfileDiffViewController<Tree<String>, DiffNode<String>>
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
    private TreeTableView<DiffNode<String>> diffTree;
    @FXML
    private TreeTableColumn<DiffNode<String>, String> methodColumn;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> baseSelfCntPct;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> newSelfCntPct;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> selfCntPctDiff;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> baseTotalCntPct;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> newTotalCntPct;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> totalCntPctDiff;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> baseSelfCnt;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> newSelfCnt;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> selfCntDiff;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> baseTotalCnt;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> newTotalCnt;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> totalCntDiff;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> baseSelfTimePct;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> newSelfTimePct;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> selfTimePctDiff;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> baseTotalTimePct;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> newTotalTimePct;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> totalTimePctDiff;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> baseSelfTime;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> newSelfTime;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> selfTimeDiff;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> baseTotalTime;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> newTotalTime;
    @FXML
    private TreeTableColumn<DiffNode<String>, Number> totalTimeDiff;

    private TreeDiff<String> diff;

    @Override
    @FXML
    protected void initialize()
    {
        diff = new TreeDiff<>();

        super.initialize(
            filterButton,
            quickFilterButton,
            quickFilterText,
            DIFFNODE);
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
                data.getValue() == null ? null : data.getValue().getValue().getKey()));
        methodColumn.setCellFactory(col -> new MethodNameTreeTableCell<>(appCtx()));

        cfgPctCol(baseSelfCntPct, "baseSelfCntPct", baseCtx(), getText(COLUMN_SELF_CNT_PCT));
        cfgPctCol(newSelfCntPct, "newSelfCntPct", newCtx(), getText(COLUMN_SELF_CNT_PCT));
        cfgPctDiffCol(selfCntPctDiff, "selfCntPctDiff", getText(COLUMN_SELF_CNT_PCT_DIFF));

        cfgPctCol(baseTotalCntPct, "baseTotalCntPct", baseCtx(), getText(COLUMN_TOTAL_CNT_PCT));
        cfgPctCol(newTotalCntPct, "newTotalCntPct", newCtx(), getText(COLUMN_TOTAL_CNT_PCT));
        cfgPctDiffCol(totalCntPctDiff, "totalCntPctDiff", getText(COLUMN_TOTAL_CNT_PCT_DIFF));

        cfgNrCol(baseSelfCnt, "baseSelfCnt", baseCtx(), getText(COLUMN_SELF_CNT));
        cfgNrCol(newSelfCnt, "newSelfCnt", newCtx(), getText(COLUMN_SELF_CNT));
        cfgNrDiffCol(selfCntDiff, "selfCntDiff", getText(COLUMN_SELF_CNT_DIFF));

        cfgNrCol(baseTotalCnt, "baseTotalCnt", baseCtx(), getText(COLUMN_TOTAL_CNT));
        cfgNrCol(newTotalCnt, "newTotalCnt", newCtx(), getText(COLUMN_TOTAL_CNT));
        cfgNrDiffCol(totalCntDiff, "totalCntDiff", getText(COLUMN_TOTAL_CNT_DIFF));

        cfgPctCol(baseSelfTimePct, "baseSelfTimePct", baseCtx(), getText(COLUMN_SELF_TIME_PCT));
        cfgPctCol(newSelfTimePct, "newSelfTimePct", newCtx(), getText(COLUMN_SELF_TIME_PCT));
        cfgPctDiffCol(selfTimePctDiff, "selfTimePctDiff", getText(COLUMN_SELF_TIME_PCT_DIFF));

        cfgPctCol(baseTotalTimePct, "baseTotalTimePct", baseCtx(), getText(COLUMN_TOTAL_TIME_PCT));
        cfgPctCol(newTotalTimePct, "newTotalTimePct", newCtx(), getText(COLUMN_TOTAL_TIME_PCT));
        cfgPctDiffCol(totalTimePctDiff, "totalTimePctDiff", getText(COLUMN_TOTAL_TIME_PCT_DIFF));

        cfgTimeCol(baseSelfTime, "baseSelfTime", baseCtx(), getText(COLUMN_SELF_TIME));
        cfgTimeCol(newSelfTime, "newSelfTime", newCtx(), getText(COLUMN_SELF_TIME));
        cfgTimeDiffCol(selfTimeDiff, "selfTimeDiff", getText(COLUMN_SELF_TIME_DIFF));

        cfgTimeCol(baseTotalTime, "baseTotalTime", baseCtx(), getText(COLUMN_TOTAL_TIME));
        cfgTimeCol(newTotalTime, "newTotalTime", newCtx(), getText(COLUMN_TOTAL_TIME));
        cfgTimeDiffCol(totalTimeDiff, "totalTimeDiff", getText(COLUMN_TOTAL_TIME_DIFF));
    }

    private void updateDiff(Tree<String> profile, boolean base)
    {
        if (profile != null)
        {
            diff.set(profile, base);
            diffTree.setRoot(new DiffNodeTreeItem(diff.filter(getFilterSpecification())));
            expandPartial(diffTree.getRoot(), 2);
        }
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
        diff = new TreeDiff<>();
        updateDiff(getBaseTarget(), true);
        updateDiff(getNewTarget(), false);
    }
}
