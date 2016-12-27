package com.insightfullogic.honest_profiler.ports.javafx.util.validation;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class ValidateStringListener extends StringValidationListener
{
    public ValidateStringListener(Node inputNode, Button... buttons)
    {
        super(inputNode, (t) -> true, buttons);
    }
}
