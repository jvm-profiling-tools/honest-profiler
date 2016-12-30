package com.insightfullogic.honest_profiler.ports.javafx.model;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.insightfullogic.honest_profiler.ports.javafx.controller.RootController;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;

public class ApplicationContext
{
    private SimpleStringProperty info;
    private Map<String, ProfileContext> profileContextMap;
    private RootController rootController;

    private ExecutorService executorService = newCachedThreadPool();

    public ApplicationContext(RootController rootController)
    {
        info = new SimpleStringProperty();
        this.rootController = rootController;
        profileContextMap = new HashMap<String, ProfileContext>();
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
