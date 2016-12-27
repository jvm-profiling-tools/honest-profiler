package com.insightfullogic.honest_profiler.ports.javafx.controller.dialog;

import java.util.Optional;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

public interface DialogController<T>
{
    Dialog<T> getDialog();

    void setDialog(Dialog<T> dialog);

    void show();

    Optional<T> showAndWait();

    Callback<ButtonType, T> createResultHandler();

    void reset();
}
