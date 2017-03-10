package com.insightfullogic.honest_profiler.ports.javafx.view.cell;

import static javafx.geometry.Pos.CENTER_RIGHT;
import static javafx.scene.text.TextAlignment.RIGHT;

import java.util.function.Function;

import javafx.scene.control.TableCell;

public class NumberTableCell<T> extends TableCell<T, Number>
{
    // Instance Properties

    private Function<Number, String> displayFunction;
    private Function<Number, String> styleFunction;

    // Instance Constructors

    /**
     * Simple Constructor.
     *
     * @param displayFunction a {@link Function} which converts the {@link Number} to be displayed to a {@link String}
     * @param styleFunction an optional {@link Function} returning a style based on the displayed {@link Number}
     */
    public NumberTableCell(Function<Number, String> displayFunction,
                           Function<Number, String> styleFunction)
    {
        super();

        setTextAlignment(RIGHT);
        setAlignment(CENTER_RIGHT);

        this.displayFunction = displayFunction;
        this.styleFunction = styleFunction;
    }

    // TableCell Implementation

    @Override
    protected void updateItem(Number number, boolean isEmpty)
    {
        if (isEmpty || number == null)
        {
            setText(null);
            setStyle(null);
            return;
        }

        setText(displayFunction.apply(number));
        setStyle(styleFunction == null ? null : styleFunction.apply(number));
    }
}
