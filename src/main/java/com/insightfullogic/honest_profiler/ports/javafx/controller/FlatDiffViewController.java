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
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.addProfileNr;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.createColoredLabelContainer;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.refreshTable;
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
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.doubleDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.intDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeFlatProfileDiffCsv;
import static javafx.geometry.Pos.CENTER;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.diff.FlatEntryDiff;
import com.insightfullogic.honest_profiler.ports.javafx.model.diff.FlatProfileDiff;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.model.task.CopyAndFilterProfile;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.CountTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.PercentageTableCell;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class FlatDiffViewController extends ProfileDiffViewController<Profile>
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

    private FlatProfileDiff diff;

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(
            profileContext -> profileContext.profileProperty(),
            filterButton,
            quickFilterButton,
            quickFilterText);

        diff = new FlatProfileDiff(diffTable.getItems());

        exportButton.setOnAction(event -> showExportDialog(
            exportButton.getScene().getWindow(),
            "flat_diff_profile.csv",
            out -> writeFlatProfileDiffCsv(out, diff, ReportUtil.Mode.CSV)
        ));

        method
            .setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFullName()));
        method.setCellFactory(col -> new MethodNameTableCell<FlatEntryDiff>());

    }

    @Override
    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        super.setProfileContexts(baseContext, newContext);

        configurePercentColumn(
            baseSelfTime,
            "baseSelfTimeShare",
            baseContext(),
            getText(COLUMN_SELF_PCT));
        configurePercentColumn(
            newSelfTime,
            "newSelfTimeShare",
            newContext(),
            getText(COLUMN_SELF_PCT));
        configurePercentColumn(
            selfTimeDiff,
            "pctSelfChange",
            doubleDiffStyler,
            getText(COLUMN_SELF_PCT_DIFF));

        configurePercentColumn(
            baseTotalTime,
            "baseTotalTimeShare",
            baseContext(),
            getText(COLUMN_TOTAL_PCT));
        configurePercentColumn(
            newTotalTime,
            "newTotalTimeShare",
            newContext(),
            getText(COLUMN_TOTAL_PCT));
        configurePercentColumn(
            totalTimeDiff,
            "pctTotalChange",
            doubleDiffStyler,
            getText(COLUMN_TOTAL_PCT_DIFF));

        configureCountColumn(
            baseSelfCount,
            "baseSelfCount",
            baseContext(),
            getText(COLUMN_SELF_CNT));
        configureCountColumn(newSelfCount, "newSelfCount", newContext(), getText(COLUMN_SELF_CNT));
        configureCountDiffColumn(
            selfCountDiff,
            data -> new ReadOnlyIntegerWrapper(
                data.getValue().getNewSelfCount() - data.getValue().getBaseSelfCount()),
            intDiffStyler,
            getText(COLUMN_SELF_CNT_DIFF));

        configureCountColumn(
            baseTotalCount,
            "baseTotalCount",
            baseContext(),
            getText(COLUMN_TOTAL_CNT));
        configureCountColumn(
            newTotalCount,
            "newTotalCount",
            newContext(),
            getText(COLUMN_TOTAL_CNT));
        configureCountDiffColumn(
            totalCountDiff,
            data -> new ReadOnlyIntegerWrapper(
                data.getValue().getNewTotalCount() - data.getValue().getBaseTotalCount()),
            intDiffStyler,
            getText(COLUMN_TOTAL_CNT_DIFF));

        configureCountColumn(
            baseTraceCount,
            "baseTraceCount",
            baseContext(),
            getText(COLUMN_PROFILE_CNT));
        configureCountColumn(
            newTraceCount,
            "newTraceCount",
            newContext(),
            getText(COLUMN_PROFILE_CNT));
        configureCountDiffColumn(
            traceCountDiff,
            data -> new ReadOnlyIntegerWrapper(
                data.getValue().getNewTraceCount() - data.getValue().getBaseTraceCount()),
            intDiffStyler,
            getText(COLUMN_PROFILE_CNT_DIFF));
    }

    @Override
    protected void refresh()
    {
        diff.clear();
        updateDiff(getBaseTarget(), true);
        updateDiff(getNewTarget(), false);
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
                diff.updateBase(task.getValue().getFlatByMethodProfile());
            }
            else
            {
                diff.updateNew(task.getValue().getFlatByMethodProfile());
            }

            refreshTable(diffTable);
        });
        appCtx().getExecutorService().execute(task);
    }

    private void configurePercentColumn(TableColumn<FlatEntryDiff, Number> column,
        String propertyName, ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new PercentageTableCell<>());
        setColumnHeader(column, title, profileContext);
    }

    private void configurePercentColumn(TableColumn<FlatEntryDiff, Number> column,
        String propertyName, Function<Number, String> styler, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new PercentageTableCell<FlatEntryDiff>(styler));
        setColumnHeader(column, title, null);
    }

    private void configureCountColumn(TableColumn<FlatEntryDiff, Number> column,
        String propertyName, ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new CountTableCell<FlatEntryDiff>());
        setColumnHeader(column, title, profileContext);
    }

    private void configureCountDiffColumn(TableColumn<FlatEntryDiff, Number> column,
        Callback<CellDataFeatures<FlatEntryDiff, Number>, ObservableValue<Number>> callback,
        Function<Number, String> styler, String title)
    {
        column.setCellValueFactory(callback);
        column.setCellFactory(col -> new CountTableCell<FlatEntryDiff>(styler));
        setColumnHeader(column, title, null);
    }

    private void setColumnHeader(TableColumn<?, ?> column, String title,
        ProfileContext profileContext)
    {
        HBox header = createColoredLabelContainer(CENTER);

        column.setText(null);
        column.setGraphic(header);

        if (profileContext != null)
        {
            addProfileNr(header, profileContext);
        }

        header.getChildren().add(new Text(title));

        // Somehow it's hard to get a TableColumn to resize properly.
        // Therefore, we calculate a fair width ourselves.
        double newWidth = calculateWidth(header);
        column.setMinWidth(newWidth);
        column.setPrefWidth(newWidth + 5); // some extra margin
        diffTable.refresh();
    }

    private double calculateWidth(HBox box)
    {
        double width = 0;
        for (Node node : box.getChildren())
        {
            width += node.getBoundsInLocal().getWidth();
        }
        width += box.getSpacing() * (box.getChildren().size() - 1);
        width += box.getPadding().getLeft() + box.getPadding().getRight();
        return width;
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

    // AbstractViewController Implementation

    @Override
    protected FilterType[] getAllowedFilterTypes()
    {
        return new FilterType[]
        { STRING };
    }
}
