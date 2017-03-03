package com.insightfullogic.honest_profiler.ports.javafx.controller.dialog;

import java.util.Optional;

import com.insightfullogic.honest_profiler.ports.javafx.controller.AbstractController;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

/**
 * Abstract superclass for DialogController implementations.
 * <p>
 *
 * @param <R> the return type of the {@link Dialog}
 */
public abstract class AbstractDialogController<R> extends AbstractController
{
    private Dialog<R> dialog;

    /**
     * This method must be called by subclasses in their FXML initialize().
     * <p>
     * The idea is to streamline similar tasks happening in the initialization method, and encourage decluttering of the
     * initialize() methods by extracting similar tasks to separate methods.
     */
    protected void initialize(Dialog<R> dialog)
    {
        super.initialize();

        this.dialog = dialog;
        dialog.setResultConverter(createResultHandler());
    }

    public void show()
    {
        this.dialog.show();
    }

    public Optional<R> showAndWait()
    {
        return this.dialog.showAndWait();
    }

    public abstract Callback<ButtonType, R> createResultHandler();

    public abstract void reset();
}
