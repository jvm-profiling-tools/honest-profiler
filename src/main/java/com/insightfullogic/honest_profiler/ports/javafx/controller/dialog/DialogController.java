package com.insightfullogic.honest_profiler.ports.javafx.controller.dialog;

import java.util.Optional;

import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.util.DialogUtil;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Callback;

/**
 * Interface for controllers for {@link Dialog}s.
 * <p>
 * NOTE : May be refactored soon, since the get/setDialog can be avoided through injection from FXML.
 * <p>
 * @param <R> the return type of the {@link Dialog}
 */
public interface DialogController<R>
{
    /**
     * Set the {@link ApplicationContext}. The {@link ApplicationContext} is shared by all controllers in the
     * application, for sharing application resources.
     * <p>
     * @param applicationContext the {@link ApplicationContext} for the application
     */
    void setApplicationContext(ApplicationContext applicationContext);

    /**
     * Returns the associated {@link Dialog} object controlled by this controller.
     * <p>
     * @return the associated {@link Dialog} object controlled by this controller
     */
    Dialog<R> getDialog();

    /**
     * Sets the {@link Dialog} object controlled by this controller instance.
     * <p>
     * @param dialog the {@link Dialog} object controlled by this controller instance
     */
    void setDialog(Dialog<R> dialog);

    /**
     * Shows the {@link Dialog}.
     */
    void show();

    /**
     * Shows the {@link Dialog} and waits until the {@link Dialog} is closed.
     * <p>
     * @return an {@link Optional} wrapping the potential result from the {@link Dialog}
     */
    Optional<R> showAndWait();

    /**
     * Returns the resulthandler which generates the result based on the state of the {@link Dialog} and the button used
     * to close the {@link Dialog}.
     * <p>
     * Subclasses should implement this. The {@link DialogUtil#newDialog(ApplicationContext, String, String, boolean)}
     * method will use it to install the resulthandler in the {@link Dialog}.
     * <p>
     * @return the resulthandler for generating the result from the {@link Dialog}
     */
    Callback<ButtonType, R> createResultHandler();

    /**
     * Sets the {@link Dialog} back to a blank, "initial" state.
     * <p>
     * Subclasses should implement this.
     */
    void reset();
}
