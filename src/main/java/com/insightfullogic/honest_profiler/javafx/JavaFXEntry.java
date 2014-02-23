package com.insightfullogic.honest_profiler.javafx;

import com.insightfullogic.honest_profiler.RootMarker;
import com.insightfullogic.honest_profiler.javafx.profile.ProfileViewModel;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JavaFXEntry extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = createRoot();
        stage.setTitle("Honest Profiler");
        stage.setScene(new Scene(root));
        stage.show();
    }

    static Parent createRoot() {
        ApplicationContext pico = registerComponents();
        PicoFXLoader loader = pico.getBean(PicoFXLoader.class);
        return loader.load("ProfileView.fxml", ProfileViewModel.class);
    }

    private static ApplicationContext registerComponents() {
        final AnnotationConfigApplicationContext pico = new AnnotationConfigApplicationContext();
        pico.scan(RootMarker.class.getPackage().getName());
        pico.refresh();
        return pico;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
