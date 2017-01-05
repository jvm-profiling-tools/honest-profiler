package com.insightfullogic.honest_profiler.ports.javafx.controller;

import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.Node;
import javafx.scene.control.Tab;

public abstract class AbstractController
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

    protected final String getText(String key)
    {
        return applicationContext.textFor(key);
    }

    protected final String getText(String key, Object... args)
    {
        return applicationContext.textFor(key, args);
    }

    protected final void info(String message)
    {
        applicationContext.setInfo(message);
    }

    protected final void infoFromBundle(String key)
    {
        applicationContext.setInfoFromBundle(key);
    }

    protected final void infoFromBundle(String key, Object... args)
    {
        applicationContext.setInfoFromBundle(key, args);
    }

    protected final void clearInfo()
    {
        applicationContext.setInfo("");
    }

    protected void info(Node node, final String key)
    {
        node.onMouseEnteredProperty().set(event -> infoFromBundle(key));
        node.onMouseExitedProperty().set(event -> clearInfo());
    }

    protected void info(Node node, final String key, Object... args)
    {
        node.onMouseEnteredProperty().set(event -> infoFromBundle(key, args));
        node.onMouseExitedProperty().set(event -> clearInfo());
    }

    protected void info(Tab tab, final String key)
    {
        info(tab.getGraphic(), key);
    }

    protected void info(Tab tab, final String key, Object... args)
    {
        info(tab.getGraphic(), key, args);
    }

    protected void initialize()
    {
        initializeInfoText();
        initializeHandlers();
    }

    protected abstract void initializeInfoText();

    protected abstract void initializeHandlers();
}
