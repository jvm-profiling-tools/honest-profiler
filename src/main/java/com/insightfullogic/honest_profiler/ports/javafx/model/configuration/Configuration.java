package com.insightfullogic.honest_profiler.ports.javafx.model.configuration;

import static com.insightfullogic.honest_profiler.ports.javafx.model.configuration.FormattingConfiguration.DEFAULT_DISPLAY_CONFIGURATION;
import static com.insightfullogic.honest_profiler.ports.javafx.model.configuration.FormattingConfiguration.DEFAULT_EXPORT_CONFIGURATION;

public class Configuration
{
    // Class Properties

    public static final Configuration DEFAULT_CONFIGURATION = new Configuration(
        DEFAULT_DISPLAY_CONFIGURATION,
        DEFAULT_EXPORT_CONFIGURATION);

    // Instance Properties

    private FormattingConfiguration displayFormattingConfiguration;
    private FormattingConfiguration exportFormattingConfiguration;

    // Instance Constructors

    public Configuration(FormattingConfiguration displayFormattingConfiguration,
                         FormattingConfiguration exportFormattingConfiguration)
    {
        super();

        this.displayFormattingConfiguration = displayFormattingConfiguration;
        this.exportFormattingConfiguration = exportFormattingConfiguration;
    }

    // Instance Accessors

    public FormattingConfiguration getDisplayFormattingConfiguration()
    {
        return displayFormattingConfiguration;
    }

    public FormattingConfiguration getExportFormattingConfiguration()
    {
        return exportFormattingConfiguration;
    }
}
