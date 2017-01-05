package com.insightfullogic.honest_profiler.ports.javafx.controller;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public abstract class ProfileViewController<T> extends AbstractViewController
{

    private ProfileContext profileContext;

    private ObjectProperty<T> target;
    private Function<ProfileContext, ObservableValue<T>> targetExtractor;

    protected void initialize(Function<ProfileContext, ObservableValue<T>> targetExtractor,
        Button filterButton, Button quickFilterButton, TextField quickFilterText)
    {
        super.initialize(filterButton, quickFilterButton, quickFilterText);

        this.targetExtractor = targetExtractor;
        target = new SimpleObjectProperty<>();
        target.addListener((property, oldValue, newValue) -> refresh());
    }

    protected ProfileContext prContext()
    {
        return profileContext;
    }

    public void setProfileContext(ProfileContext profileContext)
    {
        this.profileContext = profileContext;
    }

    protected T getTarget()
    {
        return target.get();
    }

    // Activation

    public void activate()
    {
        target.bind(targetExtractor.apply(profileContext));
    }

    public void deactivate()
    {
        target.unbind();
    }
}
