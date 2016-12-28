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
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeFlatProfileCsv;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.COMPARE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.EXPORT_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_ACTIVE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;

import java.util.List;

import com.insightfullogic.honest_profiler.core.collector.FlatProfileEntry;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.Rendering;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.GraphicalShareTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.PercentageTableCell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class FlatViewController extends AbstractController
{
    @FXML
    private Button filterButton;
    @FXML
    private Button compareButton;
    @FXML
    private Button exportButton;

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

    private ProfileContext profileContext;

    private ProfileFilter currentFilter;

    @FXML
    private void initialize()
    {
        info(filterButton, "Specify filters restricting the visible entries");
        info(compareButton, "Click to select another open profile to compare this profile against");
        info(exportButton, "Export the visible entries to a CSV file");
        info(flatProfileView, "Shows methods and their Self and Total usage percentages");

        currentFilter = new ProfileFilter();
        flatProfile = flatProfileView.getItems();

        exportButton.setGraphic(viewFor(EXPORT_16));
        exportButton.setTooltip(new Tooltip("Export the current view to a file"));
        exportButton.setOnAction(event ->

        showExportDialog(
            exportButton.getScene().getWindow(),
            "flat_profile.csv",
            out -> writeFlatProfileCsv(out, flatProfile, ReportUtil.Mode.CSV)
        ));

        initializeComparison();
        initializeFilter();
        initializeTable();
    }

    // Instance Accessors

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {

        super.setApplicationContext(applicationContext);
        filterDialogController.setApplicationContext(appCtx());
    }

    public void setProfileContext(ProfileContext profileContext)
    {
        this.profileContext = profileContext;
        profileContext.profileProperty()
            .addListener((property, oldValue, newValue) -> refresh(newValue));
    }

    // Initialization Helper Methods

    private void initializeComparison()
    {
        compareButton.setGraphic(viewFor(COMPARE_16));
        compareButton
            .setTooltip(new Tooltip("Compare this profile with another open profile"));
        compareButton.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                ContextMenu ctxMenu = compareButton.getContextMenu();
                if (ctxMenu == null)
                {
                    ctxMenu = new ContextMenu();
                    compareButton.setContextMenu(ctxMenu);
                }
                refreshContextMenu(compareButton.getContextMenu());
                compareButton.getContextMenu().show(
                    compareButton,
                    event.getScreenX(),
                    event.getScreenY());
            }
        });
    }

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
            refresh(profileContext.getProfile());
        });

        filterButton.setGraphic(viewFor(FUNNEL_16));
        filterButton.setTooltip(new Tooltip("Specify filters"));
        filterButton
            .setOnAction(event -> filterSpec.set(filterDialogController.showAndWait().get()));
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

    private void refresh(Profile profile)
    {
        Profile newProfile = profile.copy();
        currentFilter.accept(newProfile);

        flatProfile.clear();
        newProfile.flatByMethodProfile().forEach(flatProfile::add);

        refreshTable(flatProfileView);
    }

    // Compare Helper Methods

    private void refreshContextMenu(ContextMenu menu)
    {
        menu.getItems().clear();

        List<String> profileNames = appCtx().getOpenProfileNames();

        profileNames.forEach(name ->
        {
            if (name.equals(profileContext.getName()))
            {
                return;
            }

            MenuItem item = new MenuItem(name);
            item.setOnAction(
                event -> appCtx().createDiffView(profileContext.getName(), name));
            menu.getItems().add(item);
        });
    }
}
