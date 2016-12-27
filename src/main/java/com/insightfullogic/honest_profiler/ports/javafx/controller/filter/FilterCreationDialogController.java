package com.insightfullogic.honest_profiler.ports.javafx.controller.filter;

import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.STRING;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.THREAD_SAMPLE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType.TIME_SHARE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ConversionUtil.getStringConverterForType;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.ports.javafx.controller.dialog.AbstractDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.ComparisonType;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterItem;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterType;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.TargetType;
import com.insightfullogic.honest_profiler.ports.javafx.util.handle.ChangeListenerHandle;
import com.insightfullogic.honest_profiler.ports.javafx.util.validation.StringValidationListener;
import com.insightfullogic.honest_profiler.ports.javafx.util.validation.ValidateDoubleListener;
import com.insightfullogic.honest_profiler.ports.javafx.util.validation.ValidateStringListener;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class FilterCreationDialogController extends AbstractDialogController<FilterItem>
{
    private final Map<FilterType, StringValidationListener> validatorMap = new HashMap<>();

    @FXML
    private DialogPane dialogPane;
    @FXML
    private ChoiceBox<FilterType> type;
    @FXML
    private ChoiceBox<TargetType> target;
    @FXML
    private ChoiceBox<ComparisonType> comparison;
    @FXML
    private TextField value;
    @FXML
    private Label percentLabel;

    private ChangeListenerHandle<String> currentListenerHandle;

    @FXML
    public void initialize()
    {
        // Validator Map Population
        Button okButton = (Button) dialogPane.lookupButton(OK);
        validatorMap.put(THREAD_SAMPLE, new ValidateDoubleListener(value, 0.0, 100.0, okButton));
        validatorMap.put(TIME_SHARE, new ValidateDoubleListener(value, 0.0, 100.0, okButton));
        validatorMap.put(STRING, new ValidateStringListener(value, okButton));

        // Choice Display
        type.setConverter(getStringConverterForType(FilterType.class));
        target.setConverter(getStringConverterForType(TargetType.class));
        comparison.setConverter(getStringConverterForType(ComparisonType.class));

        // Choice Interaction
        type.getSelectionModel().selectedItemProperty()
            .addListener((property, oldValue, newValue) -> switchFilterType(newValue));
    }

    public void addAllowedFilterTypes(FilterType... filterType)
    {
        type.getItems().addAll(filterType);
        reset();
    }

    // DialogController Implementation

    @Override
    public Callback<ButtonType, FilterItem> createResultHandler()
    {
        return buttonType -> buttonType == CANCEL ? null
            : new FilterItem(
                type.getSelectionModel().getSelectedItem(),
                comparison.getSelectionModel().getSelectedItem(),
                target.getSelectionModel().getSelectedItem(),
                value.getText());
    }

    @Override
    public void reset()
    {
        dialogPane.lookupButton(OK).setDisable(true);
        type.getSelectionModel().select(0);
        resetSelection();
    }

    // Helper Methods

    private void resetSelection()
    {
        target.getSelectionModel().select(0);
        comparison.getSelectionModel().select(0);
        value.clear();
    }

    private void switchFilterType(FilterType filterType)
    {
        target.getItems().clear();
        target.getItems().addAll(filterType.getAllowedTargets());

        comparison.getItems().clear();
        comparison.getItems().addAll(filterType.getAllowedComparisons());

        if (currentListenerHandle != null)
        {
            currentListenerHandle.detach();
        }

        resetSelection();

        percentLabel.setVisible(filterType == THREAD_SAMPLE || filterType == TIME_SHARE);
        percentLabel.setManaged(filterType == THREAD_SAMPLE || filterType == TIME_SHARE);

        currentListenerHandle = validatorMap.get(filterType).attach(value.textProperty(), value);
    }
}
