package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping.combine;
import static com.insightfullogic.honest_profiler.ports.javafx.util.FxUtil.reconfigureColumn;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.doubleDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.intDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.longDiffStyler;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.FUNNEL_ACTIVE_16;
import static com.insightfullogic.honest_profiler.ports.javafx.view.Icon.viewFor;
import static javafx.scene.input.KeyCode.ENTER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterSpecification;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.CombinedGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.core.aggregation.result.diff.DiffEntry;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.NumberTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.NumberTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.menu.ColumnGroupMenuItem;
import com.insightfullogic.honest_profiler.ports.javafx.view.menu.ColumnMenuItem;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 * Superclass for all View Controllers in the application. These controllers provide a particular view on data
 * consisting of items of type T. The class manages the filter, quickFilter, grouping and column view controls.
 * <p>
 * This superclass also provides some common UI helper methods for column configuration.
 * <p>
 *
 * @param <T> the type of the items contained in the View which can be filtered
 */
public abstract class AbstractViewController<T> extends AbstractController
{
    // Instance Properties

    // - Filtering

    private FilterDialogController<T> filterController;
    private Button filterButton;
    private Button quickFilterButton;
    private TextField quickFilterText;

    private ObjectProperty<FilterSpecification<T>> filterSpec;

    // - Grouping

    private Label threadGroupingLabel;
    private ChoiceBox<ThreadGrouping> threadGrouping;
    private Label frameGroupingLabel;
    private ChoiceBox<FrameGrouping> frameGrouping;

    private ObjectProperty<CombinedGrouping> grouping;

    // - Column View

    private Button columnViewButton;
    private List<ColumnMenuItem> columnViewItems;
    private List<ColumnGroupMenuItem> columnViewGroupItems;

    // - General

    private ItemType type;

    // FXML Implementation

    /**
     * Initialize method for subclasses which sets the basic properties needed by this superdclass. This method must be
     * called by such subclasses in their FXML initialize().
     * <p>
     *
     * @param type the {@link ItemType} of the items shown in the view
     */
    protected void initialize(ItemType type)
    {
        super.initialize();

        columnViewItems = new ArrayList<>();
        columnViewGroupItems = new ArrayList<>();

        this.type = type;
    }

    /**
     * Initialize method for subclasses which have filter-related controls, which will be managed by this superclass.
     * This method must be called by such subclasses in their FXML initialize(). It should provide the controller-local
     * UI nodes needed by the AbstractViewController.
     * <p>
     *
     * @param filterController the {@link FilterDialogController}
     * @param filterButton the button used to trigger filter editing
     * @param quickFilterButton the button used to apply the quick filter
     * @param quickFilterText the TextField providing the value for the quick filter
     */
    protected void initializeFiltering(FilterDialogController<T> filterController,
        Button filterButton, Button quickFilterButton, TextField quickFilterText)
    {
        this.filterController = filterController;

        this.filterButton = filterButton;
        this.quickFilterButton = quickFilterButton;
        this.quickFilterText = quickFilterText;

        // Model initialization
        filterSpec = new SimpleObjectProperty<>(new FilterSpecification<>(type));
    }

    /**
     * Initialize method for subclasses which have grouping-related controls, which will be managed by this superclass.
     * This method must be called by such subclasses in their FXML initialize(). It should provide the controller-local
     * UI nodes needed by the AbstractViewController.
     * <p>
     *
     * @param threadGroupingLabel the label next to the {@link ThreadGrouping} {@link ChoiceBox}
     * @param threadGrouping the {@link ThreadGrouping} {@link ChoiceBox}
     * @param frameGroupingLabel the label next to the {@link FrameGrouping} {@link ChoiceBox}
     * @param frameGrouping the {@link FrameGrouping} {@link ChoiceBox}
     */
    protected void initializeGrouping(Label threadGroupingLabel,
        ChoiceBox<ThreadGrouping> threadGrouping, Label frameGroupingLabel,
        ChoiceBox<FrameGrouping> frameGrouping)
    {
        this.threadGroupingLabel = threadGroupingLabel;
        this.threadGrouping = threadGrouping;
        this.frameGroupingLabel = frameGroupingLabel;
        this.frameGrouping = frameGrouping;

        // Model initialization
        grouping = new SimpleObjectProperty<>();

        setVisibility(
            false,
            threadGroupingLabel,
            threadGrouping,
            frameGroupingLabel,
            frameGrouping);
    }

