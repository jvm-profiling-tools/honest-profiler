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
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDECNTALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDEDIFFALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDENONDIFFALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDEPCTALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDESELFALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDETIMEALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDETOTALALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWCNTALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWDIFFALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWNONDIFFALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWPCTALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWSELFALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWTIMEALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWTOTALALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLLAPSEALLALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLUMNVIEW;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPANDALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_TREEDIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandPartial;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffNode;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.TreeDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.DiffNodeTreeItem;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumnBase;
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
    private Button columnViewButton;
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

    private Set<TableColumnBase<?, ?>> selfColumns;
    private Set<TableColumnBase<?, ?>> totalColumns;
    private Set<TableColumnBase<?, ?>> cntColumns;
    private Set<TableColumnBase<?, ?>> pctColumns;
    private Set<TableColumnBase<?, ?>> timeColumns;
    private Set<TableColumnBase<?, ?>> diffColumns;
    private Set<TableColumnBase<?, ?>> nonDiffColumns;

    // FXML Implementation

    @Override
    @FXML
    protected void initialize()
    {
        diff = new TreeDiff();

        super.initialize(DIFFENTRY);
        super.initializeFiltering(
            filterController,
            filterButton,
            quickFilterButton,
            quickFilterText);
        super.initializeGrouping(
            threadGroupingLabel,
            threadGrouping,
            frameGroupingLabel,
            frameGrouping);
        super.initializeColumnView(columnViewButton);

        createColumnSets();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);

        addColumnGroupMenuItem(
            appCtx().textFor(CTXMENU_COLVW_SHOWNONDIFFALL),
            true,
            nonDiffColumns);
        addColumnGroupMenuItem(
            appCtx().textFor(CTXMENU_COLVW_HIDENONDIFFALL),
            false,
            nonDiffColumns);
        addColumnGroupMenuItem(
            appCtx().textFor(CTXMENU_COLVW_SHOWDIFFALL),
            true,
            diffColumns);
        addColumnGroupMenuItem(
            appCtx().textFor(CTXMENU_COLVW_HIDEDIFFALL),
            false,
            diffColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_SHOWCNTALL), true, cntColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_HIDECNTALL), false, cntColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_SHOWPCTALL), true, pctColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_HIDEPCTALL), false, pctColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_SHOWTIMEALL), true, timeColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_HIDETIMEALL), false, timeColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_SHOWSELFALL), true, selfColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_HIDESELFALL), false, selfColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_SHOWTOTALALL), true, totalColumns);
        addColumnGroupMenuItem(appCtx().textFor(CTXMENU_COLVW_HIDETOTALALL), false, totalColumns);
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(columnViewButton, INFO_BUTTON_COLUMNVIEW);
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

        // The column configuration methods should (for now) be called in the same order as the columns are declared in
        // the FXML.
        // The only reason is the column view context menu, which collects its menu items in the order of these
        // declarations. If the order is out of sync, the column view context menu will not have its items in the
        // correct order.
        // This will probably, eventually, be remedied by reordering but for now this comment will have to do.
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

    /**
     * Defines the column sets for collective showing or hiding.
     */
    private void createColumnSets()
    {
        selfColumns = new HashSet<>(
            asList(
                baseSelfCnt,
                baseSelfCntPct,
                baseSelfTime,
                baseSelfTimePct,
                newSelfCnt,
                newSelfCntPct,
                newSelfTime,
                newSelfTimePct,
                selfCntDiff,
                selfCntPctDiff,
                selfTimeDiff,
                selfTimePctDiff));

        totalColumns = new HashSet<>(
            asList(
                baseTotalCnt,
                baseTotalCntPct,
                baseTotalTime,
                baseTotalTimePct,
                newTotalCnt,
                newTotalCntPct,
                newTotalTime,
                newTotalTimePct,
                totalCntDiff,
                totalCntPctDiff,
                totalTimeDiff,
                totalTimePctDiff));

        cntColumns = new HashSet<>(
            asList(baseSelfCnt, newSelfCnt, selfCntDiff, baseTotalCnt, newTotalCnt, totalCntDiff));

        pctColumns = new HashSet<>(
            asList(
                baseSelfCntPct,
                newSelfCntPct,
                selfCntPctDiff,
                baseTotalCntPct,
                newTotalCntPct,
                totalCntPctDiff,
                baseSelfTimePct,
                newSelfTimePct,
                selfTimePctDiff,
                baseTotalTimePct,
                newTotalTimePct,
                totalTimePctDiff));

        timeColumns = new HashSet<>(
            asList(
                baseSelfTime,
                newSelfTime,
                selfTimeDiff,
                baseTotalTime,
                newTotalTime,
                totalTimeDiff,
                baseSelfTimePct,
                newSelfTimePct,
                selfTimePctDiff,
                baseTotalTimePct,
                newTotalTimePct,
                totalTimePctDiff));

        nonDiffColumns = new HashSet<>(
            asList(
                baseSelfCnt,
                baseSelfCntPct,
                baseSelfTime,
                baseSelfTimePct,
                baseTotalCnt,
                baseTotalCntPct,
                baseTotalTime,
                baseTotalTimePct,
                newSelfCnt,
                newSelfCntPct,
                newSelfTime,
                newSelfTimePct,
                newTotalCnt,
                newTotalCntPct,
                newTotalTime,
                newTotalTimePct));

        diffColumns = new HashSet<>(
            asList(
                selfCntDiff,
                selfCntPctDiff,
                selfTimeDiff,
                selfTimePctDiff,
                totalCntDiff,
                totalCntPctDiff,
                totalTimeDiff,
                totalTimePctDiff
                ));
    }
}
