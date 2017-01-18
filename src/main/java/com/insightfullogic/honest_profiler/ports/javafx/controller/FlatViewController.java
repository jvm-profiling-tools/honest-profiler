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

import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LIVE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.STRING;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.TIME_SHARE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExportDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.refreshTable;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_PROFILE_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPORT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeFlatProfileCsv;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedEntry;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.GraphicalShareTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FlatViewController extends ProfileViewController<AggregationProfile>
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
    private TableView<AggregatedEntry<String>> flatProfileView;
    @FXML
    private TableColumn<AggregatedEntry<String>, String> method;
    @FXML
    private TableColumn<AggregatedEntry<String>, Double> selfTimeGraphical;
    @FXML
    private TableColumn<AggregatedEntry<String>, Number> selfPct;
    @FXML
    private TableColumn<AggregatedEntry<String>, Number> totalPct;
    @FXML
    private TableColumn<AggregatedEntry<String>, Number> selfCnt;
    @FXML
    private TableColumn<AggregatedEntry<String>, Number> totalCnt;
    @FXML
    private TableColumn<AggregatedEntry<String>, Number> profileCnt;

    private ObservableList<AggregatedEntry<String>> flatProfile;

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(
            profileContext -> profileContext.profileProperty(),
            filterButton,
            quickFilterButton,
            quickFilterText);

        flatProfile = flatProfileView.getItems();

        initializeTable();
    }

    @Override
    public void setProfileContext(ProfileContext profileContext)
    {
        if (profileContext.getMode() == LIVE)
        {
            flatProfileView.getColumns().forEach(column -> column.setSortable(false));
        }
        super.setProfileContext(profileContext);
    }

    // Initialization Helper Methods

    private void initializeTable()
    {
        method.setCellValueFactory(new PropertyValueFactory<>("key"));
        method.setCellFactory(col -> new MethodNameTableCell<AggregatedEntry<String>>());

        selfTimeGraphical.setCellValueFactory(new PropertyValueFactory<>("selfCntPct"));
        selfTimeGraphical.setCellFactory(col -> new GraphicalShareTableCell<>(col.getPrefWidth()));

        cfgPctCol(selfPct, "selfCntPct", prfCtx(), COLUMN_SELF_PCT);
        cfgPctCol(totalPct, "totalCntPct", prfCtx(), COLUMN_TOTAL_PCT);
        cfgCntCol(selfCnt, "selfCnt", prfCtx(), COLUMN_SELF_CNT);
        cfgCntCol(totalCnt, "totalCnt", prfCtx(), COLUMN_TOTAL_CNT);
        cfgCntCol(profileCnt, "refCnt", prfCtx(), COLUMN_PROFILE_CNT);
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(exportButton, INFO_BUTTON_EXPORT);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(flatProfileView, INFO_TABLE_FLAT);
    }

    @Override
    protected void initializeHandlers()
    {
        exportButton.setOnAction(
            event -> showExportDialog(
                appCtx(),
                exportButton.getScene().getWindow(),
                "flat_profile.csv",
                out -> writeFlatProfileCsv(out, flatProfile, ReportUtil.Mode.CSV)
            ));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        // CopyAndFilterProfileTask task = new CopyAndFilterProfileTask(
        // getTarget(),
        // getAdjustedProfileFilter());
        // task.setOnSucceeded(state ->
        // {
        // flatProfile.clear();
        // task.getValue().flatByMethodProfile().forEach(flatProfile::add);
        // refreshTable(flatProfileView);
        // });
        // appCtx().execute(task);
        flatProfile.clear();
        flatProfile.addAll(getTarget().getFlatAggregation().getData());
        flatProfileView.refresh();
        refreshTable(flatProfileView);
    }

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return new FilterType[]
        { STRING, TIME_SHARE };
    }
}
