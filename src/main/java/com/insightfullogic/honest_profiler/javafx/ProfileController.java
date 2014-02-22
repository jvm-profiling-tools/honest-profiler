package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.javafx.flat.FlatProfileController;
import com.insightfullogic.honest_profiler.javafx.flat.FlatViewModel;
import com.insightfullogic.honest_profiler.javafx.tree.TreeProfileController;
import com.insightfullogic.honest_profiler.javafx.tree.TreeViewModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.Consumer;

public class ProfileController {

    @FXML
    private FlatProfileController flatProfileController;

    @FXML
    private TreeProfileController treeProfileController;

    @FXML
    private StackPane content;

    private boolean flatView = false;

    private Consumer<File> parser;

    public ProfileController setFlatModel(FlatViewModel viewModel) {
        flatProfileController.setViewModel(viewModel);
        return this;
    }

    public ProfileController setTreeModel(TreeViewModel viewModel) {
        treeProfileController.setViewModel(viewModel);
        return this;
    }

    public ProfileController setFileParser(Consumer<File> parser) {
        this.parser = parser;
        return this;
    }

    public void quit(ActionEvent event) {
        Platform.exit();
    }

    public void open(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a log file");
        File file = fileChooser.showOpenDialog(null);
        if (file != null)
            parser.accept(file);
    }

    public void flipView(ActionEvent event) {
        Button button = (Button) event.getSource();
        flipButtonText(button);
        flipContent();
    }

    private void flipContent() {
        // StackPane only displays the head of its children list
        ObservableList<Node> children = content.getChildren();
        Node previouslyVisible = children.remove(0);
        children.add(previouslyVisible);
    }

    private void flipButtonText(Button button) {
        flatView = !flatView;
        button.setText(flatView ? "Tree View" : "Flat View");
    }

}
