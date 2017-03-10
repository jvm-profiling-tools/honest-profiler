package com.insightfullogic.honest_profiler.ports.javafx.model.configuration;

import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Locale.getDefault;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FormattingConfiguration
{
    // Class Properties

    /**
     * Only for use when creating the default configuration, to set the default separators to those of the default
     * {@link Locale}, to avoid annoying people in other locations than myself.
     */
    private static DecimalFormatSymbols DEFAULT_SYMBOLS = new DecimalFormatSymbols(getDefault());

    public static final List<ChronoUnit> ALLOWED_UNITS = asList(NANOS, MICROS, MILLIS, SECONDS);

    public static final FormattingConfiguration DEFAULT_DISPLAY_CONFIGURATION = new FormattingConfiguration(
        true,
        DEFAULT_SYMBOLS.getGroupingSeparator(),
        DEFAULT_SYMBOLS.getDecimalSeparator(),
        2,
        0,
        true,
        true,
        true,
        MILLIS,
        false);

    public static final FormattingConfiguration DEFAULT_EXPORT_CONFIGURATION = new FormattingConfiguration(
        false,
        DEFAULT_SYMBOLS.getGroupingSeparator(),
        DEFAULT_SYMBOLS.getDecimalSeparator(),
        2,
        0,
        false,
        false,
        true,
        MILLIS,
        false);

    private static final Map<ChronoUnit, String> UNIT_NAMES = new HashMap<>();

    // Class Constructors

    static
    {
        UNIT_NAMES.put(NANOS, "ns");
        UNIT_NAMES.put(MICROS, "µs");
        UNIT_NAMES.put(MILLIS, "ms");
        UNIT_NAMES.put(SECONDS, "s");
    }

    // Instance Properties

    private boolean showThousandsSeparator;
    private Character thousandsSeparator;

    private Character decimalSeparator;
    private int fractionDigitsNumber;
    private int fractionDigitsTime;

    private boolean showAsPercentage;
    private boolean showPercentSign;
    private boolean spaceBeforePercentSign;

    private ChronoUnit timeUnit;
    private boolean showTimeUnits;

    // Instance Constructors

    /**
     * Constructor specifying all settings.
     *
     * @param showThousandsSeparator
     * @param thousandsSeparator
     * @param decimalSeparator
     * @param fractionDigitsNumber
     * @param fractionDigitsTime
     * @param showAsPercentage
     * @param showPercentSign
     * @param spaceBeforePercentSign
     * @param timeUnit
     * @param showTimeUnits
     */
    public FormattingConfiguration(boolean showThousandsSeparator,
                                   Character thousandsSeparator,
                                   Character decimalSeparator,
                                   int fractionDigitsNumber,
                                   int fractionDigitsTime,
                                   boolean showAsPercentage,
                                   boolean showPercentSign,
                                   boolean spaceBeforePercentSign,
                                   ChronoUnit timeUnit,
                                   boolean showTimeUnits)
    {
        super();

        this.showThousandsSeparator = showThousandsSeparator;
        this.thousandsSeparator = thousandsSeparator;
        this.decimalSeparator = decimalSeparator;
        this.fractionDigitsNumber = fractionDigitsNumber;
        this.fractionDigitsTime = fractionDigitsTime;
        this.showAsPercentage = showAsPercentage;
        this.showPercentSign = showPercentSign;
        this.spaceBeforePercentSign = spaceBeforePercentSign;
        this.timeUnit = timeUnit;
        this.showTimeUnits = showTimeUnits;
    }

    // Instance Accessors

    /**
     * Returns a boolean indicating whether the thousands separator should be displayed.
     *
     * @return a boolean indicating whether the thousands separator should be displayed
     */
    public boolean isShowThousandsSeparator()
    {
        return showThousandsSeparator;
    }

    /**
     * Returns the character used as thousands separator.
     *
     * @return the character used as thousands separator
     */
    public Character getThousandsSeparator()
    {
        return thousandsSeparator;
    }

    /**
     * Returns the character used as decimal separator.
     *
     * @return the character used as decimal separator
     */
    public Character getDecimalSeparator()
    {
        return decimalSeparator;
    }

    /**
     * Returns the number of digits to be displayed after the decimal separator when displaying numbers other than
     * integers or time periods.
     *
     * @return the number of digits to be displayed after the decimal separator
     */
    public int getFractionDigitsNumber()
    {
        return fractionDigitsNumber;
    }

    /**
     * Returns the number of digits to be displayed after the decimal separator when displaying time periods.
     *
     * @return the number of digits to be displayed after the decimal separator
     */
    public int getFractionDigitsTime()
    {
        return fractionDigitsTime;
    }

    /**
     * Returns a boolean indicating whether a percentage should be shown as a percent (100-based) or a fraction (where 1
     * == 100 %).
     *
     * @return a boolean indicating whether a percentage should be shown as a percent
     */
    public boolean isShowAsPercentage()
    {
        return showAsPercentage;
    }

    /**
     * Returns a boolean indicating whether the percent sign should be shown in percentages. This setting only is
     * applicable if {@link #isShowAsPercentage()} returns true.
     *
     * @return a boolean indicating whether the percent sign should be shown in percentages
     */
    public boolean isShowPercentSign()
    {
        return showPercentSign;
    }

    /**
     * Returns a boolean indicating whether the percentage and percent sign should be separated by a space. This setting
     * only is applicable if {@link #isShowAsPercentage()} and {@link #isShowPercentSign()} both return true.
     *
     * @return a boolean indicating whether the percentage and percent sign should be separated by a space
     */
    public boolean isSpaceBeforePercentSign()
    {
        return spaceBeforePercentSign;
    }

    /**
     * Returns a {@link ChronoUnit} indicating the time units to be used when displaying time periods.
     *
     * @return a {@link ChronoUnit} indicating the time units to be used when displaying time periods
     */
    public ChronoUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * Returns a boolean indicating whether the time units should be shown when displaying time periods.
     *
     * @return a boolean indicating whether the time units should be shown when displaying time periods
     */
    public boolean isShowTimeUnits()
    {
        return showTimeUnits;
    }

    // Formatter Generation Methods

    /**
     * Returns a {@link DecimalFormat} for formatting integer numbers, based on the configured settings.
     *
     * @return a {@link DecimalFormat} for formatting integer numbers
     */
    public DecimalFormat getIntegerFormatter()
    {
        return new DecimalFormat(integral(), symbols());
    }

    /**
     * Returns a {@link DecimalFormat} for formatting {@link Number}s, based on the configured settings.
     *
     * @return a {@link DecimalFormat} for formatting {@link Number}s
     */
    public DecimalFormat getNumberFormatter()
    {
        return new DecimalFormat(integral() + fractional(fractionDigitsNumber), symbols());
    }

    /**
     * Returns a {@link DecimalFormat} for formatting percentages, based on the configured settings.
     *
     * @return a {@link DecimalFormat} for formatting percentages
     */
    public DecimalFormat getPercentFormatter()
    {
        return new DecimalFormat(
            integral() + fractional(fractionDigitsNumber) + percent(),
            symbols());
    }

    /**
     * Returns a {@link DecimalFormat} for formatting time periods, based on the configured settings.
     *
     * @return a {@link DecimalFormat} for formatting time periods
     */
    public DecimalFormat getTimeFormatter()
    {
        return new DecimalFormat(integral() + fractional(fractionDigitsTime) + time(), symbols());
    }

    /**
     * Returns the {@link DecimalFormatSymbols} for {@link DecimalFormat} creation, based on the configured settings.
     *
     * @return the {@link DecimalFormatSymbols} for {@link DecimalFormat} creation
     */
    private DecimalFormatSymbols symbols()
    {
        DecimalFormatSymbols result = new DecimalFormatSymbols();
        result.setDecimalSeparator(decimalSeparator);
        result.setGroupingSeparator(thousandsSeparator);
        return result;
    }

    /**
     * Returns the portion of the formatting pattern corresponding to the integer digits.
     *
     * @return the portion of the formatting pattern corresponding to the integerdigits
     */
    private String integral()
    {
        return showThousandsSeparator ? "#,##0" : "0";
    }

    /**
     * Returns the portion of the formatting pattern corresponding to the decimal separator and the fraction digits.
     *
     * @param fractionDigits the number of fraction digits to be displayed
     * @return the portion of the formatting pattern corresponding to the decimal separator and the fraction digits
     */
    private String fractional(int fractionDigits)
    {
        if (fractionDigits == 0)
        {
            return "";
        }

        StringBuilder result = new StringBuilder(".");
        for (int i = 0; i < fractionDigits; i++)
        {
            result.append("0");
        }
        return result.toString();
    }

    /**
     * Returns the portion of the formatting pattern corresponding to the percent symbol.
     *
     * @return the portion of the formatting pattern corresponding to the percent symbol
     */
    private String percent()
    {
        if (!showAsPercentage || !showPercentSign)
        {
            return "";
        }
        return spaceBeforePercentSign ? " %" : "%";
    }

    /**
     * Returns the portion of the formatting pattern corresponding to the time unit.
     *
     * @return the portion of the formatting pattern corresponding to the time unit
     */
    private String time()
    {
        if (!showTimeUnits)
        {
            return "";
        }

        return " " + UNIT_NAMES.get(timeUnit);
    }
}
