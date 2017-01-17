package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedNode;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class MethodNodeAdapter extends TreeItem<AggregatedNode>
{
    private static final double TIMESHARE_EXPAND_FACTOR = 0.2;

    private final Map<String, MethodNodeAdapter> childrenByMethodId;

    private boolean firstUpdate;

    public MethodNodeAdapter()
    {
        super();

        childrenByMethodId = new HashMap<>();
        firstUpdate = true;
    }

    public void update(AggregatedNode profileNode)
    {
        setValue(profileNode);

        if (firstUpdate && getParent().isExpanded())
        {
            firstUpdate = false;
            setExpanded(profileNode.getTotalCntPct() >= TIMESHARE_EXPAND_FACTOR);
        }

        Set<String> present = new HashSet<>();

        ObservableList<TreeItem<AggregatedNode>> children = getChildren();
        children.clear();

        profileNode.getChildren().forEach(child ->
        {
            String methodId = child.getKey();
            present.add(methodId);

            MethodNodeAdapter childAdapter = childrenByMethodId.computeIfAbsent(methodId, id ->
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
