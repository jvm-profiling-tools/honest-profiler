package com.insightfullogic.honest_profiler.ports.javafx.model;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.application.Platform.runLater;
import static javafx.util.Duration.seconds;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import com.insightfullogic.honest_profiler.core.aggregation.AggregationProfile;
import com.insightfullogic.honest_profiler.core.collector.lean.ProfileSource;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraph;
import com.insightfullogic.honest_profiler.core.profiles.FlameGraphListener;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;
import com.insightfullogic.honest_profiler.ports.javafx.model.task.AggregateProfileTask;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

public class ProfileContext
{
    public static enum ProfileMode
    {
        LIVE, LOG
    }

    private static final AtomicInteger counter = new AtomicInteger();

    private final ApplicationContext appCtx;

    private final int id;
    private final SimpleStringProperty name;
    private final File file;

    private final ProfileMode mode;
    private ProfileSource profileSource;

    private final SimpleObjectProperty<AggregationProfile> profile;
    private final SimpleObjectProperty<FlameGraph> flameGraph;

    private boolean frozen;

    private Duration refreshInterval;
    private Timeline timeline;

    // While frozen, incoming profiles/graphs are cached in the following 2
    // instance properties.
    private LeanProfile cachedProfile;
    private FlameGraph cachedFlameGraph;

    public ProfileContext(ApplicationContext appCtx, String name, ProfileMode mode, File file)
    {
        this.appCtx = appCtx;
        this.name = new SimpleStringProperty(name);
        this.mode = mode;
        this.file = file;

        profileSource = null;

        id = counter.incrementAndGet();
        profile = new SimpleObjectProperty<>();
        flameGraph = new SimpleObjectProperty<>();

        refreshInterval = seconds(1);
    }

    public File getFile()
    {
        return file;
    }

    public void setProfileSource(ProfileSource profileSource)
    {
        this.profileSource = profileSource;
        newTimeline();
    }

    public int getDuration()
    {
        return (int)refreshInterval.toSeconds();
    }

    public void setDuration(int seconds)
    {
        refreshInterval = seconds(seconds);
        updateTimeline();
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

    public AggregationProfile getProfile()
    {
        return profile.get();
    }

    public StringProperty profileNameProperty()
    {
        return name;
    }

    public ObjectProperty<AggregationProfile> profileProperty()
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
        frozen = freeze;
        if (!freeze)
        {
            timeline.play();
            appCtx.execute(new AggregateProfileTask(ProfileContext.this, cachedProfile));

            if (cachedFlameGraph != null)
            {
                update(cachedFlameGraph);
                cachedFlameGraph = null;
            }
        }
        else
        {
            timeline.pause();
        }
    }

    public LeanProfileListener getProfileListener()
    {
        return new LeanProfileListener()
        {
            @Override
            public void accept(LeanProfile profile)
            {
                if (profile == null)
                {
                    return;
                }

                if (profile == cachedProfile)
                {
                    return;
                }

                cachedProfile = profile;
                if (!frozen)
                {
                    appCtx.execute(new AggregateProfileTask(ProfileContext.this, profile));
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
    public void update(AggregationProfile profile)
    {
        this.profile.set(profile);
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

    private void updateTimeline()
    {
        timeline.setOnFinished(event -> newTimeline());
        timeline.stop();

    }

    private void newTimeline()
    {
        timeline = new Timeline(
            new KeyFrame(refreshInterval, e -> profileSource.requestProfile()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
