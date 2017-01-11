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

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.model.task.CopyAndFilterProfile;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.Rendering;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.GraphicalShareTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FlatViewController extends ProfileViewController<Profile>
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
    private TableView<FlatProfileEntry> flatProfileView;
    @FXML
    private TableColumn<FlatProfileEntry, String> method;
    @FXML
    private TableColumn<FlatProfileEntry, Double> selfTimeGraphical;
    @FXML
    private TableColumn<FlatProfileEntry, Number> selfPct;
    @FXML
    private TableColumn<FlatProfileEntry, Number> totalPct;
    @FXML
    private TableColumn<FlatProfileEntry, Number> selfCnt;
    @FXML
    private TableColumn<FlatProfileEntry, Number> totalCnt;
    @FXML
    private TableColumn<FlatProfileEntry, Number> profileCnt;

    private ObservableList<FlatProfileEntry> flatProfile;

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
        method.setCellValueFactory(Rendering::method);
        method.setCellFactory(col -> new MethodNameTableCell<FlatProfileEntry>());

        selfTimeGraphical.setCellValueFactory(new PropertyValueFactory<>("selfTimeShare"));
        selfTimeGraphical.setCellFactory(col -> new GraphicalShareTableCell(col.getPrefWidth()));

        cfgPctCol(selfPct, "selfTimeShare", prfCtx(), COLUMN_SELF_PCT);
        cfgPctCol(totalPct, "totalTimeShare", prfCtx(), COLUMN_TOTAL_PCT);
        cfgCntCol(selfCnt, "selfCount", prfCtx(), COLUMN_SELF_CNT);
        cfgCntCol(totalCnt, "totalCount", prfCtx(), COLUMN_TOTAL_CNT);
        cfgCntCol(profileCnt, "traceCount", prfCtx(), COLUMN_PROFILE_CNT);
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
        CopyAndFilterProfile task = new CopyAndFilterProfile(
            getTarget(),
            getAdjustedProfileFilter());
        task.setOnSucceeded(state ->
        {
            flatProfile.clear();
            task.getValue().flatByMethodProfile().forEach(flatProfile::add);
            refreshTable(flatProfileView);
        });
        appCtx().getExecutorService().execute(task);
    }

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return new FilterType[]
        { STRING, TIME_SHARE };
    }
}
