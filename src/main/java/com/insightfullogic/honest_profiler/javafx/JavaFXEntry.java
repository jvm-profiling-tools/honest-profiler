package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.collector.LogCollector;
import com.insightfullogic.honest_profiler.javafx.flat.FlatProfileController;
import com.insightfullogic.honest_profiler.javafx.flat.FlatViewModel;
import com.insightfullogic.honest_profiler.javafx.tree.TreeProfileController;
import com.insightfullogic.honest_profiler.javafx.tree.TreeViewModel;
import com.insightfullogic.honest_profiler.log.LogParser;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;

public class JavaFXEntry extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = createRoot();
        stage.setTitle("Honest Profiler");
        stage.setScene(new Scene(root));
        stage.show();
    }

    static Parent createRoot() {
        MutablePicoContainer pico = registerComponents();
        PicoFXLoader loader = pico.getComponent(PicoFXLoader.class);
        return loader.load("ProfileView.fxml", ProfileController.class);
    }

    private static MutablePicoContainer registerComponents() {
        MutablePicoContainer pico = new DefaultPicoContainer(new Caching())
            .addAdapter(new ProfileListenerProvider())
            .addComponent(FlatViewModel.class)
            .addComponent(FlatProfileController.class)
            .addComponent(TreeViewModel.class)
            .addComponent(TreeProfileController.class)
            .addComponent(PicoFXLoader.class)
            .addComponent(LogCollector.class)
            .addComponent(LogParser.class)
            .addComponent(ProfileController.class);

        return pico.addComponent(pico);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
