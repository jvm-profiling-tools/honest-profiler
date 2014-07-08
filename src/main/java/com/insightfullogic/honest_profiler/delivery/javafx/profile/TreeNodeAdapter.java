package com.insightfullogic.honest_profiler.delivery.javafx.profile;

import com.insightfullogic.honest_profiler.core.model.collector.ProfileNode;
import com.insightfullogic.honest_profiler.core.model.collector.ProfileTree;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;
import java.util.stream.IntStream;

public class TreeNodeAdapter extends TreeItem<ProfileNode> {

    private static final double TIMESHARE_EXPAND_FACTOR = 0.3;

    private static final int NOT_APPLICABLE = -1;

    public static enum Type {
        ROOT, THREAD, METHOD;
    }

    private final Type type;
    private final int threadId;

    // com.insightfullogic.honest_profiler.delivery.console.Root Node
    public TreeNodeAdapter(List<ProfileTree> trees) {
        threadId = NOT_APPLICABLE;
        type = Type.ROOT;
        setExpanded(true);
        ObservableList<TreeItem<ProfileNode>> children = getChildren();
        IntStream.range(0, trees.size())
                 .mapToObj(i -> new TreeNodeAdapter(trees.get(i), i))
                 .forEach(children::add);
    }

    // Thread Node
    public TreeNodeAdapter(ProfileTree tree, int threadId) {
        this.threadId = threadId;
        type = Type.THREAD;
        setExpanded(true);
        // add top level method
        getChildren().add(new TreeNodeAdapter(tree.getRootNode()));
    }

    // Method Node
    public TreeNodeAdapter(ProfileNode profileNode) {
        super(profileNode);
        threadId = NOT_APPLICABLE;
        type = Type.METHOD;
        double timeShare = profileNode.getTimeShare();
        setExpanded(timeShare >= TIMESHARE_EXPAND_FACTOR);
        ObservableList<TreeItem<ProfileNode>> children = getChildren();
        profileNode.children()
                   .map(node -> new TreeNodeAdapter(node))
                   .forEach(children::add);
    }

    public Type getType() {
        return type;
    }

    public int getThreadId() {
        return threadId;
    }

}