    protected void initializeColumnView(Button columnViewButton)
    {
        this.columnViewButton = columnViewButton;
        columnViewButton.setOnMousePressed(this::showColumnViewMenu);
    }

    // Instance Accessors

    /**
     * @see AbstractController#setApplicationContext(ApplicationContext)
     *
     * @param applicationContext the ApplicationContext of this application
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        applicationContext.getConfiguration()
            .addListener((property, oldValue, newValue) -> refresh());

        // If the subclass doesn't need filter management, no UI control should have been passed on in the initialize()
        // method.
        if (filterButton == null)
        {
            return;
        }

        initializeFilters(applicationContext);
    }

    // Grouping-related Methods

    /**
     * Specify which {@link ThreadGrouping}s are allowed for the View. This method should be called by the controller
     * which configures the View, if the View supports groupings.
     * <p>
     *
     * @param groupings the {@link ThreadGrouping}s allowed for the View
     */
    public void setAllowedThreadGroupings(ThreadGrouping... groupings)
    {
        threadGrouping.getItems().addAll(groupings);
        // Don't show choice if there actually is no choice :)
        setVisibility(groupings.length > 1, threadGroupingLabel, threadGrouping);
        bindGroupings();
    }

    /**
     * Specify which {@link FrameGrouping}s are allowed for the View. This method should be called by the controller
     * which configures the View, if the View supports groupings.
     * <p>
     *
     * @param groupings the {@link FrameGrouping}s allowed for the View
     */
    public void setAllowedFrameGroupings(FrameGrouping... groupings)
    {
        frameGrouping.getItems().addAll(groupings);
        // Don't show choice if there actually is no choice :)
        setVisibility(groupings.length > 1, frameGroupingLabel, frameGrouping);
        bindGroupings();
    }

    /**
     * Returns the property containing the {@link CombinedGrouping} made up by the currently selected
     * {@link FrameGrouping} and {@link ThreadGrouping}s.
     * <p>
     *
     * @return the property containing the the {@link CombinedGrouping} made up by the currently selected
     *         {@link FrameGrouping} and {@link ThreadGrouping}s
     */
    public ObservableObjectValue<CombinedGrouping> getGrouping()
    {
        return grouping;
    }

    // Column View-related Methods

    /**
     * Adds an appropriate {@link ColumnMenuItem} to the column view menu item list.
     *
     * @param column the {@link TableColumnBase} for which a menu item will be added
     * @param title the display title for the column
     * @param context the {@link ProfileContext} of the profile for which the column contains data, or null for a Diff
     *            column which contains comparison data between both profiles in a Diff
     */
    private void addColumnMenuItem(TableColumnBase<?, ?> column, String title,
        ProfileContext context)
    {
        columnViewItems.add(new ColumnMenuItem(column, getColumnHeader(column, title, context)));
    }

    /**
     * Adds a menu item which controls the visibility of a group of columns.
     *
     * @param title the display title for the menu item
     * @param visible the visibility of the columns after the menu item has been selected
     * @param the columns in the group
     */
    protected void addColumnGroupMenuItem(String title, boolean visible,
        TableColumnBase<?, ?>... columns)
    {
        columnViewGroupItems.add(new ColumnGroupMenuItem(title, visible, columns));
    }

    /**
     * Adds a menu item which controls the visibility of a group of columns.
     *
     * @param title the display title for the menu item
     * @param visible the visibility of the columns after the menu item has been selected
     * @param a number of {@link Collection}s containing the columns in the group
     */
    protected void addColumnGroupMenuItem(String title, boolean visible,
        Collection<TableColumnBase<?, ?>> columns)
    {
        columnViewGroupItems.add(new ColumnGroupMenuItem(title, visible, columns));
    }

