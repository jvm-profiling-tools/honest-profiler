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

public final class ApplicationContext
{
    private Locale currentLocale;
    private ResourceBundle currentBundle;

    private SimpleStringProperty info;
    private Map<String, ProfileContext> nameToContextMap;
    private Map<String, ProfileContext> pathToContextMap;
    private RootController rootController;

    private ExecutorService executorService = newCachedThreadPool();

    public ApplicationContext(RootController rootController)
    {
        currentLocale = getDefaultLocale();
        currentBundle = getDefaultBundle();
        info = new SimpleStringProperty();
        this.rootController = rootController;
        nameToContextMap = new HashMap<String, ProfileContext>();
        pathToContextMap = new HashMap<String, ProfileContext>();
    }

    public Integer getContextIdByPath(File file)
    {
        ProfileContext ctx = pathToContextMap.get(file.getAbsolutePath());
        return ctx == null ? null : ctx.getId();
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
        return nameToContextMap.get(name);
    }

    public void registerProfileContext(ProfileContext context)
    {
        nameToContextMap.put(context.getName(), context);
        pathToContextMap.put(context.getFile().getAbsolutePath(), context);
    }

    public List<String> getOpenProfileNames()
    {
        return nameToContextMap.keySet().stream().sorted().collect(toList());
    }

    public void execute(Task<?> task)
    {
        executorService.execute(task);
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
