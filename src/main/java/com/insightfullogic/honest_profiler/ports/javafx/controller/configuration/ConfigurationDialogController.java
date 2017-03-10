package com.insightfullogic.honest_profiler.ports.javafx.controller.configuration;

import com.insightfullogic.honest_profiler.ports.javafx.controller.dialog.AbstractDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.model.configuration.Configuration;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

public class ConfigurationDialogController extends AbstractDialogController<Configuration>
{
    // Instance Properties
    @FXML
    private Dialog<Configuration> dialog;

    @FXML
    private FormattingConfigurationController displayFormattingController;
    @FXML
    private FormattingConfigurationController exportFormattingController;

    // FXML Implementation

    @Override
    @FXML
    public void initialize()
    {
        super.initialize(dialog);
    }

    // Configuration Management Methods

    public void readConfiguration(Configuration configuration)
    {
        displayFormattingController
            .readConfiguration(configuration.getDisplayFormattingConfiguration());
        exportFormattingController
            .readConfiguration(configuration.getExportFormattingConfiguration());
    }

    // AbstractDialogController Implementation

    @Override
    public Callback<ButtonType, Configuration> createResultHandler()
    {
        return button -> new Configuration(
            displayFormattingController.getConfiguration(),
            exportFormattingController.getConfiguration());
    }

    @Override
    public void reset()
    {
        // NOOP
    }

    @Override
    protected void initializeInfoText()
    {
        // NOOP
    }

    @Override
    protected void initializeHandlers()
    {
        // NOOP
    }
}
