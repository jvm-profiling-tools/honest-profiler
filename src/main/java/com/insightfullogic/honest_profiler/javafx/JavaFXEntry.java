package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.LogCollector;
import com.insightfullogic.honest_profiler.collector.Profile;
import com.insightfullogic.honest_profiler.javafx.flat.FlatViewModel;
import com.insightfullogic.honest_profiler.javafx.tree.TreeViewModel;
import com.insightfullogic.honest_profiler.log.LogParser;
import com.insightfullogic.honest_profiler.util.Listeners;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFXEntry extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FlatViewModel flatModel = new FlatViewModel();
        TreeViewModel treeModel = new TreeViewModel();

        Listeners<Profile> listener = new Listeners<Profile>()
                .of(flatModel::accept)
                .of(treeModel::accept);

        LogCollector collector = new LogCollector(listener::accept);
        LogParser parser = new LogParser(collector);

        SceneLoader loader = new SceneLoader("ProfileView.fxml");
        stage.setTitle("Honest Profiler");
        stage.setScene(loader.load());
        stage.show();

        loader.getController(ProfileController.class)
              .setFlatModel(flatModel)
              .setTreeModel(treeModel)
              .setFileParser(parser::parse);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