    /**
     * Displays the column view context menu.
     *
     * @param event the {@link MouseEvent} which triggered the context menu display
     */
    private void showColumnViewMenu(MouseEvent event)
    {
        ContextMenu ctxMenu = columnViewButton.getContextMenu();
        if (ctxMenu == null)
        {
            ctxMenu = new ContextMenu();
            columnViewButton.setContextMenu(ctxMenu);
        }

        // The +1 accounts for the separator
        if (ctxMenu.getItems().size() != columnViewItems.size() + columnViewGroupItems.size() + 1)
        {
            ctxMenu.getItems().clear();
            ctxMenu.getItems().addAll(columnViewGroupItems);
            ctxMenu.getItems().add(new SeparatorMenuItem());
            ctxMenu.getItems().addAll(columnViewItems);
        }

        columnViewButton.getContextMenu()
            .show(columnViewButton, event.getScreenX(), event.getScreenY());
    }

    // UI Helper Methods

    /**
     * Refreshes the view. The view should be updated based on the current states of the data in the subclass, and the
     * {@link FilterSpecification} and {@link CombinedGrouping}s as currently selected at the
     * {@link AbstractViewController} level.
     */
    protected abstract void refresh();

    /**
     * Initialize the {@link TableView} or {@link TreeTableView} which contains the View data, if applicable.
     * <p>
     * Care should be taken in the subclassed to call this at the right time, because some contextual information may be
     * needed. The {@link ApplicationContext} must be set for the I18N to work, and in the case of Diff column headers,
     * the {@link ProfileContext}s for the profiles being compared should also be known. In the current implementation
     * therefore, the method is called in the {@link AbstractProfileViewController#setProfileContext(ProfileContext)}
     * and {@link AbstractProfileDiffViewController#setProfileContexts(ProfileContext, ProfileContext)} methods.
     */
    protected abstract void initializeTable();

    // UI Helper Methods : Column Configuration

    /**
     * Get the contents of the column header. This method is abstract because different implementing controllers have
     * different requirements. E.g. the Diff views need to include indications of which of the profiles being compared
     * the column data is for.
     * <p>
     * If null is passed as {@link ProfileContext}, this indicates to the subclass that the data for the column is a
     * comparison between both profiles from a {@link DiffEntry}.
     * <p>
     * The method should return null if the column has a fixed text header defined in the FXML (or elsewhere).
     * <p>
     *
     * @param <C> the type of the column
     * @param column the column for which the header contents should be set
     * @param title the display title for the column
     * @param context the {@link ProfileContext} of the profile for which the column contains data, or null for a Diff
     *            column which contains comparison data between both profiles in a Diff
     * @return an {@link HBox} which can be used as header for the column
     */
    protected abstract HBox getColumnHeader(TableColumnBase<?, ?> column, String title,
        ProfileContext context);

    /**
     * Sets the appropriate header for the column, using
     *
     * @param column
     * @param title
     * @param context
     */
    private void configureHeader(TableColumnBase<?, ?> column, String title, ProfileContext context)
    {
        HBox header = getColumnHeader(column, title, context);
        if (header != null)
        {
            reconfigureColumn(column, header);
        }
    }

    // Unfortunately the Cell- and CellValueFactories for TableColumns and TreeTableColumns are not compatible. As a
    // result, we get full code duplication. All the following UI helper methods are provided once for TableColumns, and
    // once for TreeTableColumns. Thusly ye cookie crumbleth.

    // UI Helper Methods : Table Column Configuration

