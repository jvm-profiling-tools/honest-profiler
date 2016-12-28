package com.insightfullogic.honest_profiler.ports.javafx.controller.dialog;

import java.util.Optional;

import com.insightfullogic.honest_profiler.ports.javafx.controller.AbstractController;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

public abstract class AbstractDialogController<T> extends AbstractController
    implements DialogController<T>
{
    private Dialog<T> dialog;

    @Override
    public Dialog<T> getDialog()
    {
        return this.dialog;
    }

    @Override
    public void setDialog(Dialog<T> dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public void show()
    {
        this.dialog.show();
    }

    @Override
    public Optional<T> showAndWait()
    {
        return this.dialog.showAndWait();
    }

    @Override
    public abstract Callback<ButtonType, T> createResultHandler();

    @Override
    public abstract void reset();
}
