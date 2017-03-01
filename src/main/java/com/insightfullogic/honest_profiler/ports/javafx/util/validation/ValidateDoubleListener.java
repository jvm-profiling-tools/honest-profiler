package com.insightfullogic.honest_profiler.ports.javafx.util.validation;

import static java.lang.Double.parseDouble;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class ValidateDoubleListener extends StringValidationListener
{
    public ValidateDoubleListener(Node inputNode, double min, double max, Button... buttons)
    {
        super(inputNode, (t) ->
        {
            double value = parseDouble(t);
            return value >= min && value <= max;
        }, buttons);
    }
}
