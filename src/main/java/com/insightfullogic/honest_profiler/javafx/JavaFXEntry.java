package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.LogCollector;
import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.javafx.flat.FlatViewModel;
import com.insightfullogic.honest_profiler.javafx.tree.TreeViewModel;
import com.insightfullogic.honest_profiler.log.LogParser;
import com.insightfullogic.honest_profiler.util.Listeners;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXEntry extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ProfileWindow window = new ProfileWindow();
        Parent root = window.initialise();
        stage.setTitle("Honest Profiler");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
