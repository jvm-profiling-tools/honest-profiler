package com.insightfullogic.honest_profiler.ports.javafx.controller.configuration;

import static com.insightfullogic.honest_profiler.ports.javafx.model.configuration.FormattingConfiguration.ALLOWED_UNITS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.util.Arrays.asList;
import static javafx.beans.binding.Bindings.and;
import static javafx.beans.binding.Bindings.or;

import java.time.temporal.ChronoUnit;
import java.util.List;

import com.insightfullogic.honest_profiler.ports.javafx.controller.AbstractController;
import com.insightfullogic.honest_profiler.ports.javafx.model.configuration.FormattingConfiguration;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class FormattingConfigurationController extends AbstractController
{
    // Class Properties

    private static final List<Character> ALLOWED_SEPARATORS = asList(
        new Character(','),
        new Character('.'));

    // Instance Properties

    @FXML
    private CheckBox showThousandsSeparator;
    @FXML
    private ChoiceBox<Character> thousandsSeparator;
    @FXML
    private ChoiceBox<Integer> fractionDigitsNumber;
    @FXML
    private ChoiceBox<Character> decimalSeparator;
    @FXML
    private RadioButton showAsPercentage;
    @FXML
    private ToggleGroup percentDisplay;
    @FXML
    private CheckBox showPercentSign;
    @FXML
    private CheckBox spaceBeforePercentSign;
    @FXML
    private RadioButton showAsFraction;
    @FXML
    private ChoiceBox<ChronoUnit> timeUnit;
    @FXML
    private ChoiceBox<Integer> fractionDigitsTime;
    @FXML
    private CheckBox showTimeUnits;

    @Override
    @FXML
    public void initialize()
    {
        super.initialize();

        populateChoices();
    }

    // Instance Accessors

    /**
     * Returns a {@link FormattingConfiguration} reflecting the currently selected settings.
     *
     * @return a {@link FormattingConfiguration} reflecting the currently selected settings
     */
    public FormattingConfiguration getConfiguration()
    {
        return new FormattingConfiguration(
            showThousandsSeparator.isSelected(),
            thousandsSeparator.getValue(),
            decimalSeparator.getValue(),
            fractionDigitsNumber.getValue(),
            fractionDigitsTime.getValue(),
            showAsPercentage.isSelected(),
            showPercentSign.isSelected(),
            spaceBeforePercentSign.isSelected(),
            timeUnit.getValue(),
            showTimeUnits.isSelected());
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        // TODO Auto-generated method stub
    }

    @Override
    protected void initializeHandlers()
    {
        // Thousands separator is irrelevant if no thousands separator is shown.
        thousandsSeparator.disableProperty().bind(showThousandsSeparator.selectedProperty().not());

        // Decimal separator is irrelevant if no fractional numbers are shown.
        decimalSeparator.disableProperty().bind(
            and(
                fractionDigitsNumber.getSelectionModel().selectedItemProperty().isEqualTo(0),
                fractionDigitsTime.getSelectionModel().selectedItemProperty().isEqualTo(0)));

        // If a new thousands separator is selected, switch the decimal separator. The logic here exploits the fact that
        // in the current code, only 2 separators are selectable, and that they are added to the choice boxen in the
        // same order.
        thousandsSeparator.getSelectionModel().selectedIndexProperty().addListener(
            (property, oldValue, newValue) -> decimalSeparator.getSelectionModel()
                .select(1 - newValue.intValue()));

        // If a new decimal separator is selected, switch the thousands separator. The logic here exploits the fact that
        // in the current code, only 2 separators are selectable, and that they are added to the choice boxen in the
        // same order.
        decimalSeparator.getSelectionModel().selectedIndexProperty().addListener(
            (property, oldValue, newValue) -> thousandsSeparator.getSelectionModel()
                .select(1 - newValue.intValue()));

        // Show percent sign is irrelevant if percentages are displayed as fractions.
        showPercentSign.disableProperty().bind(showAsPercentage.selectedProperty().not());

        // Space before percent sign is irrelevant if no percent sign is displayed in the first place.
        spaceBeforePercentSign.disableProperty().bind(or(
            showAsPercentage.selectedProperty().not(),
            showPercentSign.selectedProperty().not()));

        // Fraction digits for time periods are irrelevant when showing nanoseconds, since this is the smallest time
        // unit recorded.
        fractionDigitsTime.disableProperty()
            .bind(timeUnit.getSelectionModel().selectedItemProperty().isEqualTo(NANOS));
    }

    // Configuration Management Methods

    /**
     * Updates the selections in the UI based on the provided {@link FormattingConfiguration}.
     *
     * @param configuration the {@link FormattingConfiguration} whose settings should be reflected in the UI
     */
    public void readConfiguration(FormattingConfiguration configuration)
    {
        showThousandsSeparator.setSelected(configuration.isShowThousandsSeparator());
        thousandsSeparator.getSelectionModel().select(configuration.getThousandsSeparator());
        decimalSeparator.getSelectionModel().select(configuration.getDecimalSeparator());
        fractionDigitsNumber.getSelectionModel().select(configuration.getFractionDigitsNumber());
        fractionDigitsTime.getSelectionModel().select(configuration.getFractionDigitsTime());
        showAsPercentage.setSelected(configuration.isShowAsPercentage());
        showPercentSign.setSelected(configuration.isShowPercentSign());
        spaceBeforePercentSign.setSelected(configuration.isSpaceBeforePercentSign());
        timeUnit.getSelectionModel().select(configuration.getTimeUnit());
        showTimeUnits.setSelected(configuration.isShowTimeUnits());
    }

    // Population Methods

    /**
     * Populate all {@link ChoiceBox}es.
     */
    private void populateChoices()
    {
        populateSeparator(thousandsSeparator);
        populateSeparator(decimalSeparator);
        pupulateTimeUnit();
        populateFractionDigits(fractionDigitsNumber);
        populateFractionDigits(fractionDigitsTime);
    }

    /**
     * Populates a {@link ChoiceBox} with the allowed separator characters.
     *
     * @param choiceBox the {@link ChoiceBox} to be populated
     */
    private void populateSeparator(ChoiceBox<Character> choiceBox)
    {
        for (Character separator : ALLOWED_SEPARATORS)
        {
            choiceBox.getItems().add(separator);
        }
    }

    /**
     * Populates the Time Unit {@link ChoiceBox} with the allowed {@link ChronoUnit}s.
     */
    private void pupulateTimeUnit()
    {
        for (ChronoUnit unit : ALLOWED_UNITS)
        {
            timeUnit.getItems().add(unit);
        }
    }

    /**
     * Populates a {@link ChoiceBox} with the allowed numbers of fraction digits.
     *
     * @param choiceBox the {@link ChoiceBox} to be populated
     */
    private void populateFractionDigits(ChoiceBox<Integer> choiceBox)
    {
        // Why 0-6 ? 1 ms == 1.000 µs == 1.000.000 ns.
        for (int i = 0; i < 7; i++)
        {
            choiceBox.getItems().add(i);
        }
    }
}
