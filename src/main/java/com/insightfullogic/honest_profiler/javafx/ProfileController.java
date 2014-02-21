package com.insightfullogic.honest_profiler.javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.function.Consumer;

public class ProfileController {

    @FXML
    private FlatProfileController flatProfileController;

    private Consumer<File> parser;

    public ProfileController setViewModel(ProfileViewModel viewModel) {
        flatProfileController.setViewModel(viewModel);
        return this;
    }

    public ProfileController setFileParser(Consumer<File> parser) {
        this.parser = parser;
        return this;
    }

    public void quit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void open(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a log file");
        parser.accept(fileChooser.showOpenDialog(null));
    }

}
