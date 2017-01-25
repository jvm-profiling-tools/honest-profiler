package com.insightfullogic.honest_profiler.ports.javafx.util;

import static javafx.scene.text.Font.loadFont;

import javafx.scene.text.Font;

public final class FontUtil
{

    private static final String PATH_OPENSANS_REGULAR = "/com/insightfullogic/honest_profiler/ports/javafx/font/OpenSans-Regular.ttf";
    private static final String PATH_OPENSANS_BOLD = "/com/insightfullogic/honest_profiler/ports/javafx/font/OpenSans-Bold.ttf";

    private static Font OPENSANS_REGULAR;
    private static Font OPENSANS_BOLD;

    public static void initialize(Class<?> c)
    {
        OPENSANS_REGULAR = loadFont(c.getResource(PATH_OPENSANS_REGULAR).toExternalForm(), 10);
        OPENSANS_BOLD = loadFont(c.getResource(PATH_OPENSANS_BOLD).toExternalForm(), 10);
    }

    public static final Font openSansRegular()
    {
        return OPENSANS_REGULAR;
    }

    public static final Font openSansBold()
    {
        return OPENSANS_BOLD;
    }

    private FontUtil()
    {
        // Private Utility Class Constructor
    }
}
