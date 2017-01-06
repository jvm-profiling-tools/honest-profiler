package com.insightfullogic.honest_profiler.ports.javafx.controller;

import java.util.ResourceBundle;

import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * Superclass for all Controllers in the application. It holds the context which is shared by all controllers.
 */
public abstract class AbstractController
{
    private ApplicationContext applicationContext;

    /**
     * Sets the application context.
     *
     * @param applicationContext the ApplicationContext of this application
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    /**
     * Gets the {@link ApplicationContext}. The name has been shortened to unclutter code in subclasses.
     *
     * @return the {@link ApplicationContext} of this application.
     */
    protected ApplicationContext appCtx()
    {
        return applicationContext;
    }

    /**
     * Look up the String associated with this key in the current {@link ResourceBundle}.
     *
     * @param key the key in the ResourceBundle
     * @return the String associated with the key in the curremt {@link ResourceBundle}
     */
    protected final String getText(String key)
    {
        return applicationContext.textFor(key);
    }

    /**
     * Construct the String based on the pattern associated with this key in the current {@link ResourceBundle}, using
     * the provided arguments.
     *
     * @param key the key in the ResourceBundle
     * @param args the arguments needed by the pattern
     * @return the String constructed using the pattern and provided arguments
     */
    protected final String getText(String key, Object... args)
    {
        return applicationContext.textFor(key, args);
    }

    /**
     * Display a message in the InfoBar, by retrieving the message from the current {@link ResourceBundle} using the
     * provided key.
     *
     * @param message the key to the message in the {@link ResourceBundle}
     */
    protected final void infoFromBundle(String key)
    {
        applicationContext.setInfoFromBundle(key);
    }

    /**
     * Display a message in the InfoBar, by retrieving the pattern from the current {@link ResourceBundle} using the
     * provided key and formatting it using the provided arguments.
     *
     * @param message the key to the message pattern in the {@link ResourceBundle}
     * @param args the arguments needed by the pattern
     */
    protected final void infoFromBundle(String key, Object... args)
    {
        applicationContext.setInfoFromBundle(key, args);
    }

    /**
     * Clears the InfoBar
     */
    protected final void clearInfo()
    {
        applicationContext.setInfo("");
    }

    /**
     * Associates a message with a given Node. The message is retrieved from the current {@link ResourceBundle} using
     * the provided key, and is displayed while the InfoBar whenever the mouse hovers over the Node.
     *
     * @param node the target Node
     * @param key the key to the message in the {@link ResourceBundle}
     */
    protected void info(Node node, final String key)
    {
        node.onMouseEnteredProperty().set(event -> infoFromBundle(key));
        node.onMouseExitedProperty().set(event -> clearInfo());
    }

    /**
     * Associates a message with a given Node. The message pattern is retrieved from the current {@link ResourceBundle}
     * using the provided key, formatted using the provided arguments, and displayed while the InfoBar whenever the
     * mouse hovers over the Node.
     *
     * @param node the target Node
     * @param key the key to the message in the {@link ResourceBundle}
     * @param args the arguments needed by the pattern
     */
    protected void info(Node node, final String key, Object... args)
    {
        node.onMouseEnteredProperty().set(event -> infoFromBundle(key, args));
        node.onMouseExitedProperty().set(event -> clearInfo());
    }

    /**
     * Associates a message with a Tab Header, as per {@link #info(Node, String)}.
     *
     * @param tab the target Tab
     * @param key the key to the message in the {@link ResourceBundle}
     */
    protected void info(Tab tab, final String key)
    {
        info(tab.getGraphic(), key);
    }

    /**
     * Associates a message with a Tab Header, as per {@link #info(Node, String, Object...)}.
     *
     * @param tab the target Tab
     * @param key the key to the message in the {@link ResourceBundle}
     * @param args the arguments needed by the pattern
     */
    protected void info(Tab tab, final String key, Object... args)
    {
        info(tab.getGraphic(), key, args);
    }

    /**
     * This method must be called by subclasses in their FXML initialize(). The idea is to streamline similar tasks
     * happening in the initialization method, and encourage decluttering of those methods by extracting similar tasks
     * to separate methods.
     */
    protected void initialize()
    {
        initializeInfoText();
        initializeHandlers();
    }

    /**
     * Link nodes with "InfoBar" messages. The InfoBar is the info- or statusbar at the bottom of the UI. While hovering
     * over various nodes, helpful information should appear in the InfoBar.
     */
    protected abstract void initializeInfoText();

    /**
     * Associate various Handlers and/or Listeners with the nodes managed by the controller.
     */
    protected abstract void initializeHandlers();
}
