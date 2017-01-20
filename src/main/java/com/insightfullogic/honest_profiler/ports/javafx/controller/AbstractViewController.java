package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil.FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.TITLE_DIALOG_SPECIFYFILTERS;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.doubleDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.intDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.longDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_ACTIVE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;
import static javafx.scene.input.KeyCode.ENTER;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.CountTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.CountTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.PercentageTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.PercentageTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.TimeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.TimeTreeTableCell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;

/**
 * Superclass for all View Controllers in the application. These controllers
 * provide a particular view on data. The class holds the code for the filters
 * and quick filter.
 *
 * The superclass also provides some common UI helper methods for column
 * configuration.
 */
public abstract class AbstractViewController<T> extends AbstractController
{
    private Button filterButton;
    private Button quickFilterButton;
    private TextField quickFilterText;

    private FilterDialogController<T> dialogController;
    private ObjectProperty<FilterSpecification<T>> filterSpec;

    private ItemType type;

    /**
     * This method must be called by subclasses in their FXML initialize(). It
     * provides the controller-local UI nodes needed by the
     * AbstractViewController.
     *
     * @param filterButton the button used to trigger filter editing
     * @param quickFilterButton the button used to apply the quick filter
     * @param quickFilterText the TextField providing the value for the quick
     *            filter
     * @param type the {@link ItemType} shown in the vies
     */
    protected void initialize(Button filterButton, Button quickFilterButton,
        TextField quickFilterText, ItemType type)
    {
        super.initialize();

        if (filterButton == null)
        {
            return;
        }

        this.filterButton = filterButton;
        this.quickFilterButton = quickFilterButton;
        this.quickFilterText = quickFilterText;

        this.type = type;
        filterSpec = new SimpleObjectProperty<>(new FilterSpecification<>(type));
    }

    // Accessors

    /**
     * In addition to the normal functionality, the method calls filter
     * initialization, which needs the ApplicationContext to be present. If a
     * particular view controller
     *
     * @param applicationContext the ApplicationContext of this application
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);

        if (filterButton == null)
        {
            return;
        }

        initializeFilters(applicationContext);
    }

    /**
     * Refreshes the view. The view should be updated based on the current state
     * of the {@link Profile} and {@link ProfileFilter}.
     */
    protected abstract void refresh();

    // UI Helper Methods

    protected abstract <C> void setColumnHeader(C column, String title, ProfileContext context);

    protected <U> void cfgPctCol(TableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new PercentageTableCell<>(null));
        setColumnHeader(column, title, profileContext);
    }

    protected <U> void cfgPctDiffCol(TableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new PercentageTableCell<>(doubleDiffStyler));
        setColumnHeader(column, title, null);
    }

    protected <U> void cfgNrCol(TableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new CountTableCell<>(null));
        setColumnHeader(column, title, profileContext);
    }

    protected <U> void cfgNrDiffCol(TableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new CountTableCell<>(intDiffStyler));
        setColumnHeader(column, title, null);
    }

    protected <U> void cfgTimeCol(TableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new TimeTableCell<>(null));
        setColumnHeader(column, title, profileContext);
    }

    protected <U> void cfgTimeDiffCol(TableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new TimeTableCell<>(longDiffStyler));
        setColumnHeader(column, title, null);
    }

    protected <U> void cfgPctCol(TreeTableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new PercentageTreeTableCell<>(null));
        setColumnHeader(column, title, profileContext);
    }

    protected <U> void cfgPctDiffCol(TreeTableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new PercentageTreeTableCell<>(doubleDiffStyler));
        setColumnHeader(column, title, null);
    }

    protected <U> void cfgNrCol(TreeTableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new CountTreeTableCell<>(null));
        setColumnHeader(column, title, profileContext);
    }

    protected <U> void cfgNrDiffCol(TreeTableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new CountTreeTableCell<>(intDiffStyler));
        setColumnHeader(column, title, null);
    }

    protected <U> void cfgTimeCol(TreeTableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new TimeTreeTableCell<>(null));
        setColumnHeader(column, title, profileContext);
    }

    protected <U> void cfgTimeDiffCol(TreeTableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new TimeTreeTableCell<>(longDiffStyler));
        setColumnHeader(column, title, null);
    }

    // Filter-related methods

    /**
     * Returns the current {@link FilterSpecification}.
     *
     * @return the current {@link FilterSpecification}
     */
    protected FilterSpecification<T> getFilterSpecification()
    {
        return filterSpec.get();
    }

    /**
     * Initializes the filters.
     *
     * @param applicationContext the {@link ApplicationContext}. The parameter
     *            is used to explicitly point out the dependency on the presense
     *            of the context.
     */
    private void initializeFilters(ApplicationContext applicationContext)
    {

        dialogController = createFilterDialog();
        dialogController.setApplicationContext(applicationContext);
        dialogController.setItemType(type);

        filterSpec.addListener((property, oldValue, newValue) ->
        {
            filterButton.setGraphic(iconFor(newValue));
            refresh();
        });

        filterButton.setOnAction(event -> filterSpec.set(dialogController.showAndWait().get()));

        quickFilterButton.setOnAction(event -> applyQuickFilter());
        quickFilterText.setOnKeyPressed(event ->
        {
            if (event.getCode() == ENTER)
            {
                applyQuickFilter();
            }
        });
    }

    private FilterDialogController<T> createFilterDialog()
    {
        return (FilterDialogController<T>) DialogUtil.<FilterSpecification<T>>newDialog(
            appCtx(),
            FILTER,
            getText(TITLE_DIALOG_SPECIFYFILTERS),
            false);
    }

    private ImageView iconFor(FilterSpecification<T> spec)
    {
        return spec == null || !spec.isFiltering() ? viewFor(FUNNEL_16) : viewFor(FUNNEL_ACTIVE_16);
    }

    private void applyQuickFilter()
    {
        String input = quickFilterText.getText();
        filterSpec.get().setQuickFilter(input == null || input.isEmpty() ? null : input);
        refresh();
    }
}
