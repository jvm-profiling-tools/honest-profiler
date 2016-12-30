package com.insightfullogic.honest_profiler.ports.javafx.view.cell;

import static com.insightfullogic.honest_profiler.ports.javafx.util.ContextMenuUtil.bindContextMenuForTreeCell;

import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

import javafx.scene.control.TreeTableCell;

public class ProfileNodeTreeTableCell extends TreeTableCell<ProfileNode, String>
{
    public ProfileNodeTreeTableCell()
    {
        super();
        bindContextMenuForTreeCell(this);
    }

    @Override
    protected void updateItem(String item, boolean empty)
    {
        super.updateItem(item, empty);

        setStyle(null);
        if (empty)
        {
            setText(null);
            setGraphic(null);
            return;
        }
        setText(item);
    }
}
