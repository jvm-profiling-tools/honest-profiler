package com.insightfullogic.honest_profiler.ports.javafx.controller;

import java.util.function.Function;

import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public abstract class ProfileViewController<T> extends AbstractController
{

    private ProfileContext profileContext;

    private ObjectProperty<T> target;
    private Function<ProfileContext, ObservableValue<T>> targetExtractor;

    protected void initialize(Function<ProfileContext, ObservableValue<T>> targetExtractor)
    {
        super.initialize();

        this.targetExtractor = targetExtractor;
        target = new SimpleObjectProperty<>();
        target.addListener((property, oldValue, newValue) -> refresh(newValue));
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

    protected abstract void refresh(T target);
}
