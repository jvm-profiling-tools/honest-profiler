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

import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.ENTRY;
import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.showExportDialog;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.refreshTable;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_TIME;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_TIME_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_TIME;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_TIME_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDECNTALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDEPCTALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDESELFALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDETIMEALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_HIDETOTALALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWCNTALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWPCTALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWSELFALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWTIMEALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.CTXMENU_COLVW_SHOWTOTALALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLUMNVIEW;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPORT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_FLAT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil.writeFlatProfileCsv;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Entry;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Flat;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.util.report.ReportUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.GraphicalShareTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTableCell;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for Views which displays the contents of a {@link Flat} {@link Aggregation}.
 */
public class FlatViewController extends AbstractProfileViewController<Flat, Entry>
{
    @FXML
    private Button filterButton;
    @FXML
    private Button columnViewButton;
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
    private TableView<Entry> flatTable;
    @FXML
    private TableColumn<Entry, String> method;
    @FXML
    private TableColumn<Entry, Double> selfTimeGraphical;
    @FXML
    private TableColumn<Entry, Number> selfCntPct;
    @FXML
    private TableColumn<Entry, Number> totalCntPct;
    @FXML
    private TableColumn<Entry, Number> selfCnt;
    @FXML
    private TableColumn<Entry, Number> totalCnt;
    @FXML
    private TableColumn<Entry, Number> selfTimePct;
    @FXML
    private TableColumn<Entry, Number> totalTimePct;
    @FXML
    private TableColumn<Entry, Number> selfTime;
    @FXML
    private TableColumn<Entry, Number> totalTime;

    @FXML
    private FilterDialogController<Entry> filterController;

    private ObservableList<Entry> flatProfile;

    private Set<TableColumnBase<?, ?>> selfColumns;
    private Set<TableColumnBase<?, ?>> totalColumns;
    private Set<TableColumnBase<?, ?>> cntColumns;
    private Set<TableColumnBase<?, ?>> pctColumns;
    private Set<TableColumnBase<?, ?>> timeColumns;

    // FXML Implementation

    @Override
    @FXML
    protected void initialize()
    {
        flatProfile = flatTable.getItems();

        super.initialize(ENTRY);
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

    // Instance Accessors

    /**
     * Returns the {@link ReadOnlyObjectProperty} tracking which item is currently selected.
     * <p>
     *
     * @return the {@link ReadOnlyObjectProperty} tracking which item is currently selected
     */
    public ReadOnlyObjectProperty<Entry> selectedProperty()
    {
        return flatTable.getSelectionModel().selectedItemProperty();
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(columnViewButton, INFO_BUTTON_COLUMNVIEW);
        info(exportButton, INFO_BUTTON_EXPORT);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(flatTable, INFO_TABLE_FLAT);
    }

    @Override
    protected void initializeHandlers()
    {
        exportButton.setOnAction(
            event -> showExportDialog(
                appCtx(),
                exportButton.getScene().getWindow(),
                "flat_profile.csv",
                out -> writeFlatProfileCsv(appCtx(), out, flatProfile, ReportUtil.Mode.CSV)
            ));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        flatProfile.clear();
        Flat target = getTarget();

        if (target != null)
        {
            flatProfile.addAll(target.filter(getFilterSpecification()).getData());
            flatTable.refresh();
        }

        refreshTable(flatTable);

        flatTable.sort();
    }

    /**
     * Initializes the {@link TableView} which displays the {@link Flat} {@link Aggregation}.
     */
    @Override
    protected void initializeTable()
    {
        method.setCellValueFactory(new PropertyValueFactory<>("key"));
        method.setCellFactory(col -> new MethodNameTableCell<Entry>());

        selfTimeGraphical.setCellValueFactory(new PropertyValueFactory<>("selfCntPct"));
        selfTimeGraphical.setCellFactory(col -> new GraphicalShareTableCell<>(appCtx()));

        // The column configuration methods should (for now) be called in the same order as the columns are declared in
        // the FXML.
        // The only reason is the column view context menu, which collects its menu items in the order of these
        // declarations. If the order is out of sync, the column view context menu will not have its items in the
        // correct order.
        // This will probably, eventually, be remedied by reordering but for now this comment will have to do.
        cfgPctCol(selfCntPct, "selfCntPct", prfCtx(), COLUMN_SELF_CNT_PCT);
        cfgPctCol(totalCntPct, "totalCntPct", prfCtx(), COLUMN_TOTAL_CNT_PCT);
        cfgNrCol(selfCnt, "selfCnt", prfCtx(), COLUMN_SELF_CNT);
        cfgNrCol(totalCnt, "totalCnt", prfCtx(), COLUMN_TOTAL_CNT);
        cfgPctCol(selfTimePct, "selfTimePct", prfCtx(), COLUMN_SELF_TIME_PCT);
        cfgPctCol(totalTimePct, "totalTimePct", prfCtx(), COLUMN_TOTAL_TIME_PCT);
        cfgTimeCol(selfTime, "selfTime", prfCtx(), COLUMN_SELF_TIME);
        cfgTimeCol(totalTime, "totalTime", prfCtx(), COLUMN_TOTAL_TIME);
    }

    /**
     * Defines the column sets for collective showing or hiding.
     */
    private void createColumnSets()
    {
        selfColumns = new HashSet<>(asList(selfCnt, selfCntPct, selfTime, selfTimePct));
        totalColumns = new HashSet<>(asList(totalCnt, totalCntPct, totalTime, totalTimePct));
        cntColumns = new HashSet<>(asList(selfCnt, totalCnt));
        pctColumns = new HashSet<>(asList(selfCntPct, totalCntPct, selfTimePct, totalTimePct));
        timeColumns = new HashSet<>(asList(selfTime, totalTime, selfTimePct, totalTimePct));
    }
}
