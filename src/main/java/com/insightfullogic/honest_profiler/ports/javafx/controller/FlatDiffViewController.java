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

import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.FlatDiff;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FlatDiffViewController
    extends AbstractProfileDiffViewController<Flat<String>, DiffEntry<String>>
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
    private TableView<DiffEntry<String>> diffTable;
    @FXML
    private TableColumn<DiffEntry<String>, String> method;
    @FXML
    private TableColumn<DiffEntry<String>, Number> baseSelfCntPct;
    @FXML
    private TableColumn<DiffEntry<String>, Number> newSelfCntPct;
    @FXML
    private TableColumn<DiffEntry<String>, Number> selfCntPctDiff;
    @FXML
    private TableColumn<DiffEntry<String>, Number> baseTotalCntPct;
    @FXML
    private TableColumn<DiffEntry<String>, Number> newTotalCntPct;
    @FXML
    private TableColumn<DiffEntry<String>, Number> totalCntPctDiff;
    @FXML
    private TableColumn<DiffEntry<String>, Number> baseSelfCnt;
    @FXML
    private TableColumn<DiffEntry<String>, Number> newSelfCnt;
    @FXML
    private TableColumn<DiffEntry<String>, Number> selfCntDiff;
    @FXML
    private TableColumn<DiffEntry<String>, Number> baseTotalCnt;
    @FXML
    private TableColumn<DiffEntry<String>, Number> newTotalCnt;
    @FXML
    private TableColumn<DiffEntry<String>, Number> totalCntDiff;
    @FXML
    private TableColumn<DiffEntry<String>, Number> baseSelfTimePct;
    @FXML
    private TableColumn<DiffEntry<String>, Number> newSelfTimePct;
    @FXML
    private TableColumn<DiffEntry<String>, Number> selfTimePctDiff;
    @FXML
    private TableColumn<DiffEntry<String>, Number> baseTotalTimePct;
    @FXML
    private TableColumn<DiffEntry<String>, Number> newTotalTimePct;
    @FXML
    private TableColumn<DiffEntry<String>, Number> totalTimePctDiff;
    @FXML
    private TableColumn<DiffEntry<String>, Number> baseSelfTime;
    @FXML
    private TableColumn<DiffEntry<String>, Number> newSelfTime;
    @FXML
    private TableColumn<DiffEntry<String>, Number> selfTimeDiff;
    @FXML
    private TableColumn<DiffEntry<String>, Number> baseTotalTime;
    @FXML
    private TableColumn<DiffEntry<String>, Number> newTotalTime;
    @FXML
    private TableColumn<DiffEntry<String>, Number> totalTimeDiff;

    private FlatDiff<String> diff;

    @Override
    @FXML
    protected void initialize()
    {
        diff = new FlatDiff<>();

        super.initialize(
            filterButton,
            quickFilterButton,
            quickFilterText,
            DIFFENTRY);
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
        method.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getKey()));
        method.setCellFactory(col -> new MethodNameTableCell<DiffEntry<String>>());

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

    private void updateDiff(Flat<String> flat, boolean base)
    {
        if (flat != null)
        {
            diff.set(flat, base);
            diffTable.getItems().clear();
            diffTable.getItems().addAll(diff.filter(getFilterSpecification()).getData());
        }
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(exportButton, INFO_BUTTON_EXPORT);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(diffTable, INFO_TABLE_FLATDIFF);
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
        updateDiff(getBaseTarget(), true);
        updateDiff(getNewTarget(), false);
    }
}
