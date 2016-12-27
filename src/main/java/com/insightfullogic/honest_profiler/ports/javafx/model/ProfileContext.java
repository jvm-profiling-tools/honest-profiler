package com.insightfullogic.honest_profiler.ports.javafx.model;

import static java.util.Arrays.asList;
import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProfileContext implements ProfileListener
{
    private final Logger logger;

    private ApplicationContext applicationContext;

    private SimpleStringProperty name;
    private SimpleObjectProperty<Profile> profile;
    private List<ProfileListener> listeners;

    public ProfileContext()
    {
        logger = getLogger(ProfileContext.class);

        name = new SimpleStringProperty();
        profile = new SimpleObjectProperty<>();
        listeners = new ArrayList<>();
    }

    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public String getName()
    {
        return name.get();
    }

    public void setName(String name)
    {
        this.name.set(name);
    }

    public Profile getProfile()
    {
        return profile.get();
    }

    public StringProperty profileNameProperty()
    {
        return name;
    }

    public ObjectProperty<Profile> profileProperty()
    {
        return profile;
    }

    public void setProfile(Profile profile)
    {
        this.profile.set(profile);
    }

    public void addListener(ProfileListener listener)
    {
        listeners.add(listener);
    }

    public void addListeners(ProfileListener... listeners)
    {
        this.listeners.addAll(asList(listeners));
    }

    @Override
    public void accept(Profile profile)
    {
        // All UI updates must go through here.
        onFxThread(() ->
        {
            this.profile.set(profile);

            try
            {
                listeners.forEach(listener -> listener.accept(profile));
            }
            catch (Throwable t)
            {
                logger.error(t.getMessage(), t);
            }
        });
    }

    // Controllers can happily update the UI without worrying about threading
    // implications
    private void onFxThread(final Runnable block)
    {
        if (isFxApplicationThread())
        {
            block.run();
        }
        else
        {
            runLater(block);
        }
    }
}
