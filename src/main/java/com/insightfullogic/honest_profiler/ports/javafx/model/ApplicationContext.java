package com.insightfullogic.honest_profiler.ports.javafx.model;

import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.format;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.getDefaultBundle;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.getDefaultLocale;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

import com.insightfullogic.honest_profiler.ports.javafx.controller.RootController;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;

public final class ApplicationContext
{
    private Locale currentLocale;
    private ResourceBundle currentBundle;

    private SimpleStringProperty info;
    private Map<String, ProfileContext> profileContextMap;
    private RootController rootController;

    private ExecutorService executorService = newCachedThreadPool();

    public ApplicationContext(RootController rootController)
    {
        currentLocale = getDefaultLocale();
        currentBundle = getDefaultBundle();
        info = new SimpleStringProperty();
        this.rootController = rootController;
        profileContextMap = new HashMap<String, ProfileContext>();
    }

    public String textFor(String key)
    {
        return currentBundle.getString(key);
    }

    public String textFor(String key, Object... args)
    {
        return format(currentLocale, currentBundle, key, args);
    }

    public void setInfoFromBundle(String key)
    {
        info.set(textFor(key));
    }

    public void setInfoFromBundle(String key, Object... args)
    {
        info.set(textFor(key, args));
    }

    public void setInfo(String message)
    {
        info.set(message);
    }

    public ObservableStringValue getInfo()
    {
        return info;
    }

    public ProfileContext getProfileContext(String name)
    {
        return profileContextMap.get(name);
    }

    public void registerProfileContext(ProfileContext context)
    {
        profileContextMap.put(context.getName(), context);
    }

    public List<String> getOpenProfileNames()
    {
        return profileContextMap.keySet().stream().sorted().collect(toList());
    }

    public ExecutorService getExecutorService()
    {
        return executorService;
    }

    public void createDiffView(String baseName, String newName)
    {
        rootController.generateDiffTab(baseName, newName);
    }
}
