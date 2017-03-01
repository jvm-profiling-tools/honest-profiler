package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        Set<Long> present = new HashSet<>();

        ObservableList<TreeItem<ProfileNode>> children = getChildren();
        children.clear();

        profileNode.getChildren().forEach(child ->
        {
            long methodId = child.getFrameInfo().getMethodId();
            present.add(methodId);

            MethodNodeAdapter childAdapter = childrenByMethodId
                .computeIfAbsent(methodId, id ->
                {
                    MethodNodeAdapter adapter = new MethodNodeAdapter();
                    return adapter;
                });

            children.add(childAdapter);
            childAdapter.update(child);
        });

        childrenByMethodId.keySet().stream().filter(key -> !present.contains(key))
            .forEach(key -> children.remove(childrenByMethodId.get(key)));
    }

    public int getDepth()
    {
        return getValue() == null ? 0 : getValue().getDescendantDepth();
    }
}
