package com.insightfullogic.honest_profiler.javafx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.insightfullogic.honest_profiler.javafx.WindowViewModel.Window.Landing;

public class WindowViewModel {

    public static enum Window {
        Landing,
        Profile;

        private String getFxmlFile() {
            return name() + "View.fxml";
        }
    }

    private final PicoFXLoader loader;
    private final Stage stage;

    public WindowViewModel(PicoFXLoader loader, Stage stage) {
        this.loader = loader;
        this.stage = stage;
    }

    public Parent display(Window window) {
        Parent parent = loader.load(window.getFxmlFile());
        stage.setScene(new Scene(parent));
        return parent;
    }

    public Parent displayStart() {
        return display(Landing);
    }

}
