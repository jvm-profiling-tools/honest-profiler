package com.insightfullogic.honest_profiler.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class SceneLoader {

    private Object controller;
    private final String fxml;

    public SceneLoader(String fxml) {
        this.fxml = fxml;
    }

    public Scene load() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent view = loader.load();
        controller = loader.getController();
        return new Scene(view);
    }

    public <T> T getController() {
        return (T) controller;
    }

}
