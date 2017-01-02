package com.insightfullogic.honest_profiler.ports.javafx.view.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.insightfullogic.honest_profiler.core.profiles.ProfileNode;
import com.insightfullogic.honest_profiler.core.profiles.ProfileTree;
import com.insightfullogic.honest_profiler.ports.javafx.model.filter.FilterSpecification;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class RootNodeAdapter extends TreeItem<ProfileNode>
{
    private ObjectProperty<FilterSpecification> filterSpec;
    private final Map<Long, ThreadNodeAdapter> threadsById;

    public RootNodeAdapter(ObjectProperty<FilterSpecification> filterSpec)
    {
        super();

        this.filterSpec = filterSpec;
        threadsById = new HashMap<>();
        setExpanded(true);
    }

    public void update(List<ProfileTree> trees)
    {
        ObservableList<TreeItem<ProfileNode>> children = getChildren();
        children.clear();

        boolean hideErrorThreads = filterSpec.get().isHideErrorThreads();

        Set<Long> present = new HashSet<>();
        for (ProfileTree tree : trees)
        {
            if (hideErrorThreads
                && tree.getRootNode().getFrameInfo().getMethodId() < 0
                && tree.getRootNode().getChildren().isEmpty())
            {
                continue; // Skip. Won't be marked as present either.
            }

            long threadId = tree.getThreadId();
            present.add(threadId);

            ThreadNodeAdapter thread = threadsById.computeIfAbsent(threadId, id ->
            {
                ThreadNodeAdapter adapter = new ThreadNodeAdapter(
                    id,
                    tree.getThreadName(),
                    tree.getNumberOfSamples());
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
