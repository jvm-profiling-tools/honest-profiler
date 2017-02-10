package com.insightfullogic.honest_profiler.ports.javafx.controller.filter;

import static com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType.DOUBLE;
import static com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType.SHARE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ConversionUtil.getStringConverterForType;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_CHOICE_COMPARISONOPERATOR;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_CHOICE_FILTERTARGET;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_FILTERVALUE;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.aggregation.filter.Comparison;
import com.insightfullogic.honest_profiler.core.aggregation.filter.FilterItem;
import com.insightfullogic.honest_profiler.core.aggregation.filter.Target;
import com.insightfullogic.honest_profiler.core.aggregation.filter.ValueType;
import com.insightfullogic.honest_profiler.core.aggregation.result.ItemType;
import com.insightfullogic.honest_profiler.ports.javafx.controller.dialog.AbstractDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.util.handle.ChangeListenerHandle;
import com.insightfullogic.honest_profiler.ports.javafx.util.validation.StringValidationListener;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class FilterCreationDialogController<T> extends AbstractDialogController<FilterItem<T, ?>>
{
    private final Map<ValueType, StringValidationListener> validatorMap = new HashMap<>();

    @FXML
    private Dialog<FilterItem<T, ?>> dialog;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private ChoiceBox<Target> target;
    @FXML
    private ChoiceBox<Comparison> comparison;
    @FXML
    private TextField value;
    @FXML
    private Label percentLabel;

    private ChangeListenerHandle<String> currentListenerHandle;

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(dialog);

        // Validator Map Population
        Button okButton = (Button)dialogPane.lookupButton(OK);
        for (ValueType type : ValueType.values())
        {
            validatorMap
                .put(type, new StringValidationListener(value, type.getValidator(), okButton));
        }
    }

    public void setItemType(ItemType itemType)
    {
        target.getItems().clear();
        target.getItems().addAll(itemType.getAllowedTargets());
        target.getSelectionModel().select(0);
    }

    // DialogController Implementation

    @Override
    public Callback<ButtonType, FilterItem<T, ?>> createResultHandler()
    {
        return buttonType -> buttonType == CANCEL ? null : new FilterItem<>(
            target.getSelectionModel().getSelectedItem(),
            comparison.getSelectionModel().getSelectedItem(),
            target.getSelectionModel().getSelectedItem().getType().getInterpreter()
                .apply(value.getText()));
    }

    @Override
    public void reset()
    {
        dialogPane.lookupButton(OK).setDisable(true);
        target.getSelectionModel().select(0);
        resetSelection();
    }

    // Helper Methods

    private void resetSelection()
    {
        comparison.getSelectionModel().select(0);
        value.clear();
    }

    private void switchTarget(Target target)
    {
        comparison.getItems().clear();
        comparison.getItems().addAll(target.getType().getAllowedComparisons());

        if (currentListenerHandle != null)
        {
            currentListenerHandle.detach();
        }

        resetSelection();

        boolean isPct = target.getType() == SHARE || target.getType() == DOUBLE;
        percentLabel.setVisible(isPct);
        percentLabel.setManaged(isPct);

        currentListenerHandle = validatorMap.get(target.getType())
            .attach(value.textProperty(), value);
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(target, INFO_CHOICE_FILTERTARGET);
        info(comparison, INFO_CHOICE_COMPARISONOPERATOR);
        info(value, INFO_INPUT_FILTERVALUE);
    }

    @Override
    protected void initializeHandlers()
    {
        dialog.setOnShown(event -> reset());

        target.getSelectionModel().selectedItemProperty()
            .addListener((property, oldValue, newValue) -> switchTarget(newValue));

        // Choice Display
        target.setConverter(getStringConverterForType(Target.class));
        comparison.setConverter(getStringConverterForType(Comparison.class));
    }
}
