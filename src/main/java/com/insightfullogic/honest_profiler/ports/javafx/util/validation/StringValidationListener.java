package com.insightfullogic.honest_profiler.ports.javafx.util.validation;

import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.STYLE_ERROR;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.STYLE_NORMAL;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.insightfullogic.honest_profiler.ports.javafx.util.handle.ChangeListenerHandle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Listener which validates a String in a {@link Node}, and changes the styling of the {@link Node} and the associated
 * {@link Button} state based on the validity of the input. Typically this is the "OK" {@link Button} is associated.
 */
public class StringValidationListener implements ChangeListener<String>
{
    // Instance Properties

    private ChangeListenerHandle<String> handle;

    private Node inputNode;
    private List<Button> buttons;
    private Predicate<String> test;

    // Instance Constructors

    /**
     * Constructor specifying the source {@link Node}, the predicate for testing the validity, and any {@link Button}s
     * which should be disabled if the validity test fails.
     * <p>
     * @param inputNode the source {@link Node}
     * @param test the predicate for testing the validity of the String value
     * @param buttons the {@link Button}s which should be disabled if the validity test fails
     */
    public StringValidationListener(Node inputNode, Predicate<String> test, Button... buttons)
    {
        this.inputNode = inputNode;
        this.test = test;
        this.buttons = buttons == null ? new ArrayList<>() : asList(buttons);
    }

    // Management Methods

    /**
     * Create a {@link ChangeListenerHandle} for the input {@link Node} and the specified text {@link ObservableValue},
     * and attaches it.
     * <p>
     * @param value the {@link ObservableValue} this Listener is added to
     * @param inputNode the new input {@link Node} being validated by the StringValidationListener
     * @return a new {@link ChangeListenerHandle} for this StringValidationListener and the specified
     *         {@link ObservableValue}
     */
    public ChangeListenerHandle<String> attach(ObservableValue<String> value, Node inputNode)
    {
        this.inputNode = inputNode;

        handle = new ChangeListenerHandle<String>(value, this);
        handle.attach();

        return handle;
    }

    // ChangeListener Implementation

    @Override
    public void changed(ObservableValue<? extends String> value, String oldValue, String newValue)
    {
        // An empty value is styled as valid, but the associated Button is disabled.
        if ((newValue == null) || newValue.isEmpty())
        {
            inputNode.setStyle(STYLE_NORMAL);
            setDisabled(true);
            return;
        }

        try
        {
            // Input valid => enable Button and style as valid.
            if (test.test(newValue))
            {
                inputNode.setStyle(STYLE_NORMAL);
                setDisabled(false);
                return;
            }
        }
        catch (Throwable t)
        {
            // Do nothing, treat as failed test.
        }

        // Input invalid => disable Button and style as error.
        inputNode.setStyle(STYLE_ERROR);
        setDisabled(true);
    }

    /**
     * Disable or enable the {@link Button}s.
     * <p>
     * @param disable a boolean indicating whether the {@link Button}s should be disabled
     */
    private void setDisabled(boolean disable)
    {
        buttons.forEach(button -> button.setDisable(disable));
    }
}
