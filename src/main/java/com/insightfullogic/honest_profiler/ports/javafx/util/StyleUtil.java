package com.insightfullogic.honest_profiler.ports.javafx.util;

import java.util.function.Function;

/**
 * Utility class for working with JavaFX CSS.
 * <p>
 * See : <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html">JavaFX CSS Reference
 * Guide</a>
 */
public final class StyleUtil
{
    // Class Properties

    /** Styling for neutral items. */
    public static final String STYLE_NORMAL = "-fx-font-weight: normal; -fx-text-fill: black;";
    /** Styling for highlighting problematic items. */
    public static final String STYLE_WARNING = "-fx-font-weight: bold; -fx-text-fill: orange;";
    /** Styling for highlighting really problematic items. */
    public static final String STYLE_ERROR = "-fx-font-weight: bold; -fx-text-fill: red;";

    /** Styling for highlighting good results. */
    public static final String STYLE_GOOD = "-fx-font-weight: bold; -fx-text-fill: green;";
    /** Styling for highlighting dubious results. */
    public static final String STYLE_DUBIOUS = "-fx-font-weight: bold; -fx-text-fill: orange;";
    /** Styling for highlighting bad results. */
    public static final String STYLE_BAD = "-fx-font-weight: bold; -fx-text-fill: red;";

    /** Styling for highlighting. */
    public static final String STYLE_BOLD = "-fx-font-weight: bold;";

    /** Styling for method names. */
    public static final String STYLE_METHOD_NAME = "-fx-font-family: Courier New; -fx-font-weight: bold;";

    /**
     * Function which tests the integer value of the supplied number, and returns a style String :
     * <ul>
     * <li>{@link #STYLE_NORMAL} if the value is equal to 0</li>
     * <li>{@link #STYLE_BAD} if the value is higher than 0</li>
     * <li>{@link #STYLE_NORMAL} if the value is lower than 0</li>
     * </ul>
     * <p>
     * This can be used for styling comparisons where a higher number is worse.
     */
    public static final Function<Number, String> intDiffStyler = number -> number.intValue() > 0
        ? STYLE_BAD : (number.intValue() < 0 ? STYLE_GOOD : STYLE_NORMAL);

    /**
     * Function which tests the long value of the supplied number, and returns a style String :
     * <ul>
     * <li>{@link #STYLE_NORMAL} if the value is equal to 0</li>
     * <li>{@link #STYLE_BAD} if the value is higher than 0</li>
     * <li>{@link #STYLE_NORMAL} if the value is lower than 0</li>
     * </ul>
     * <p>
     * This can be used for styling comparisons where a higher number is worse.
     */
    public static final Function<Number, String> longDiffStyler = number -> number.longValue() > 0
        ? STYLE_BAD : (number.longValue() < 0 ? STYLE_GOOD : STYLE_NORMAL);

    /**
     * Function which tests the double value of the supplied number, and returns a style String :
     * <ul>
     * <li>{@link #STYLE_NORMAL} if the value is equal to 0</li>
     * <li>{@link #STYLE_BAD} if the value is higher than 0</li>
     * <li>{@link #STYLE_NORMAL} if the value is lower than 0</li>
     * </ul>
     * <p>
     * This can be used for styling comparisons where a higher number is worse.
     */
    public static final Function<Number, String> doubleDiffStyler = number -> number
        .doubleValue() > 0 ? STYLE_BAD : (number.doubleValue() < 0 ? STYLE_GOOD : STYLE_NORMAL);

    /**
     * Function which tests a String containing the representation of a numeric value, and returns a style String :
     * <ul>
     * <li>{@link #STYLE_NORMAL} if the value is equal to "0" or "0.00%"</li>
     * <li>{@link #STYLE_GOOD} if the value is higher than 0</li>
     * <li>{@link #STYLE_BAD} if the value is lower than 0</li>
     * </ul>
     */
    public static final Function<String, String> stringDiffStyler = string -> string.startsWith("-")
        ? STYLE_GOOD : ("0".equals(string) || "0.00%".equals(string)) ? STYLE_NORMAL : STYLE_BAD;

    // Instance Constructors

    /**
     * Private Utility Class Constructor.
     */
    private StyleUtil()
    {
        // Private Utility Class Constructor
    }
}
