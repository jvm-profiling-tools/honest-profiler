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

public class StringValidationListener implements ChangeListener<String>
{

    private ChangeListenerHandle<String> handle;

    private Node inputNode;
    private List<Button> buttons;
    private Predicate<String> test;

    public StringValidationListener(Node inputNode, Predicate<String> test, Button... buttons)
    {
        this.inputNode = inputNode;
        this.test = test;
        this.buttons = buttons == null ? new ArrayList<>() : asList(buttons);
    }

    public void setInputNode(Node inputNode)
    {
        this.inputNode = inputNode;
    }

    public void detach()
    {
        if (handle != null)
        {
            handle.detach();
        }
    }

    public ChangeListenerHandle<String> attach(ObservableValue<String> value, Node inputNode)
    {
        this.inputNode = inputNode;

        handle = new ChangeListenerHandle<String>(value, this);
        handle.attach();

        return handle;
    }

    @Override
    public void changed(ObservableValue<? extends String> value, String oldValue, String newValue)
    {
        if ((newValue == null) || newValue.isEmpty())
        {
            inputNode.setStyle(STYLE_NORMAL);
            setDisabled(true);
            return;
        }

        try
        {
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

        inputNode.setStyle(STYLE_ERROR);
        setDisabled(true);
    }

    private void setDisabled(boolean disable)
    {
        buttons.forEach(button -> button.setDisable(disable));
    }
}
