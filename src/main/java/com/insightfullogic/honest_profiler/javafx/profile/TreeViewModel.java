package com.insightfullogic.honest_profiler.javafx.profile;

import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.collector.ProfileListener;
import com.insightfullogic.honest_profiler.collector.ProfileNode;
import com.insightfullogic.honest_profiler.collector.ProfileTree;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.TreeView;

import java.util.List;

public class TreeViewModel implements ProfileListener {

    @FXML
    private TreeView<ProfileNode> treeView;

    @FXML
    private void initialize() {
        treeView.setCellFactory(view -> new TreeViewCell());
        treeView.setShowRoot(false);
    }

    @Override
    public void accept(Profile profile) {
        List<ProfileTree> trees = profile.getTrees();
        treeView.setRoot(new TreeNodeAdapter(trees));
    }

}
