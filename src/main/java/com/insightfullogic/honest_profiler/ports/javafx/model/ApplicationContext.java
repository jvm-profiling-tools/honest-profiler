package com.insightfullogic.honest_profiler.ports.javafx.model;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insightfullogic.honest_profiler.ports.javafx.controller.RootController;

public class ApplicationContext
{
    private Map<String, ProfileContext> profileContextMap;
    private RootController rootController;

    public ApplicationContext(RootController rootController)
    {
        this.rootController = rootController;
        profileContextMap = new HashMap<String, ProfileContext>();
    }

    public ProfileContext getProfileContext(String name)
    {
        return profileContextMap.get(name);
    }

    public void registerProfileContext(ProfileContext context)
    {
        context.setApplicationContext(this);
        profileContextMap.put(context.getName(), context);
    }

    public List<String> getOpenProfileNames()
    {
        return profileContextMap.keySet().stream().sorted().collect(toList());
    }

    public void createDiffView(String baseName, String newName)
    {
        rootController.generateDiffTab(baseName, newName);
    }
}