    /**
     * Configures a {@link TableColumn} containing percentages calculated for a single profile.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param profileContext the {@link ProfileContext} for the profile whose data is shown
     * @param title the column title
     */
    protected <U> void cfgPctCol(TableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new NumberTableCell<>(appCtx()::displayPercent, null));
        configureHeader(column, title, profileContext);
        addColumnMenuItem(column, title, profileContext);
    }

    /**
     * Configures a {@link TableColumn} containing the percentage difference comparing two profiles.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param title the column title
     */
    protected <U> void cfgPctDiffCol(TableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(
            col -> new NumberTableCell<>(appCtx()::displayPercent, doubleDiffStyler));
        configureHeader(column, title, null);
        addColumnMenuItem(column, title, null);
    }

    /**
     * Configures a {@link TableColumn} containing numbers calculated for a single profile.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param profileContext the {@link ProfileContext} for the profile whose data is shown
     * @param title the column title
     */
    protected <U> void cfgNrCol(TableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new NumberTableCell<>(appCtx()::displayIntegral, null));
        configureHeader(column, title, profileContext);
        addColumnMenuItem(column, title, profileContext);
    }

    /**
     * Configures a {@link TableColumn} containing the number difference comparing two profiles.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param title the column title
     */
    protected <U> void cfgNrDiffCol(TableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new NumberTableCell<>(appCtx()::displayIntegral, intDiffStyler));
        configureHeader(column, title, null);
        addColumnMenuItem(column, title, null);
    }

    /**
     * Configures a {@link TableColumn} containing durations calculated for a single profile.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param profileContext the {@link ProfileContext} for the profile whose data is shown
     * @param title the column title
     */
    protected <U> void cfgTimeCol(TableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new NumberTableCell<>(appCtx()::displayTime, null));
        configureHeader(column, title, profileContext);
        addColumnMenuItem(column, title, profileContext);
    }

    /**
     * Configures a {@link TableColumn} containing the duration difference comparing two profiles.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param title the column title
     */
    protected <U> void cfgTimeDiffCol(TableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new NumberTableCell<>(appCtx()::displayTime, longDiffStyler));
        configureHeader(column, title, null);
        addColumnMenuItem(column, title, null);
    }

    // UI Helper Methods : TreeTable Column Configuration

    /**
     * Configures a {@link TreeTableColumn} containing percentages calculated for a single profile.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param profileContext the {@link ProfileContext} for the profile whose data is shown
     * @param title the column title
     */
    protected <U> void cfgPctCol(TreeTableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new NumberTreeTableCell<>(appCtx()::displayPercent, null));
        configureHeader(column, title, profileContext);
        addColumnMenuItem(column, title, profileContext);
    }

    /**
     * Configures a {@link TreeTableColumn} containing the percentage difference comparing two profiles.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param title the column title
     */
    protected <U> void cfgPctDiffCol(TreeTableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(
            col -> new NumberTreeTableCell<>(appCtx()::displayPercent, doubleDiffStyler));
        configureHeader(column, title, null);
        addColumnMenuItem(column, title, null);
    }

    /**
     * Configures a {@link TreeTableColumn} containing numbers calculated for a single profile.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param profileContext the {@link ProfileContext} for the profile whose data is shown
     * @param title the column title
     */
    protected <U> void cfgNrCol(TreeTableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new NumberTreeTableCell<>(appCtx()::displayIntegral, null));
        configureHeader(column, title, profileContext);
        addColumnMenuItem(column, title, profileContext);
    }

    /**
     * Configures a {@link TreeTableColumn} containing the number difference comparing two profiles.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param title the column title
     */
    protected <U> void cfgNrDiffCol(TreeTableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column
            .setCellFactory(col -> new NumberTreeTableCell<>(appCtx()::displayIntegral, intDiffStyler));
        configureHeader(column, title, null);
        addColumnMenuItem(column, title, null);
    }

    /**
     * Configures a {@link TreeTableColumn} containing durations calculated for a single profile.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param profileContext the {@link ProfileContext} for the profile whose data is shown
     * @param title the column title
     */
    protected <U> void cfgTimeCol(TreeTableColumn<U, Number> column, String propertyName,
        ProfileContext profileContext, String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new NumberTreeTableCell<>(appCtx()::displayTime, null));
        configureHeader(column, title, profileContext);
        addColumnMenuItem(column, title, profileContext);
    }

    /**
     * Configures a {@link TableColumn} containing the duration difference comparing two profiles.
     * <p>
     *
     * @param <U> the type of the items in the {@link TableView} containing the {@link TableColumn}
     * @param column the column being configured
     * @param propertyName the name of the property containing the value for this column
     * @param title the column title
     */
    protected <U> void cfgTimeDiffCol(TreeTableColumn<U, Number> column, String propertyName,
        String title)
    {
        column.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
        column.setCellFactory(
            col -> new NumberTreeTableCell<>(appCtx()::displayTime, longDiffStyler));
        configureHeader(column, title, null);
        addColumnMenuItem(column, title, null);
    }

    // Filter-related methods

    /**
     * Returns the current {@link FilterSpecification}.
     * <p>
     *
     * @return the current {@link FilterSpecification}
     */
    protected FilterSpecification<T> getFilterSpecification()
    {
        return filterSpec.get();
    }

    /**
     * Initializes the filters.
     * <p>
     * The {@link ApplicationContext} parameter is used to explicitly point out the dependency on the presence of the
     * context, to anybody who wants to modify {@link AbstractViewController}.
     * <p>
     *
     * @param applicationContext the {@link ApplicationContext}
     */
    private void initializeFilters(ApplicationContext applicationContext)
    {
        // Prepare the filter selection Dialog
        filterController.setApplicationContext(applicationContext);
        filterController.setItemType(type);

        // When the FilterSpecification is updated, refresh the view and update the button icon.
        filterSpec.addListener((property, oldValue, newValue) ->
        {
            filterButton.setGraphic(iconFor(newValue));
            refresh();
        });

        // Show the filter selection Dialog when pushing the Button.
        filterButton.setOnAction(event -> filterSpec.set(filterController.showAndWait().get()));

        // Apply the QuickFilter when the quickFilter button is Pressed.
        quickFilterButton.setOnAction(event -> applyQuickFilter());

        // Apply the QuickFilter when the user presses RETURN in the quickFilter text area.
        quickFilterText.setOnKeyPressed(event ->
        {
            if (event.getCode() == ENTER)
            {
                applyQuickFilter();
            }
        });
    }

    /**
     * Select the appropriate icon for the state of the specified {@link FilterSpecification}.
     * <p>
     *
     * @param spec the {@link FilterSpecification}
     * @return an {@link ImageView} for the appropriate icon
     */
    private ImageView iconFor(FilterSpecification<T> spec)
    {
        return spec == null || !spec.isFiltering() ? viewFor(FUNNEL_16) : viewFor(FUNNEL_ACTIVE_16);
    }

    /**
     * Apply the quickFilter by updating the {@link FilterSpecification} (which will trigger the {@link ChangeListener}
     * which will refresh the view).
     */
    private void applyQuickFilter()
    {
        String input = quickFilterText.getText();
        filterSpec.get().setQuickFilter(input == null || input.isEmpty() ? null : input);
        refresh();
    }

    /**
     * Shows or hides the selected {@link Node}s. Used to show or hide the grouping {@link ChoiceBox}es.
     * <p>
     *
     * @param visible a boolean indicating whether the {@link Node}s should be visible
     * @param nodes the {@link Node}s to be made (in)visible
     */
    private void setVisibility(boolean visible, Node... nodes)
    {
        for (Node node : nodes)
        {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }

    /**
     * Configures the grouping controls, ensuring a new {@link CombinedGrouping} is set whenever either a new
     * {@link ThreadGrouping} or {@link FrameGrouping} is selected.
     * <p>
     * Also sets the initial selection to the first grouping supplied by the
     * {@link #setAllowedFrameGroupings(FrameGrouping...)} and {@link #setAllowedThreadGroupings(ThreadGrouping...)}
     * methods.
     */
    private void bindGroupings()
    {
        if (threadGrouping == null
            || frameGrouping == null
            || threadGrouping.getItems().size() == 0
            || frameGrouping.getItems().size() == 0)
        {
            return;
        }

        threadGrouping.getSelectionModel().selectedItemProperty().addListener(
            (property, oldValue, newValue) ->
            {
                if (newValue != null)
                {
                    FrameGrouping other = frameGrouping.getSelectionModel().getSelectedItem();
                    if (other != null)
                    {
                        grouping.set(combine(newValue, other));
                    }
                }
            });

        frameGrouping.getSelectionModel().selectedItemProperty().addListener(
            (property, oldValue, newValue) ->
            {
                if (newValue != null)
                {
                    ThreadGrouping other = threadGrouping.getSelectionModel().getSelectedItem();
                    if (other != null)
                    {
                        grouping.set(combine(other, newValue));
                    }
                }
            });

        threadGrouping.getSelectionModel().select(0);
        frameGrouping.getSelectionModel().select(0);
    }
}
