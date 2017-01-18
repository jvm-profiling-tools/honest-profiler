package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.insightfullogic.honest_profiler.core.aggregation.result.AggregatedNode;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterSpecification;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class RootNodeAdapter extends TreeItem<AggregatedNode<String>>
{
    private ObjectProperty<FilterSpecification> filterSpec;
    private final Map<String, ThreadNodeAdapter> threadsById;

    public RootNodeAdapter(ObjectProperty<FilterSpecification> filterSpec)
    {
        super();

        this.filterSpec = filterSpec;
        threadsById = new HashMap<>();
        setExpanded(true);
    }

    public void update(List<AggregatedNode<String>> trees)
    {
        ObservableList<TreeItem<AggregatedNode<String>>> children = getChildren();
        children.clear();

        boolean hideErrorThreads = filterSpec.get().isHideErrorThreads();

        Set<String> present = new HashSet<>();
        for (AggregatedNode<String> tree : trees)
        {
            // TODO fix this.
            // if (hideErrorThreads
            // && tree.getRootNode().getFrameInfo().getMethodId() < 0
            // && tree.getRootNode().getChildren().isEmpty())
            // {
            // continue; // Skip. Won't be marked as present either.
            // }

            String threadId = tree.getKey();
            present.add(threadId);

            ThreadNodeAdapter thread = threadsById.computeIfAbsent(threadId, id ->
            {
                ThreadNodeAdapter adapter = new ThreadNodeAdapter(
                    id,
                    tree.getKey(),
                    tree.getData().getTotalCnt());
                return adapter;
            });

            children.add(thread);
            thread.update(tree);
        }

        threadsById.keySet().stream().filter(key -> !present.contains(key))
            .forEach(key -> children.remove(threadsById.get(key)));
        // threadsById.keySet().retainAll(present);
    }
}
