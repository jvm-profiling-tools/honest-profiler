package com.insightfullogic.honest_profiler.ports.javafx.controller;

import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.Node;
import javafx.scene.control.Tab;

public class AbstractController
{
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    protected ApplicationContext appCtx()
    {
        return applicationContext;
    }

    protected final void info(String message)
    {
        applicationContext.setInfo(message);
    }

    protected void info(Node node, final String message)
    {
        node.onMouseEnteredProperty().set(event -> info(message));
        node.onMouseExitedProperty().set(event -> info(""));
    }

    protected void info(Tab tab, final String message)
    {
        tab.getGraphic().onMouseEnteredProperty().set(event -> info(message));
        tab.getGraphic().onMouseExitedProperty().set(event -> info(""));
    }
}
