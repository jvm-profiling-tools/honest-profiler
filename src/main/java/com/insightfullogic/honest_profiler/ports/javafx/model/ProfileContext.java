package com.insightfullogic.honest_profiler.ports.javafx.model;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;

import java.util.concurrent.atomic.AtomicInteger;

import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraphListener;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProfileContext
{
    public static enum ProfileMode
    {
        LIVE, LOG
    }

    private static final AtomicInteger counter = new AtomicInteger();

    private final int id;

    private final SimpleStringProperty name;
    private final ProfileMode mode;
    private final SimpleObjectProperty<Profile> profile;
    private final SimpleObjectProperty<FlameGraph> flameGraph;

    private boolean frozen;
    // While frozen, incoming profiles/graphs are cached in the following 2
    // instance properties.
    private Profile cachedProfile;
    private FlameGraph cachedFlameGraph;

    public ProfileContext(String name, ProfileMode mode)
    {
        id = counter.incrementAndGet();
        this.name = new SimpleStringProperty(name);
        this.mode = mode;
        profile = new SimpleObjectProperty<>();
        flameGraph = new SimpleObjectProperty<>();
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name.get();
    }

    public ProfileMode getMode()
    {
        return mode;
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

    public ObjectProperty<FlameGraph> flameGraphProperty()
    {
        return flameGraph;
    }

    public boolean isFrozen()
    {
        return frozen;
    }

    // Call only on FX Thread !
    public void setFrozen(boolean freeze)
    {
        this.frozen = freeze;
        if (!freeze)
        {
            if (cachedProfile != null)
            {
                update(cachedProfile);
                cachedProfile = null;
            }

            if (cachedFlameGraph != null)
            {
                update(cachedFlameGraph);
                cachedFlameGraph = null;
            }
        }
    }

    public ProfileListener getProfileListener()
    {
        return new ProfileListener()
        {
            @Override
            public void accept(Profile t)
            {
                if (isFxApplicationThread())
                {
                    update(t);
                }
                else
                {
                    runLater(() -> update(t));
                }
            }
        };
    }

    public FlameGraphListener getFlameGraphListener()
    {
        return new FlameGraphListener()
        {
            @Override
            public void accept(FlameGraph t)
            {
                if (isFxApplicationThread())
                {
                    update(t);
                }
                else
                {
                    runLater(() -> update(t));
                }
            }
        };
    }

    // Call only on FX Thread !
    private void update(Profile t)
    {
        if (frozen)
        {
            cachedProfile = t;
        }
        else
        {
            profile.set(t);
        }
    }

    private void update(FlameGraph t)
    {
        if (frozen)
        {
            cachedFlameGraph = t;
        }
        else
        {
            flameGraph.set(t);
        }
    }
}
