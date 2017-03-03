package com.insightfullogic.honest_profiler.ports.javafx.model;

import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.format;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.getDefaultBundle;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.getDefaultLocale;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

import com.insightfullogic.honest_profiler.ports.javafx.controller.RootController;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;

/**
 * The ApplicationContext contains state which needs to be shared by all controllers in the application, and methods to
 * access and change that state.
 */
public final class ApplicationContext
{
    // Instance Properties

    // - I18N
    private Locale currentLocale;
    private ResourceBundle currentBundle;

    // - InfoBar current text
    private SimpleStringProperty info;

    // - Profile Context mappings
    private Map<String, ProfileContext> nameToContextMap;
    private Map<String, ProfileContext> pathToContextMap;

    // - Root Controller
    private RootController rootController;

    // - Task Execution
    private ExecutorService executorService = newCachedThreadPool();

    // Instance Constructors

    /**
     * Create the ApplicationContext with teh specified {@link RootController}.
     * <p>
     * 
     * @param rootController the {@link RootController} for the application
     */
    public ApplicationContext(RootController rootController)
    {
        currentLocale = getDefaultLocale();
        currentBundle = getDefaultBundle();
        info = new SimpleStringProperty();
        this.rootController = rootController;
        nameToContextMap = new HashMap<String, ProfileContext>();
        pathToContextMap = new HashMap<String, ProfileContext>();
    }

    // Instance Accessors

    /**
     * Returns the id of the {@link ProfileContext} which tracks the profile based on the specified {@link File}.
     * <p>
     * 
     * @param file the {@link File} containing the Profiling Agent output
     * @return the id of the {@link ProfileContext} which tracks the profile based on the specified {@link File}
     */
    public Integer getContextIdByPath(File file)
    {
        ProfileContext ctx = pathToContextMap.get(file.getAbsolutePath());
        return ctx == null ? null : ctx.getId();
    }

    /**
     * Returns the internationalized String stored in the application {@link ResourceBundle} for the specified key based
     * on the current {@link Locale}.
     * <p>
     * 
     * @param key the key for the internationalized String in the application {@link ResourceBundle}
     * @return the internationalized String for the specified key
     */
    public String textFor(String key)
    {
        return currentBundle.getString(key);
    }

    /**
     * Returns the internationalized String constructed by looking up the specified key in the application
     * {@link ResourceBundle} based on the current {@link Locale}, interpreting it as a format and formatting it using
     * the specified argument.
     * <p>
     * 
     * @param key the key for the pattern in the application {@link ResourceBundle}
     * @param args the arguments for formatting the pattern
     * @return the constructed internationalized String
     */
    public String textFor(String key, Object... args)
    {
        return format(currentLocale, currentBundle, key, args);
    }

    /**
     * Set the text in the InfoBar as per {@link #textFor(String)}.
     * <p>
     * 
     * @param key the key for the internationalized String in the application {@link ResourceBundle}
     */
    public void setInfoFromBundle(String key)
    {
        info.set(textFor(key));
    }

    /**
     * Set the text in the InfoBar as per {@link #textFor(String, Object...)}.
     * <p>
     * 
     * @param key the key for the pattern in the application {@link ResourceBundle}
     * @param args the arguments for formatting the pattern
     */
    public void setInfoFromBundle(String key, Object... args)
    {
        info.set(textFor(key, args));
    }

    /**
     * Clears the text in the InfoBar.
     */
    public void clearInfo()
    {
        info.set("");
    }

    /**
     * Returns the InfoBar {@link ObservableStringValue}.
     * <p>
     * 
     * @return the InfoBar {@link ObservableStringValue}.
     */
    public ObservableStringValue getInfo()
    {
        return info;
    }

    /**
     * Returns the {@link ProfileContext} with the specified name.
     * <p>
     * 
     * @param name the name of the {@link ProfileContext}
     * @return the corresponding {@link ProfileContext}
     */
    public ProfileContext getProfileContext(String name)
    {
        return nameToContextMap.get(name);
    }

    /**
     * Registers a {@link ProfileContext} with this ApplicationContext, making it available as shared state.
     * <p>
     * 
     * @param context the {@link ProfileContext} to be registered
     */
    public void registerProfileContext(ProfileContext context)
    {
        nameToContextMap.put(context.getName(), context);
        pathToContextMap.put(context.getFile().getAbsolutePath(), context);
    }

    /**
     * Returns a list of the names of all known {@link ProfileContext}s.
     * <p>
     * 
     * @return a list of the names of all known {@link ProfileContext}s
     */
    public List<String> getOpenProfileNames()
    {
        return nameToContextMap.keySet().stream().sorted().collect(toList());
    }

    /**
     * Executes a task on a background worker thread.
     * <p>
     * 
     * @param task the task to be executed
     */
    public void execute(Task<?> task)
    {
        executorService.execute(task);
    }

    /**
     * Stop the executorService. If this isn't called on application shutdown, the application shutdown will be held up
     * for a while.
     */
    public void stop()
    {
        this.executorService.shutdown();
    }

    /**
     * Create a {@link Tab} containing the Diff Views for the specified profiles.
     * <p>
     * 
     * @param baseName the name of the {@link ProfileContext} for the Base profile
     * @param newName the name of the {@link ProfileContext} for the New profile
     */
    public void createDiffView(String baseName, String newName)
    {
        rootController.createDiffTab(baseName, newName);
    }
}
