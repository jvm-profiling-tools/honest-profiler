package com.insightfullogic.honest_profiler.ports.javafx.view.cell;

import static com.insightfullogic.honest_profiler.ports.javafx.util.ContextMenuUtil.bindContextMenuForTreeTableCell;
import static com.insightfullogic.honest_profiler.ports.javafx.util.StyleUtil.STYLE_METHOD_NAME;

import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.control.TreeTableCell;

public class MethodNameTreeTableCell<T> extends TreeTableCell<T, String>
{
    public MethodNameTreeTableCell(ApplicationContext appCtx)
    {
        super();
        bindContextMenuForTreeTableCell(appCtx, this);
    }

    @Override
    protected void updateItem(String item, boolean empty)
    {
        super.updateItem(item, empty);

        if (empty)
        {
            setText(null);
            setGraphic(null);
            return;
        }
        setStyle(STYLE_METHOD_NAME);
        setText(item);
    }
}
