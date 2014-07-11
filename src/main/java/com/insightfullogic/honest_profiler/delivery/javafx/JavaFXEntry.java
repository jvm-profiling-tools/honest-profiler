package com.insightfullogic.honest_profiler.delivery.javafx;

import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.adapters.sources.LocalMachineSource;
import com.insightfullogic.honest_profiler.delivery.javafx.landing.LandingViewModel;
import com.insightfullogic.honest_profiler.delivery.javafx.profile.*;
import com.insightfullogic.honest_profiler.core.parser.LogParser;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

public class JavaFXEntry extends Application {

    private MutablePicoContainer pico;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Honest Profiler");
        createStart(stage);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        pico.stop();
    }

    void createStart(Stage stage) {
        pico = registerComponents(stage);
        WindowViewModel stageModel = pico.getComponent(WindowViewModel.class);
        Parent parent = stageModel.displayStart();
        pico.start();
    }

    private static MutablePicoContainer registerComponents(Stage stage) {
        return registerComponents().addComponent(stage);
    }

    public static MutablePicoContainer registerComponents() {
        MutablePicoContainer pico = new PicoBuilder()
            .withJavaEE5Lifecycle()
            .withCaching()
            .build()

            .addAdapter(new ProfileListenerProvider())
            .addComponent(LogCollector.class)
            .addComponent(LogParser.class)
            .addComponent(FlatViewModel.class)
            .addComponent(TreeViewModel.class)
            .addComponent(TraceCountViewModel.class)
            .addComponent(ProfileViewModel.class)
            .addComponent(LandingViewModel.class)
            .addComponent(WindowViewModel.class)
            .addComponent(PicoFXLoader.class)
            .addComponent(LocalMachineSource.class);

        return pico.addComponent(pico);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
