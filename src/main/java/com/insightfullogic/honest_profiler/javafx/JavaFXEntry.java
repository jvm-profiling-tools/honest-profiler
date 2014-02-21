package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.LogCollector;
import com.insightfullogic.honest_profiler.log.LogParser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class JavaFXEntry extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ProfileViewModel viewModel = new ProfileViewModel();
        LogCollector collector = new LogCollector(viewModel);
        LogParser parser = new LogParser(collector);

        SceneLoader loader = new SceneLoader("ProfileView.fxml");
        stage.setTitle("Honest Profiler");
        stage.setScene(loader.load());
        stage.show();

        loader.getController(ProfileController.class)
              .setViewModel(viewModel);

        parser.parse(new File("example.hpl"));
    }

    public static void main(String[] args) {
        launch(args);
    }

}
