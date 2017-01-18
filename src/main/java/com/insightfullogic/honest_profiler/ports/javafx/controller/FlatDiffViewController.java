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

import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.STRING;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExportDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_PROFILE_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_PROFILE_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_PCT_DIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPORT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_FLATDIFF;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeFlatProfileDiffCsv;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedDiffEntry;
import com.insightfullogic.honest_profiler.core.aggregation.result.FlatDiffAggregation;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FlatDiffViewController extends ProfileDiffViewController<AggregationProfile>
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
    private TableView<AggregatedDiffEntry<String>> diffTable;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, String> method;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> baseSelfPct;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> newSelfPct;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> selfPctDiff;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> baseTotalPct;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> newTotalPct;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> totalPctDiff;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> baseSelfCnt;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> newSelfCnt;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> selfCntDiff;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> baseTotalCnt;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> newTotalCnt;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> totalCntDiff;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> baseProfileCnt;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> newProfileCnt;
    @FXML
    private TableColumn<AggregatedDiffEntry<String>, Number> profileCntDiff;

    private FlatDiffAggregation<String> diff;

    @Override
    @FXML
    protected void initialize()
    {
        diff = new FlatDiffAggregation<>();

        super.initialize(
            profileContext -> profileContext.profileProperty(),
            filterButton,
            quickFilterButton,
            quickFilterText);
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
        method.setCellFactory(col -> new MethodNameTableCell<AggregatedDiffEntry<String>>());

        cfgPctCol(baseSelfPct, "baseSelfCntPct", baseCtx(), getText(COLUMN_SELF_PCT));
        cfgPctCol(newSelfPct, "newSelfCntPct", newCtx(), getText(COLUMN_SELF_PCT));
        cfgPctDiffCol(selfPctDiff, "selfCntPctDiff", getText(COLUMN_SELF_PCT_DIFF));

        cfgPctCol(baseTotalPct, "baseTotalCntPct", baseCtx(), getText(COLUMN_TOTAL_PCT));
        cfgPctCol(newTotalPct, "newTotalCntPct", newCtx(), getText(COLUMN_TOTAL_PCT));
        cfgPctDiffCol(totalPctDiff, "totalCntPctDiff", getText(COLUMN_TOTAL_PCT_DIFF));

        cfgCntCol(baseSelfCnt, "baseSelfCnt", baseCtx(), getText(COLUMN_SELF_CNT));
        cfgCntCol(newSelfCnt, "newSelfCnt", newCtx(), getText(COLUMN_SELF_CNT));
        cfgCntDiffCol(selfCntDiff, "selfCntDiff", getText(COLUMN_SELF_CNT_DIFF));

        cfgCntCol(baseTotalCnt, "baseTotalCnt", baseCtx(), getText(COLUMN_TOTAL_CNT));
        cfgCntCol(newTotalCnt, "newTotalCnt", newCtx(), getText(COLUMN_TOTAL_CNT));
        cfgCntDiffCol(totalCntDiff, "totalCntDiff", getText(COLUMN_TOTAL_CNT_DIFF));

        cfgCntCol(baseProfileCnt, "baseRefCnt", baseCtx(), getText(COLUMN_PROFILE_CNT));
        cfgCntCol(newProfileCnt, "newRefCnt", newCtx(), getText(COLUMN_PROFILE_CNT));
        cfgCntDiffCol(profileCntDiff, "refCntDiff", getText(COLUMN_PROFILE_CNT_DIFF));
    }

    private void updateDiff(AggregationProfile profile, boolean base)
    {
        // CopyAndFilterProfileTask task = new CopyAndFilterProfileTask(
        // profile,
        // getAdjustedProfileFilter());
        // task.setOnSucceeded(state ->
        // {
        // // No need to worry about concurrency here, since this (the code for
        // // onSucceeded()) will be executed on the FX thread. So even though
        // // in the diff 2 tasks might execute concurrently during refresh(),
        // // the resulting update calls in this if-statement won't execute
        // // concurrently.
        // if (base)
        // {
        // diff.addBase(profile.getFlatAggregation());
        // }
        // else
        // {
        // diff.addNew(profile.getFlatAggregation());
        // }
        //
        // refreshTable(diffTable);
        // });
        // appCtx().execute(task);

        if (profile != null)
        {
            if (base)
            {
                diff.setBase(profile.getFlatAggregation());
            }
            else
            {
                diff.setNew(profile.getFlatAggregation());
            }

            diffTable.getItems().clear();
            diffTable.getItems().addAll(diff.getData());
            // refreshTable(diffTable);
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
        // diff.clear();
        updateDiff(getBaseTarget(), true);
        updateDiff(getNewTarget(), false);
    }

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return new FilterType[]
        { STRING };
    }
}
