package com.insightfullogic.honest_profiler.ports.javafx.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class Icon
{
    private static final String ICON_16_DIR = "/com/insightfullogic/honest_profiler/ports/javafx/icon/icon16/";

    public static final Image PLUS_16 = toImage(ICON_16_DIR + "plus-white.png");
    public static final Image MINUS_16 = toImage(ICON_16_DIR + "minus-white.png");

    public static final Image FUNNEL_16 = toImage(ICON_16_DIR + "funnel.png");
    public static final Image FUNNEL_ACTIVE_16 = toImage(ICON_16_DIR + "funnel--exclamation.png");

    public static final Image EXPAND_16 = toImage(ICON_16_DIR + "arrow-out.png");
    public static final Image COLLAPSE_16 = toImage(ICON_16_DIR + "arrow-in.png");

    public static final Image COMPARE_16 = toImage(ICON_16_DIR + "balance-unbalance.png");

    public static final Image EXPORT_16 = toImage(ICON_16_DIR + "document-import.png");

    public static final Image LIVE_16 = toImage(ICON_16_DIR + "monitor.png");
    public static final Image LOG_16 = toImage(ICON_16_DIR + "document-binary.png");

    public static final Image FREEZE_16 = toImage(ICON_16_DIR + "clock.png");
    public static final Image UNFREEZE_16 = toImage(ICON_16_DIR + "clock--exclamation.png");

    public static final Image VIEW_16 = toImage(ICON_16_DIR + "eye.png");
    public static final Image VIEW_ACTIVE_16 = toImage(ICON_16_DIR + "eye--exclamation.png");

    private static Image toImage(String resource)
    {
        return new Image(Icon.class.getResourceAsStream(resource));
    }

    public static ImageView viewFor(Image image)
    {
        return new ImageView(image);
    }

    private Icon()
    {
        // Empty Utility Class Constructor
    }
}
