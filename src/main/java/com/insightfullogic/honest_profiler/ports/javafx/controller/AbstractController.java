package com.insightfullogic.honest_profiler.ports.javafx.controller;

import java.util.ResourceBundle;

import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;

import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * Superclass for all Controllers in the application. It holds the {@link ApplicationContext}, a context which is shared
 * by all controllers.
 * <p>
 * The class interacts with the {@link ApplicationContext} for I18N, retrieving the resources for keys. It also manages
 * what is displayed in the InfoBar, an application-level text area displayed at the bottem of the root window, used for
 * displaying contextual help and information messages.
 * <p>
 * NOTE : Any controllers extending this class, and which in turn contain other ApplicationController controllers,
 * should override {@link #setApplicationContext(ApplicationContext)} using the following pattern :
 *
 * <pre>
 * <code>
 * public setApplicationContext(ApplicationContext context)
 * {
 *   super.setApplicationContext(context);
 *   &lt;includedController1&gt;.setApplicationContext(context);
 *   &lt;includedController2&gt;.setApplicationContext(context);
 *   ...
 * }
 * </code>
 * </pre>
 */
public abstract class AbstractController
{
    // Instance Properties

    private ApplicationContext applicationContext;

    // FXML Implementation

    /**
     * This method must be called by subclasses in their FXML initialize().
     * <p>
     * The idea is to streamline similar tasks happening in the initialization method, and encourage decluttering of the
     * initialize() methods by extracting similar tasks to separate methods.
     */
    protected void initialize()
    {
        initializeInfoText();
        initializeHandlers();
    }

    /**
     * Link nodes with "InfoBar" messages.
     * <p>
     * The InfoBar is the info- or status bar at the bottom of the UI. While hovering over various {@link Node}s or when
     * specific events occur, helpful information should appear in the InfoBar.
     */
    protected abstract void initializeInfoText();

    /**
     * Associate various Handlers and/or Listeners with the {@link Node}s managed by the subclassing controller.
     */
    protected abstract void initializeHandlers();

    // Instance Accessors

    /**
     * Returns the {@link ApplicationContext}. The name has been shortened to unclutter code in subclasses.
     * <p>
     *
     * @return the {@link ApplicationContext} of this application
     */
    protected ApplicationContext appCtx()
    {
        return applicationContext;
    }

    /**
     * Sets the application context.
     * <p>
     * NOTE : Any controllers extending this class, and which in turn contain other ApplicationController controllers,
     * should override this method using the following pattern :
     *
     * <pre>
     * <code>
     * public setApplicationContext(ApplicationContext context)
     * {
     *   super.setApplicationContext(context);
     *   &lt;includedController1&gt;.setApplicationContext(context);
     *   &lt;includedController2&gt;.setApplicationContext(context);
     *   ...
     * }
     * </code>
     * </pre>
     * <p>
     *
     * @param applicationContext the ApplicationContext of this application
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    // I18N-related Methods

    /**
     * Look up the String associated with this key in the current {@link ResourceBundle}.
     * <p>
     *
     * @param key the key in the ResourceBundle
     * @return the String associated with the key in the current {@link ResourceBundle}
     */
    protected final String getText(String key)
    {
        return applicationContext.textFor(key);
    }

    /**
     * Construct the String based on the pattern associated with this key in the current {@link ResourceBundle}, using
     * the provided arguments.
     * <p>
     *
     * @param key the key in the ResourceBundle
     * @param args the arguments needed by the pattern
     * @return the String constructed using the pattern and provided arguments
     */
    protected final String getText(String key, Object... args)
    {
        return applicationContext.textFor(key, args);
    }

    // InfoBar-related Methods

    /**
     * Display a message in the InfoBar, by retrieving the message from the current {@link ResourceBundle} using the
     * provided key.
     * <p>
     *
     * @param key the key to the message in the {@link ResourceBundle}
     */
    protected final void infoFromBundle(String key)
    {
        applicationContext.setInfoFromBundle(key);
    }

    /**
     * Display a message in the InfoBar, by retrieving the pattern from the current {@link ResourceBundle} using the
     * provided key and formatting it using the provided arguments.
     * <p>
     *
     * @param key the key to the message pattern in the {@link ResourceBundle}
     * @param args the arguments needed by the pattern
     */
    protected final void infoFromBundle(String key, Object... args)
    {
        applicationContext.setInfoFromBundle(key, args);
    }

    /**
     * Clears the InfoBar.
     */
    protected final void clearInfo()
    {
        applicationContext.clearInfo();
    }

    /**
     * Associates a message with a given {@link Node}. The message is retrieved from the current {@link ResourceBundle}
     * using the provided key, and is displayed while the InfoBar whenever the mouse hovers over the {@link Node}.
     * <p>
     *
     * @param node the target {@link Node}
     * @param key the key to the message in the {@link ResourceBundle}
     */
    protected void info(Node node, final String key)
    {
        node.onMouseEnteredProperty().set(event -> infoFromBundle(key));
        node.onMouseExitedProperty().set(event -> clearInfo());
    }

    /**
     * Associates a message with a given {@link Node}. The message pattern is retrieved from the current
     * {@link ResourceBundle} using the provided key, formatted using the provided arguments, and displayed while the
     * InfoBar whenever the mouse hovers over the {@link Node}.
     * <p>
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
     * Associates a message with a {@link Tab} Header, as per {@link #info(Node, String)}.
     * <p>
     *
     * @param tab the target {@link Tab}
     * @param key the key to the message in the {@link ResourceBundle}
     */
    protected void info(Tab tab, final String key)
    {
        info(tab.getGraphic(), key);
    }

    /**
     * Associates a message with a {@link Tab} Header, as per {@link #info(Node, String, Object...)}.
     * <p>
     *
     * @param tab the target {@link Tab}
     * @param key the key to the message in the {@link ResourceBundle}
     * @param args the arguments needed by the pattern
     */
    protected void info(Tab tab, final String key, Object... args)
    {
        info(tab.getGraphic(), key, args);
    }
}
