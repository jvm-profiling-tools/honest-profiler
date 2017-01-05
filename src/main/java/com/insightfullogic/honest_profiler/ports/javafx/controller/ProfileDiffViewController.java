package com.insightfullogic.honest_profiler.ports.javafx.controller;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public abstract class ProfileDiffViewController<T> extends AbstractViewController
{
    private ProfileContext baseContext;
    private ProfileContext newContext;

    private ObjectProperty<T> baseTarget;
    private ObjectProperty<T> newTarget;
    private Function<ProfileContext, ObservableValue<T>> targetExtractor;

    protected void initialize(Function<ProfileContext, ObservableValue<T>> targetExtractor,
        Button filterButton, Button quickFilterButton, TextField quickFilterText)
    {
        super.initialize(filterButton, quickFilterButton, quickFilterText);

        this.targetExtractor = targetExtractor;
        baseTarget = new SimpleObjectProperty<>();
        newTarget = new SimpleObjectProperty<>();
        baseTarget.addListener((property, oldValue, newValue) -> refresh());
        newTarget.addListener((property, oldValue, newValue) -> refresh());
    }

    protected ProfileContext baseContext()
    {
        return baseContext;
    }

    protected ProfileContext newContext()
    {
        return newContext;
    }

    protected T getBaseTarget()
    {
        return baseTarget.get();
    }

    protected T getNewTarget()
    {
        return newTarget.get();
    }

    public void setProfileContexts(ProfileContext baseContext, ProfileContext newContext)
    {
        this.baseContext = baseContext;
        this.newContext = newContext;
    }

    // Activation

    public void activate()
    {
        baseTarget.bind(targetExtractor.apply(baseContext));
        newTarget.bind(targetExtractor.apply(newContext));
    }

    public void deactivate()
    {
        baseTarget.unbind();
        newTarget.unbind();
    }
}
