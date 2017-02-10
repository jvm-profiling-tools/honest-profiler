package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.DIFFENTRY;
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

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.DiffNodeTreeItem;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

/**
 * Controller for Views which displays the contents of a {@link TreeDiff} {@link Aggregation}.
 */
public class TreeDiffViewController extends AbstractProfileDiffViewController<Tree, DiffNode>
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
    private Label threadGroupingLabel;
    @FXML
    private ChoiceBox<ThreadGrouping> threadGrouping;
    @FXML
    private Label frameGroupingLabel;
    @FXML
    private ChoiceBox<FrameGrouping> frameGrouping;

    @FXML
    private TreeTableView<DiffNode> treeDiffTable;
    @FXML
    private TreeTableColumn<DiffNode, String> methodColumn;
    @FXML
    private TreeTableColumn<DiffNode, Number> baseSelfCntPct;
    @FXML
    private TreeTableColumn<DiffNode, Number> newSelfCntPct;
    @FXML
    private TreeTableColumn<DiffNode, Number> selfCntPctDiff;
    @FXML
    private TreeTableColumn<DiffNode, Number> baseTotalCntPct;
    @FXML
    private TreeTableColumn<DiffNode, Number> newTotalCntPct;
    @FXML
    private TreeTableColumn<DiffNode, Number> totalCntPctDiff;
    @FXML
    private TreeTableColumn<DiffNode, Number> baseSelfCnt;
    @FXML
    private TreeTableColumn<DiffNode, Number> newSelfCnt;
    @FXML
    private TreeTableColumn<DiffNode, Number> selfCntDiff;
    @FXML
    private TreeTableColumn<DiffNode, Number> baseTotalCnt;
    @FXML
    private TreeTableColumn<DiffNode, Number> newTotalCnt;
    @FXML
    private TreeTableColumn<DiffNode, Number> totalCntDiff;
    @FXML
    private TreeTableColumn<DiffNode, Number> baseSelfTimePct;
    @FXML
    private TreeTableColumn<DiffNode, Number> newSelfTimePct;
    @FXML
    private TreeTableColumn<DiffNode, Number> selfTimePctDiff;
    @FXML
    private TreeTableColumn<DiffNode, Number> baseTotalTimePct;
    @FXML
    private TreeTableColumn<DiffNode, Number> newTotalTimePct;
    @FXML
    private TreeTableColumn<DiffNode, Number> totalTimePctDiff;
    @FXML
    private TreeTableColumn<DiffNode, Number> baseSelfTime;
    @FXML
    private TreeTableColumn<DiffNode, Number> newSelfTime;
    @FXML
    private TreeTableColumn<DiffNode, Number> selfTimeDiff;
    @FXML
    private TreeTableColumn<DiffNode, Number> baseTotalTime;
    @FXML
    private TreeTableColumn<DiffNode, Number> newTotalTime;
    @FXML
    private TreeTableColumn<DiffNode, Number> totalTimeDiff;

    @FXML
    private FilterDialogController<DiffNode> filterController;

    private TreeDiff diff;

    // FXML Implementation

    @Override
    @FXML
    protected void initialize()
    {
        diff = new TreeDiff();

        super.initialize(DIFFENTRY);
        super.initialize(filterController, filterButton, quickFilterButton, quickFilterText);
        super.initialize(threadGroupingLabel, threadGrouping, frameGroupingLabel, frameGrouping);
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
        info(treeDiffTable, INFO_TABLE_TREEDIFF);
    }

    @Override
    protected void initializeHandlers()
    {
        expandAllButton.setOnAction(event -> expandFully(treeDiffTable.getRoot()));

        collapseAllButton.setOnAction(
            event -> treeDiffTable.getRoot().getChildren().stream()
                .forEach(TreeUtil::collapseFully));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        diff = new TreeDiff();
        updateDiff(getBaseTarget(), getNewTarget());
    }

    /**
     * Initializes the {@link TreeTableView} which displays the {@link TreeDiff} {@link Aggregation}.
     */
    @Override
    protected void initializeTable()
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

    /**
     * Helper method for {@link #refresh()}.
     * <p>
     *
     * @param baseTree the Base {@link Tree} to be compared
     * @param newTree the New {@link Tree} to be compared
     */
    private void updateDiff(Tree baseTree, Tree newTree)
    {
        if (baseTree != null && newTree != null)
        {
            diff.set(baseTree, newTree);
            treeDiffTable.setRoot(new DiffNodeTreeItem(diff.filter(getFilterSpecification())));
            expandPartial(treeDiffTable.getRoot(), 2);
            treeDiffTable.sort();
        }
    }
}
