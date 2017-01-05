package com.insightfullogic.honest_profiler.ports.javafx.controller.filter;

import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.FILTER_CREATION;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.refreshTable;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_ADDFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_REMOVEFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_CHECK_HIDEERRORTHREADS;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_LIST_FILTERS;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.MINUS_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.PLUS_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;
import static java.util.stream.Collectors.toList;
import static javafx.scene.layout.Border.EMPTY;

import java.util.List;
import java.util.function.Function;

import com.insightfullogic.honest_profiler.ports.javafx.controller.dialog.AbstractDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterItem;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.TargetType;
import com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class FilterDialogController extends AbstractDialogController<FilterSpecification>
{
    @FXML
    private DialogPane dialogPane;
    @FXML
    private CheckBox hideErrorThreads;
    @FXML
    private TableView<FilterItem> filters;
    @FXML
    private TableColumn<FilterItem, FilterItem> action;
    @FXML
    private TableColumn<FilterItem, FilterType> type;
    @FXML
    private TableColumn<FilterItem, TargetType> target;
    @FXML
    private TableColumn<FilterItem, ComparisonType> comparison;
    @FXML
    private TableColumn<FilterItem, String> value;

    private FilterCreationDialogController filterCreationController;

    @Override
    @FXML
    public void initialize()
    {
        super.initialize();

        action.setCellFactory(column -> new ActionCell<>());
        action.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));

        type.setCellFactory(
            column -> new FilterTableCell<>((FilterType type) -> type.getDisplayName()));
        type.setCellValueFactory(
            data -> new ReadOnlyObjectWrapper<>(
                data.getValue() == null ? null : data.getValue().getFilterType()));

        target.setCellFactory(
            column -> new FilterTableCell<>((TargetType type) -> type.getDisplayName()));
        target.setCellValueFactory(
            data -> new ReadOnlyObjectWrapper<>(
                data.getValue() == null ? null : data.getValue().getTargetType()));

        comparison.setCellFactory(
            column -> new FilterTableCell<>((ComparisonType type) -> type.getDisplayName()));
        comparison.setCellValueFactory(
            data -> new ReadOnlyObjectWrapper<>(
                data.getValue() == null ? null : data.getValue().getComparisonType()));

        value.setCellValueFactory(
            data -> new ReadOnlyObjectWrapper<>(
                data.getValue() == null ? null : data.getValue().getValue()));

        filterCreationController = (FilterCreationDialogController) DialogUtil
            .<FilterItem>createDialog(FILTER_CREATION, "Specify Filter", true);

        // The NULL item is always in the last row, and is used to figure out
        // where to put the "Add
        // Filter" (plus) button
        filters.getItems().add(null);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        filterCreationController.setApplicationContext(applicationContext);
    }

    public void addAllowedFilterTypes(FilterType... filterType)
    {
        filterCreationController.addAllowedFilterTypes(filterType);
    }

    @Override
    public void reset()
    {
        // NOOP
    }

    @Override
    public Callback<ButtonType, FilterSpecification> createResultHandler()
    {
        return buttonType -> new FilterSpecification(
            hideErrorThreads.isSelected(),
            filters.getItems().stream().filter(item -> item != null).collect(toList()));
    }

    private void addFilter()
    {
        List<FilterItem> items = filters.getItems();
        filterCreationController.showAndWait().ifPresent(item -> items.add(items.size() - 1, item));
        refreshTable(filters);
    }

    private void removeFilter(FilterItem item)
    {
        filters.getItems().remove(item);
        refreshTable(filters);
    }

    private Button createActionButton(FilterItem item)
    {
        Button button = new Button();
        button.borderProperty().set(EMPTY);
        button.setMinSize(16, 16);
        button.setPrefSize(16, 16);
        button.setMaxSize(16, 16);
        button.setGraphic(viewFor(item == null ? PLUS_16 : MINUS_16));
        button.setOnAction(item == null ? event -> addFilter() : event -> removeFilter(item));
        return button;
    }

    @Override
    protected void initializeInfoText()
    {
        info(hideErrorThreads, INFO_CHECK_HIDEERRORTHREADS);
        info(filters, INFO_LIST_FILTERS);
    }

    private class ActionCell<T> extends TableCell<FilterItem, FilterItem>
    {
        @Override
        protected void updateItem(FilterItem item, boolean empty)
        {
            super.updateItem(item, empty);

            if (empty)
            {
                setText(null);
                setGraphic(null);
                return;
            }
            setGraphic(createActionButton(item));
            info(
                super.getGraphic(),
                item == null ? INFO_BUTTON_ADDFILTER : INFO_BUTTON_REMOVEFILTER);
        }
    }

    private class FilterTableCell<T> extends TableCell<FilterItem, T>
    {
        private Function<T, String> displayFunction;

        public FilterTableCell(Function<T, String> displayFunction)
        {
            this.displayFunction = displayFunction;
        }

        @Override
        protected void updateItem(T type, boolean empty)
        {
            super.updateItem(type, empty);

            if (empty || type == null)
            {
                setText(null);
                return;
            }
            setText(this.displayFunction.apply(type));
        }
    }
}
