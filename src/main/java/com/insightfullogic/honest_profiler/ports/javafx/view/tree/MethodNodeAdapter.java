package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import java.util.HashMap;
import java.util.Map;

import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class MethodNodeAdapter extends TreeItem<ProfileNode>
{
    private static final double TIMESHARE_EXPAND_FACTOR = 0.2;

    private final Map<Long, MethodNodeAdapter> childrenByMethodId;

    private boolean firstUpdate;

    public MethodNodeAdapter()
    {
        super();

        childrenByMethodId = new HashMap<>();
        firstUpdate = true;
    }

    public void update(ProfileNode profileNode)
    {
        setValue(profileNode);

        if (firstUpdate && getParent().isExpanded())
        {
            firstUpdate = false;
            setExpanded(profileNode.getTotalTimeShare() >= TIMESHARE_EXPAND_FACTOR);
        }

        ObservableList<TreeItem<ProfileNode>> children = getChildren();

        profileNode.getChildren().forEach(child ->
        {
            long methodId = child.getFrameInfo().getMethodId();

            MethodNodeAdapter childAdapter = childrenByMethodId
                .computeIfAbsent(methodId, id ->
                {
                    MethodNodeAdapter adapter = new MethodNodeAdapter();
                    children.add(adapter);
                    return adapter;
                });

            childAdapter.update(child);
        });
    }

    public int getDepth()
    {
        return getValue() == null ? 0 : getValue().getDescendantDepth();
    }
}
