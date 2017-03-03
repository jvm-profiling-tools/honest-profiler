/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.DIFFENTRY;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExportDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.refreshTable;
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
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPORT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_FLATDIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeFlatProfileDiffCsv;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.FlatDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controller for Views which displays the contents of a {@link FlatDiff} {@link Aggregation}.
 */
public class FlatDiffViewController extends AbstractProfileDiffViewController<Flat, DiffEntry>
{
    @FXML
    private Button filterButton;
    @FXML
    private Button exportButton;
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
    private TableView<DiffEntry> flatDiffTable;
    @FXML
    private TableColumn<DiffEntry, String> method;
    @FXML
    private TableColumn<DiffEntry, Number> baseSelfCntPct;
    @FXML
    private TableColumn<DiffEntry, Number> newSelfCntPct;
    @FXML
    private TableColumn<DiffEntry, Number> selfCntPctDiff;
    @FXML
    private TableColumn<DiffEntry, Number> baseTotalCntPct;
    @FXML
    private TableColumn<DiffEntry, Number> newTotalCntPct;
    @FXML
    private TableColumn<DiffEntry, Number> totalCntPctDiff;
    @FXML
    private TableColumn<DiffEntry, Number> baseSelfCnt;
    @FXML
    private TableColumn<DiffEntry, Number> newSelfCnt;
    @FXML
    private TableColumn<DiffEntry, Number> selfCntDiff;
    @FXML
    private TableColumn<DiffEntry, Number> baseTotalCnt;
    @FXML
    private TableColumn<DiffEntry, Number> newTotalCnt;
    @FXML
    private TableColumn<DiffEntry, Number> totalCntDiff;
    @FXML
    private TableColumn<DiffEntry, Number> baseSelfTimePct;
    @FXML
    private TableColumn<DiffEntry, Number> newSelfTimePct;
    @FXML
    private TableColumn<DiffEntry, Number> selfTimePctDiff;
    @FXML
    private TableColumn<DiffEntry, Number> baseTotalTimePct;
    @FXML
    private TableColumn<DiffEntry, Number> newTotalTimePct;
    @FXML
    private TableColumn<DiffEntry, Number> totalTimePctDiff;
    @FXML
    private TableColumn<DiffEntry, Number> baseSelfTime;
    @FXML
    private TableColumn<DiffEntry, Number> newSelfTime;
    @FXML
    private TableColumn<DiffEntry, Number> selfTimeDiff;
    @FXML
    private TableColumn<DiffEntry, Number> baseTotalTime;
    @FXML
    private TableColumn<DiffEntry, Number> newTotalTime;
    @FXML
    private TableColumn<DiffEntry, Number> totalTimeDiff;

    @FXML
    private FilterDialogController<DiffEntry> filterController;

    private FlatDiff diff;

    // FXML Implementation

    @Override
    @FXML
    protected void initialize()
    {
        diff = new FlatDiff();

        super.initialize(DIFFENTRY);
        super.initialize(filterController, filterButton, quickFilterButton, quickFilterText);
        super.initialize(threadGroupingLabel, threadGrouping, frameGroupingLabel, frameGrouping);
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(exportButton, INFO_BUTTON_EXPORT);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(flatDiffTable, INFO_TABLE_FLATDIFF);
    }

    @Override
    protected void initializeHandlers()
    {
        exportButton.setOnAction(
            event -> showExportDialog(
                appCtx(),
                exportButton.getScene().getWindow(),
                "flat_diff_profile.csv",
                out -> writeFlatProfileDiffCsv(out, diff.getData(), ReportUtil.Mode.CSV)
            ));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        updateDiff(getBaseTarget(), getNewTarget());
    }

    /**
     * Initializes the {@link TableView} which displays the {@link FlatDiff} {@link Aggregation}.
     */
    @Override
    protected void initializeTable()
    {
        method.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getKey()));
        method.setCellFactory(col -> new MethodNameTableCell<DiffEntry>());

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
     * @param baseFlat the Base {@link Flat} to be compared
     * @param newFlat the New {@link Flat} to be compared
     */
    private void updateDiff(Flat baseFlat, Flat newFlat)
    {
        if (baseFlat != null && newFlat != null)
        {
            diff.set(baseFlat, newFlat);
            flatDiffTable.getItems().clear();
            flatDiffTable.getItems().addAll(diff.filter(getFilterSpecification()).getData());
            refreshTable(flatDiffTable);
            flatDiffTable.sort();
        }
    }
}
