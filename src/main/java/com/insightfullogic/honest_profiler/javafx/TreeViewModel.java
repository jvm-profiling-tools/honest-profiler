package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.collector.ProfileListener;
import com.insightfullogic.honest_profiler.collector.ProfileNode;
import com.insightfullogic.honest_profiler.collector.ProfileTree;
import javafx.scene.control.TreeView;

import java.util.List;

public class TreeViewModel implements ProfileListener {

    private TreeView<ProfileNode> view;

    public void setView(TreeView<ProfileNode> view) {
        this.view = view;
    }

    @Override
    public void accept(Profile profile) {
        List<ProfileTree> trees = profile.getTrees();
        ProfileNode profileNode = trees.get(0).getRootNode();
        view.setRoot(new TreeNodeAdapter(profileNode));
    }

}
