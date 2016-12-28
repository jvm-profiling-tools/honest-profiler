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
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.TIME_SHARE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExportDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.refreshTable;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.doubleDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.intDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeFlatProfileDiffCsv;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.EXPORT_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_ACTIVE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.diff.FlatEntryDiff;
import com.insightfullogic.honest_profiler.ports.javafx.model.diff.FlatProfileDiff;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.CountTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.PercentageTableCell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class FlatDiffViewController extends AbstractController
{
    @FXML
    private Button filterButton;
    @FXML
    private Button exportButton;
    @FXML
    private Label baseSourceLabel;
    @FXML
    private Label newSourceLabel;
    @FXML
    private TableView<FlatEntryDiff> diffTable;
    @FXML
    private TableColumn<FlatEntryDiff, String> method;
    @FXML
    private TableColumn<FlatEntryDiff, Number> baseSelfTime;
    @FXML
    private TableColumn<FlatEntryDiff, Number> newSelfTime;
    @FXML
    private TableColumn<FlatEntryDiff, Number> selfTimeDiff;
    @FXML
    private TableColumn<FlatEntryDiff, Number> baseTotalTime;
    @FXML
    private TableColumn<FlatEntryDiff, Number> newTotalTime;
    @FXML
    private TableColumn<FlatEntryDiff, Number> totalTimeDiff;
    @FXML
    private TableColumn<FlatEntryDiff, Number> baseSelfCount;
    @FXML
    private TableColumn<FlatEntryDiff, Number> newSelfCount;
    @FXML
    private TableColumn<FlatEntryDiff, Number> selfCountDiff;
    @FXML
    private TableColumn<FlatEntryDiff, Number> baseTotalCount;
    @FXML
    private TableColumn<FlatEntryDiff, Number> newTotalCount;
    @FXML
    private TableColumn<FlatEntryDiff, Number> totalCountDiff;
    @FXML
    private TableColumn<FlatEntryDiff, Number> baseTraceCount;
    @FXML
    private TableColumn<FlatEntryDiff, Number> newTraceCount;
    @FXML
    private TableColumn<FlatEntryDiff, Number> traceCountDiff;

    private ProfileContext baseProfileContext;
    private ProfileContext newProfileContext;

    private FilterDialogController filterDialogController;
    private ObjectProperty<FilterSpecification> filterSpec;

    private FlatProfileDiff diff;
    private ProfileFilter currentFilter;

    @FXML
    private void initialize()
    {
        info(filterButton, "Specify filters restricting the visible entries");
        info(exportButton, "Export the visible entries to a CSV file");

        diff = new FlatProfileDiff(diffTable.getItems());

        currentFilter = new ProfileFilter();

        filterDialogController = (FilterDialogController) DialogUtil
            .<FilterSpecification>createDialog(FILTER, "Specify Filters", false);
        filterDialogController.addAllowedFilterTypes(STRING, TIME_SHARE);

        exportButton.setGraphic(viewFor(EXPORT_16));
        exportButton.setTooltip(new Tooltip("Export the current view to a file"));
        exportButton.setOnAction(event ->

        showExportDialog(
            exportButton.getScene().getWindow(),
            "flat_diff_profile.csv",
            out -> writeFlatProfileDiffCsv(out, diff, ReportUtil.Mode.CSV)
        ));

        filterSpec = new SimpleObjectProperty<>(null);
        filterSpec.addListener((property, oldValue, newValue) ->
        {
            filterButton.setGraphic(
                newValue == null || !newValue.isFiltering() ? viewFor(FUNNEL_16)
                    : viewFor(FUNNEL_ACTIVE_16));
            currentFilter = new ProfileFilter(newValue.getFilters());

            diff.clear();
            updateDiff(baseProfileContext.getProfile(), true);
            updateDiff(newProfileContext.getProfile(), false);
        });

        filterButton.setGraphic(viewFor(FUNNEL_16));
        filterButton.setOnAction(
            event -> filterSpec.set(filterDialogController.showAndWait().get()));

        method
            .setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFullName()));
        method.setCellFactory(col -> new MethodNameTableCell<FlatEntryDiff>());

        configureTimeShareColumn(baseSelfTime, "baseSelfTimeShare", null);
        configureTimeShareColumn(newSelfTime, "newSelfTimeShare", null);

        configureTimeShareColumn(baseTotalTime, "baseTotalTimeShare", null);
        configureTimeShareColumn(newTotalTime, "newTotalTimeShare", null);

        configureTimeShareColumn(selfTimeDiff, "pctSelfChange", doubleDiffStyler);
        configureTimeShareColumn(totalTimeDiff, "pctTotalChange", doubleDiffStyler);

        configureCountColumn(baseSelfCount, "baseSelfCount", null);
        configureCountColumn(newSelfCount, "newSelfCount", null);

        configureCountColumn(baseTotalCount, "baseTotalCount", null);
        configureCountColumn(newTotalCount, "newTotalCount", null);

        configureCountColumn(baseTraceCount, "baseTraceCount", null);
        configureCountColumn(newTraceCount, "newTraceCount", null);

        configureCountDiffColumn(
            selfCountDiff,
            data -> new ReadOnlyIntegerWrapper(
                data.getValue().getNewSelfCount() - data.getValue().getBaseSelfCount()),
            intDiffStyler);
        configureCountDiffColumn(
            totalCountDiff,
            data -> new ReadOnlyIntegerWrapper(
                data.getValue().getNewTotalCount() - data.getValue().getBaseTotalCount()),
            intDiffStyler);
        configureCountDiffColumn(
            traceCountDiff,
            data -> new ReadOnlyIntegerWrapper(
                data.getValue().getNewTraceCount() - data.getValue().getBaseTraceCount()),
            intDiffStyler);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        filterDialogController.setApplicationContext(appCtx());
    }

    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        baseProfileContext = baseContext;
        newProfileContext = newContext;

        baseSourceLabel.setText(baseContext.getName());
        newSourceLabel.setText(newContext.getName());

        updateDiff(baseContext.getProfile(), true);
        updateDiff(newContext.getProfile(), false);

        baseProfileContext.profileProperty()
            .addListener((property, oldValue, newValue) -> updateDiff(newValue, true));

        newProfileContext.profileProperty()
            .addListener((property, oldValue, newValue) -> updateDiff(newValue, false));
    }

    private void updateDiff(Profile profile, boolean base)
    {
        Profile copy = profile.copy();
        currentFilter.accept(copy);
        if (base)
        {
            diff.updateBase(copy.getFlatByFrameProfile());
        }
        else
        {
            diff.updateNew(copy.getFlatByFrameProfile());
        }

        refreshTable(diffTable);
    }

    private void configureTimeShareColumn(TableColumn<FlatEntryDiff, Number> column,
        String propertyName, Function<Number, String> styler)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new PercentageTableCell<FlatEntryDiff>(styler));
    }

    private void configureCountColumn(TableColumn<FlatEntryDiff, Number> column,
        String propertyName, Function<Number, String> styler)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new CountTableCell<FlatEntryDiff>(styler));
    }

    private void configureCountDiffColumn(TableColumn<FlatEntryDiff, Number> column,
        Callback<CellDataFeatures<FlatEntryDiff, Number>, ObservableValue<Number>> callback,
        Function<Number, String> styler)
    {
        column.setCellValueFactory(callback);
        column.setCellFactory(col -> new CountTableCell<FlatEntryDiff>(styler));
    }
}
