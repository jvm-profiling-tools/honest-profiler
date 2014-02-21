package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.ProfileNode;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class TreeNodeAdapter extends TreeItem<ProfileNode> {

    private static final double TIMESHARE_EXPAND_FACTOR = 0.3;

    public TreeNodeAdapter(ProfileNode profileNode) {
        super(profileNode);
        double timeShare = profileNode.getTimeShare();
        setExpanded(timeShare >= TIMESHARE_EXPAND_FACTOR);
        ObservableList<TreeItem<ProfileNode>> children = getChildren();
        profileNode.children()
                   .map(TreeNodeAdapter::new)
                   .forEach(children::add);
    }

}
