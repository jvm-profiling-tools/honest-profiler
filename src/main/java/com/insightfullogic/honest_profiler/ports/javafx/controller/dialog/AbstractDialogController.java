package com.insightfullogic.honest_profiler.ports.javafx.controller.dialog;

import java.util.Optional;

import com.insightfullogic.honest_profiler.ports.javafx.controller.AbstractController;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

/**
 * Abstract superclass for DialogController implementations.
 * <p>
 * @param <R> the return type of the {@link Dialog}
 */
public abstract class AbstractDialogController<R> extends AbstractController
    implements DialogController<R>
{
    private Dialog<R> dialog;

    @Override
    public Dialog<R> getDialog()
    {
        return this.dialog;
    }

    @Override
    public void setDialog(Dialog<R> dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public void show()
    {
        this.dialog.show();
    }

    @Override
    public Optional<R> showAndWait()
    {
        return this.dialog.showAndWait();
    }

    @Override
    public abstract Callback<ButtonType, R> createResultHandler();

    @Override
    public abstract void reset();
}
