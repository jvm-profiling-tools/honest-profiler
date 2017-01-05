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
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExportDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.refreshTable;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPORT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeFlatProfileCsv;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_ACTIVE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;

import java.util.ArrayList;
import java.util.List;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.filters.Filter;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.core.filters.StringFilter;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.ports.javafx.model.task.CopyAndFilterProfile;
import com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.Rendering;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.GraphicalShareTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.PercentageTableCell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
    private TableColumn<FlatProfileEntry, Number> selfTimeShare;
    @FXML
    private TableColumn<FlatProfileEntry, Number> totalTimeShare;
    @FXML
    private TableColumn<FlatProfileEntry, Integer> selfCount;
    @FXML
    private TableColumn<FlatProfileEntry, Integer> totalCount;
    @FXML
    private TableColumn<FlatProfileEntry, Integer> traceCount;

    private ObservableList<FlatProfileEntry> flatProfile;

    private FilterDialogController filterDialogController;
    private ObjectProperty<FilterSpecification> filterSpec;

    private ProfileFilter currentFilter;
    private StringFilter quickFilter;

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(profileContext -> profileContext.profileProperty());

        currentFilter = new ProfileFilter();
        flatProfile = flatProfileView.getItems();

        exportButton.setOnAction(event -> showExportDialog(
            exportButton.getScene().getWindow(),
            "flat_profile.csv",
            out -> writeFlatProfileCsv(out, flatProfile, ReportUtil.Mode.CSV)
        ));

        initializeFilter();
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

    // Instance Accessors

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        filterDialogController.setApplicationContext(appCtx());
    }

    // Initialization Helper Methods

    private void initializeFilter()
    {
        filterDialogController = (FilterDialogController) DialogUtil
            .<FilterSpecification>createDialog(FILTER, "Specify Filters", false);
        filterDialogController.addAllowedFilterTypes(STRING, TIME_SHARE);

        filterSpec = new SimpleObjectProperty<>(null);
        filterSpec.addListener((property, oldValue, newValue) ->
        {
            filterButton.setGraphic(
                newValue == null || !newValue.isFiltering() ? viewFor(FUNNEL_16)
                    : viewFor(FUNNEL_ACTIVE_16));
            currentFilter = new ProfileFilter(newValue.getFilters());
            refresh(getTarget());
        });

        filterButton
            .setOnAction(event -> filterSpec.set(filterDialogController.showAndWait().get()));

        quickFilterButton.setOnAction(event -> applyQuickFilter());
    }

    private void applyQuickFilter()
    {
        String input = quickFilterText.getText();
        quickFilter = input.isEmpty() ? null : new StringFilter(
            Filter.Mode.CONTAINS,
            frame -> frame.getClassName() + "." + frame.getMethodName(),
            input);
        refresh(getTarget());
    }

    private void initializeTable()
    {
        method.setCellValueFactory(Rendering::method);
        method.setCellFactory(col -> new MethodNameTableCell<FlatProfileEntry>());

        selfTimeGraphical.setCellValueFactory(new PropertyValueFactory<>("selfTimeShare"));
        selfTimeGraphical.setCellFactory(col -> new GraphicalShareTableCell(col.getPrefWidth()));

        configureTimeShareColumn(selfTimeShare, "selfTimeShare");
        configureTimeShareColumn(totalTimeShare, "totalTimeShare");

        selfCount.setCellValueFactory(new PropertyValueFactory<>("selfCount"));
        totalCount.setCellValueFactory(new PropertyValueFactory<>("totalCount"));
        traceCount.setCellValueFactory(new PropertyValueFactory<>("traceCount"));
    }

    private void configureTimeShareColumn(TableColumn<FlatProfileEntry, Number> column,
        String propertyName)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new PercentageTableCell<FlatProfileEntry>());
    }

    // Refresh Methods

    @Override
    protected void refresh(Profile profile)
    {
        if (profile == null)
        {
            return;
        }

        CopyAndFilterProfile task = new CopyAndFilterProfile(profile, getAdjustedProfileFilter());
        task.setOnSucceeded(state ->
        {
            flatProfile.clear();
            task.getValue().flatByMethodProfile().forEach(flatProfile::add);
            refreshTable(flatProfileView);
        });
        appCtx().getExecutorService().execute(task);
    }

    private ProfileFilter getAdjustedProfileFilter()
    {
        if (quickFilter == null)
        {
            return currentFilter;
        }
        else
        {
            List<Filter> filters = new ArrayList<>();
            filters.add(quickFilter);
            filters.addAll(currentFilter.getFilters());
            return new ProfileFilter(filters);
        }
    }

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(exportButton, INFO_BUTTON_EXPORT);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(flatProfileView, INFO_TABLE_FLAT);
    }
}
