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
import javafx.util.Duration;

/**
 * A ProfileContext contains state which needs to be shared by all controllers created for the same profile, and methods
 * to access and change that state.
 */
public class ProfileContext
{
    // Class Properties

    /**
     * Enumeration listing the different profiling modes.
     */
    public static enum ProfileMode
    {
        /** Live profile, monitoring a running JVM. */
        LIVE,
        /** Log profile, read from a Log File produced by a finished Profiler Agent session. */
        LOG
    }

    private static final AtomicInteger counter = new AtomicInteger();

    // Instance Properties

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

    // While frozen, incoming profiles/graphs are cached in the following 2 instance properties.
    private LeanProfile cachedProfile;
    private FlameGraph cachedFlameGraph;

    // Instance Constructors

    /**
     * Constructor which specifies the {@link ApplicationContext}, the name of the context, its
     * {@link ProfileContext.ProfileMode} and the Log File containing the information emitted by the Profiler Agent.
     * <p>
     * @param appCtx the {@link ApplicationContext} for the application
     * @param name the name of the ProfileContext
     * @param mode the {@link ProfileContext.ProfileMode}
     * @param file the Log File containing the information emitted by the Profiler Agent
     */
    public ProfileContext(ApplicationContext appCtx, String name, ProfileMode mode, File file)
    {
        this.appCtx = appCtx;
        this.name = new SimpleStringProperty(name);
        this.mode = mode;
        this.file = file;

        profileSource = null;

        // Store a unique id for the ProfileContext
        id = counter.incrementAndGet();

        profile = new SimpleObjectProperty<>();
        flameGraph = new SimpleObjectProperty<>();

        refreshInterval = seconds(1);
    }

    // Instance Accessors

    /**
     * Returns the {@link File} containing the information emitted by the Profiler Agent.
     * <p>
     * @return the {@link File} containing the information emitted by the Profiler Agent
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Sets the {@link ProfileSource} which generates new {@link LeanProfile}s, and starts the polling mechanism.
     * <p>
     * @param profileSource the {@link ProfileSource} which generates new {@link LeanProfile}s
     */
    public void setProfileSource(ProfileSource profileSource)
    {
        this.profileSource = profileSource;
        newTimeline();
    }

    /**
     * Returns the interval, in seconds, at which the ProfileContext will request new {@link LeanProfile} instances from
     * the {@link ProfileSource}.
     * <p>
     * @return the interval, in seconds, at which the ProfileContext will request new {@link LeanProfile} instances
     */
    public int getDuration()
    {
        return (int)refreshInterval.toSeconds();
    }

    /**
     * Sets the interval, in seconds, at which the ProfileContext will request new {@link LeanProfile} instances from
     * the {@link ProfileSource}.
     * <p>
     * @param seconds the interval, in seconds, at which the ProfileContext will request new {@link LeanProfile}
     *            instances
     */
    public void setDuration(int seconds)
    {
        refreshInterval = seconds(seconds);
        updateTimeline();
    }

    /**
     * Returns the unique id of this ProfileContext.
     * <p>
     * @return the unique id of this ProfileContext
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the name of this ProfileContext.
     * <p>
     * @return the name of this ProfileContext
     */
    public String getName()
    {
        return name.get();
    }

    /**
     * Returns the {@link ProfileMode} for this ProfileContext.
     * <p>
     * @return the {@link ProfileMode} for this ProfileContext
     */
    public ProfileMode getMode()
    {
        return mode;
    }

    /**
     * Returns the current {@link AggregationProfile}.
     * <p>
     * @return the current {@link AggregationProfile}
     */
    public AggregationProfile getProfile()
    {
        return profile.get();
    }

    /**
     * Returns the {@link ObjectProperty} encapsulating the current {@link AggregationProfile}.
     * <p>
     * @return the {@link ObjectProperty} encapsulating the current {@link AggregationProfile}
     */
    public ObjectProperty<AggregationProfile> profileProperty()
    {
        return profile;
    }

    /**
     * Returns the {@link ObjectProperty} encapsulating the current {@link FlameGraph}.
     * <p>
     * @return the {@link ObjectProperty} encapsulating the current {@link FlameGraph}
     */
    public ObjectProperty<FlameGraph> flameGraphProperty()
    {
        return flameGraph;
    }

