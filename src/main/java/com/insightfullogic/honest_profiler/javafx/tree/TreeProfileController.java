package com.insightfullogic.honest_profiler.javafx.tree;

import com.insightfullogic.honest_profiler.collector.ProfileNode;
import com.insightfullogic.honest_profiler.javafx.tree.TreeViewCell;
import com.insightfullogic.honest_profiler.javafx.tree.TreeViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;

public class TreeProfileController {

    private final TreeViewModel viewModel;

    @FXML
    private TreeView<ProfileNode> treeView;

    public TreeProfileController(TreeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    private void initialize() {
        treeView.setCellFactory(view -> new TreeViewCell());
        viewModel.setView(treeView);
    }

}
