package com.insightfullogic.honest_profiler.ports.javafx.util;

import java.util.function.Function;

public final class StyleUtil
{
    public static final String STYLE_NORMAL = "-fx-font-weight: normal; -fx-text-fill: black;";
    public static final String STYLE_WARNING = "-fx-font-weight: bold; -fx-text-fill: orange;";
    public static final String STYLE_ERROR = "-fx-font-weight: bold; -fx-text-fill: red;";

    public static final String STYLE_GOOD = "-fx-font-weight: bold; -fx-text-fill: green;";
    public static final String STYLE_DUBIOUS = "-fx-font-weight: bold; -fx-text-fill: orange;";
    public static final String STYLE_BAD = "-fx-font-weight: bold; -fx-text-fill: red;";

    public static final String STYLE_BOLD = "-fx-font-weight: bold;";

    public static final String STYLE_METHOD_NAME = "-fx-font-family: Courier New; -fx-font-weight: bold;";

    public static final Function<Number, String> intDiffStyler = number -> number.intValue() > 0
        ? STYLE_BAD : (number.intValue() < 0 ? STYLE_GOOD : STYLE_NORMAL);

    public static final Function<Number, String> longDiffStyler = number -> number.longValue() > 0
        ? STYLE_BAD : (number.longValue() < 0 ? STYLE_GOOD : STYLE_NORMAL);

    public static final Function<Number, String> doubleDiffStyler = number -> number
        .doubleValue() > 0 ? STYLE_BAD : (number.doubleValue() < 0 ? STYLE_GOOD : STYLE_NORMAL);

    public static final Function<String, String> stringDiffStyler = string -> string.startsWith("-")
        ? STYLE_GOOD : ("0".equals(string) || "0.00%".equals(string)) ? STYLE_NORMAL : STYLE_BAD;

    private StyleUtil()
    {
        // Private Utility Class Constructor
    }
}
