package com.insightfullogic.honest_profiler.ports.javafx.controller.filter;

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

import com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterItem;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.filter.Target;
import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.ports.javafx.controller.dialog.AbstractDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class FilterDialogController<T> extends AbstractDialogController<FilterSpecification<T>>
{
    @FXML
    private Dialog<FilterSpecification<T>> dialog;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private CheckBox hideErrorThreads;
    @FXML
    private TableView<FilterItem<T, ?>> filters;
    @FXML
    private TableColumn<FilterItem<T, ?>, FilterItem<T, ?>> action;
    @FXML
    private TableColumn<FilterItem<T, ?>, Target> target;
    @FXML
    private TableColumn<FilterItem<T, ?>, Comparison> comparison;
    @FXML
    private TableColumn<FilterItem<T, ?>, Object> value;
    @FXML
    private FilterCreationDialogController<T> filterCreationController;

    private ItemType type;

    @Override
    @FXML
    public void initialize()
    {
        super.initialize(dialog);

        action.setCellFactory(column -> new ActionCell<>());
        action.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));

        target.setCellFactory(
            column -> new FilterCell<>((Target type) -> type.toString()));
        target.setCellValueFactory(
            data -> new ReadOnlyObjectWrapper<>(
                data.getValue() == null ? null : data.getValue().getTarget()));

        comparison.setCellFactory(
            column -> new FilterCell<>((Comparison type) -> type.toString()));
        comparison.setCellValueFactory(
            data -> new ReadOnlyObjectWrapper<>(
                data.getValue() == null ? null : data.getValue().getComparison()));

        value.setCellFactory(
            column -> new FilterCell<>(object -> object.toString()));
        value.setCellValueFactory(
            data -> new ReadOnlyObjectWrapper<>(
                data.getValue() == null ? null : data.getValue().getValue()));

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

    public void setItemType(ItemType type)
    {
        this.type = type;
        filterCreationController.setItemType(type);
    }

    @Override
    public void reset()
    {
        // NOOP
    }

    @Override
    public Callback<ButtonType, FilterSpecification<T>> createResultHandler()
    {
        return buttonType -> new FilterSpecification<T>(
            type,
            hideErrorThreads.isSelected(),
            filters.getItems().stream().filter(item -> item != null).collect(toList()));
    }

    private void addFilter()
    {
        List<FilterItem<T, ?>> items = filters.getItems();
        filterCreationController.showAndWait().ifPresent(item -> items.add(items.size() - 1, item));
        refreshTable(filters);
    }

    private void removeFilter(FilterItem<T, ?> item)
    {
        filters.getItems().remove(item);
        refreshTable(filters);
    }

    private Button createActionButton(FilterItem<T, ?> item)
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

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(hideErrorThreads, INFO_CHECK_HIDEERRORTHREADS);
        info(filters, INFO_LIST_FILTERS);
    }

    @Override
    protected void initializeHandlers()
    {
        //
    }

    // Helper Classes

    private class ActionCell<U> extends TableCell<FilterItem<T, ?>, FilterItem<T, ?>>
    {
        @Override
        protected void updateItem(FilterItem<T, ?> item, boolean empty)
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

    private class FilterCell<U, V> extends TableCell<FilterItem<U, ?>, V>
    {
        private Function<V, String> displayFunction;

        public FilterCell(Function<V, String> displayFunction)
        {
            this.displayFunction = displayFunction;
        }

        @Override
        protected void updateItem(V type, boolean empty)
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
