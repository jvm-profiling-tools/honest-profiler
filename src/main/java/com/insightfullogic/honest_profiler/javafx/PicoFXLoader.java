package com.insightfullogic.honest_profiler.javafx;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class PicoFXLoader {

    private final ApplicationContext pico;

    @Autowired
    public PicoFXLoader(ApplicationContext pico) {
        this.pico = pico;
    }

    public Parent load(String fxml, Class<?> controllerClass) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setControllerFactory(pico::getBean);
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UserInterfaceConfigurationException(e);
        }
    }

}
