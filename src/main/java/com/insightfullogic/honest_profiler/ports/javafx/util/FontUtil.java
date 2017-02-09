package com.insightfullogic.honest_profiler.ports.javafx.util;

import static javafx.scene.text.Font.loadFont;

import javafx.scene.text.Font;

/**
 * Utility class for managing {@link Font}s.
 */
public final class FontUtil
{
    // Class Properties

    private static final String PATH_OPENSANS_REGULAR = "/com/insightfullogic/honest_profiler/ports/javafx/font/OpenSans-Regular.ttf";
    private static final String PATH_OPENSANS_BOLD = "/com/insightfullogic/honest_profiler/ports/javafx/font/OpenSans-Bold.ttf";

    private static Font OPENSANS_REGULAR;
    private static Font OPENSANS_BOLD;

    // Class Methods

    /**
     * Loads the fonts included in the resources.
     * <p>
     * @param c a {@link Class} used for resolving the font URLs.
     */
    public static void initialize(Class<?> c)
    {
        OPENSANS_REGULAR = loadFont(c.getResource(PATH_OPENSANS_REGULAR).toExternalForm(), 10);
        OPENSANS_BOLD = loadFont(c.getResource(PATH_OPENSANS_BOLD).toExternalForm(), 10);
    }

    /**
     * Returns the Open Sans Regular {@link Font}.
     * <p>
     * @return the Open Sans Regular {@link Font}
     */
    public static final Font openSansRegular()
    {
        return OPENSANS_REGULAR;
    }

    /**
     * Returns the Open Sans Bold {@link Font}.
     * <p>
     * @return the Open Sans Bold {@link Font}
     */
    public static final Font openSansBold()
    {
        return OPENSANS_BOLD;
    }

    // Instance Constructors

    /**
     * Private Utility Class Constructor.
     */
    private FontUtil()
    {
        // Private Utility Class Constructor
    }
}
