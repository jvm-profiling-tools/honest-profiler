package com.insightfullogic.honest_profiler.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class Loader {

    private final Parent view;
    private Object controller;

    public Loader(String fxml) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        try {
            view = loader.load();
            controller = loader.getController();
        } catch (IOException e) {
            throw new UserInterfaceConfigurationException(e);
        }
    }

    public Parent getView() {
        return view;
    }

    public <T> T getController(Class<T> cls) {
        return (T) controller;
    }

}