    /**
     * Returns a boolean indicating whether the ProfileContext is currently frozen, i.e. not requesting any
     * {@link LeanProfile} updates from the {@link ProfileSource}.
     * <p>
     * @return a boolean indicating whether the ProfileContext is currently frozen
     */
    public boolean isFrozen()
    {
        return frozen;
    }

    /**
     * Freezes or unfreezes the {@link ProfileContext}. This method may only be called on the FX thread.
     * <p>
     * @param freeze a boolean indicating whether the ProfileContext should be frozen
     */
    public void setFrozen(boolean freeze)
    {
        frozen = freeze;
        if (freeze)
        {
            // The timeline is the timing mechanism which will request LeanProfiles at a rate specified by the refresh
            // interval.
            timeline.pause();
        }
        else
        {
            // The timeline is the timing mechanism which will request LeanProfiles at a rate specified by the refresh
            // interval.
            timeline.play();

            // If any unrequested LeanProfiles were emitted by the ProfileSource while frozen (which should only happen
            // if the Profiler Agent has finished, which will trigger an end-of-log event), these are cached in the
            // cachedProfile property. When unfreezing, such cached profiles are processed here, to ensure the most
            // recent emitted LeanProfile is definitely shown.
            appCtx.execute(new AggregateProfileTask(ProfileContext.this, cachedProfile));

            if (cachedFlameGraph != null)
            {
                update(cachedFlameGraph);
                cachedFlameGraph = null;
            }
        }
    }

    /**
     * Returns a {@link LeanProfileListener} which accepts new emitted {@link LeanProfile}s and updates the
     * ProfileContext accordingly.
     * <p>
     * @return a {@link LeanProfileListener} which accepts new emitted {@link LeanProfile}s
     */
    public LeanProfileListener getProfileListener()
    {
        return new LeanProfileListener()
        {
            @Override
            public void accept(LeanProfile profile)
            {
                // Don't do anything in trivial situations.
                if (profile == null || profile == cachedProfile)
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

    /**
     * Returns a {@link FlameGraphListener} which accepts new emitted {@link FlameGraph}s and updates the ProfileContext
     * accordingly.
     * <p>
     * @return a {@link FlameGraphListener} which accepts new emitted {@link FlameGraph}s
     */
    public FlameGraphListener getFlameGraphListener()
    {
        return new FlameGraphListener()
        {
            @Override
            public void accept(FlameGraph t)
            {
                // Ensure the update is run on the FX Thread.
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

    /**
     * Update the {@link AggregationProfile} {@link ObjectProperty}. This method may only be called on the FX thread.
     * <p>
     * @param profile the new {@link AggregationProfile}
     */
    public void update(AggregationProfile profile)
    {
        this.profile.set(profile);
    }

    /**
     * Update the {@link FlameGraph} {@link ObjectProperty} if the ProfileContext is not frozen, or cache it if frozen.
     * This method may only be called on the FX thread.
     * <p>
     * @param flameGraph the new {@link FlameGraph}
     */
    private void update(FlameGraph flameGraph)
    {
        if (frozen)
        {
            cachedFlameGraph = flameGraph;
        }
        else
        {
            this.flameGraph.set(flameGraph);
        }
    }

    /**
     * Stops the current {@link Timeline}, and ensures that the moment it stops, it starts a new {@link Timeline} which
     * will pick up the currently set refresh rate.
     */
    private void updateTimeline()
    {
        // Since a TimeLine is not guaranteed to stop immediately, we use this mechanism that a new TimeLine is started
        // only when the Timeline has definitely stopped, to avoid any potential concurrency issues.
        timeline.setOnFinished(event -> newTimeline());

        // Aaaaand... Cut !
        timeline.stop();
    }

    /**
     * Start a new {@link Timeline} which will request {@link LeanProfile} updates from the {@link ProfileSource} at the
     * rate specified by the refresh interval.
     */
    private void newTimeline()
    {
        timeline = new Timeline(
            new KeyFrame(refreshInterval, e -> profileSource.requestProfile()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
