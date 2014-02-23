package com.insightfullogic.honest_profiler.javafx.profile;

import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.collector.ProfileListener;
import com.insightfullogic.honest_profiler.collector.ProfileNode;
import com.insightfullogic.honest_profiler.collector.ProfileTree;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;

import java.util.List;

public class TreeViewModel implements ProfileListener {

    @FXML
    private TreeView<ProfileNode> treeView;

    @FXML
    private void initialize() {
        treeView.setCellFactory(view -> new TreeViewCell());
    }

    @Override
    public void accept(Profile profile) {
        List<ProfileTree> trees = profile.getTrees();
        ProfileNode profileNode = trees.get(0).getRootNode();
        treeView.setRoot(new TreeNodeAdapter(profileNode));
    }

}
