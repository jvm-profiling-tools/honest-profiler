package com.insightfullogic.honest_profiler.ports.javafx.model.task;

import static com.insightfullogic.honest_profiler.core.Monitor.pipe;
import static com.insightfullogic.honest_profiler.core.Monitor.pipeFile;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LIVE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;

import java.io.File;

import com.insightfullogic.honest_profiler.core.collector.FlameGraphCollector;
import com.insightfullogic.honest_profiler.core.collector.lean.LeanLogCollector;
import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.LogEventPublisher;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;

import javafx.concurrent.Task;

/**
 * Background task which opens the log file produced by a Profiler Agent, chains together the file, file parser and
 * {@link LeanLogCollector}, and creates a {@link ProfileContext} for the profile.
 */
public class InitializeProfileTask extends Task<ProfileContext>
{
    // Instance Properties

    private final ApplicationContext appCtx;
    private final Object source;
    private final boolean live;

    // Instance Constructors

    /**
     * Constructor which specifies the {@link ApplicationContext}, the source of the profile (either a live VM or a log
     * file) and an indication whether the monitoring is "live" or not (i.e. whether the Profiler Agent generating the
     * source is still running and appending data).
     * <p>
     * @param applicationContext the {@link ApplicationContext} for the application
     * @param source the source {@link VirtualMachine} or {@link FileLogSource}
     * @param live a boolean indicating whether the log file is "live"
     */
    public InitializeProfileTask(ApplicationContext applicationContext, Object source, boolean live)
    {
        super();
        appCtx = applicationContext;
        this.source = source;
        this.live = live;
    }

    @Override
    protected ProfileContext call() throws Exception
    {
        FileLogSource fileLogSource = getLogSource();

        return (source instanceof VirtualMachine || live) ? monitor(fileLogSource)
            : consume(fileLogSource);
    }

    // Guaranteed to be called on the FX thread.
    @Override
    protected void succeeded()
    {
        super.succeeded();
        // We do this on the FX thread to avoid concurrency issues with the ProfileContext map read/write access in the
        // ApplicationContext.
        appCtx.registerProfileContext(this.getValue());
    }

    // Guaranteed to be called on the FX thread.
    @Override
    protected void failed()
    {
        super.failed();
        // This should be shown in an error dialog someday instead.
        getException().printStackTrace();
    }

    /**
     * Create a new {@link ProfileContext} instance.
     * <p>
     * @param mode the {@link ProfileContext.ProfileMode} for the {@link ProfileContext}
     * @param fileLogSource the {@link FileLogSource} exposing the log file from the Profiler Agent
     * @return a new {@link ProfileContext}
     */
    private ProfileContext newProfileContext(ProfileMode mode, FileLogSource fileLogSource)
    {
        return new ProfileContext(appCtx, getName(), mode, fileLogSource.getFile());
    }

    /**
     * Returns a {@link LeanLogCollector} which emits {@link LeanProfile}s to the specified {@link ProfileContext}.
     * <p>
     * @param context the {@link ProfileContext} which will receive the emitted {@link LeanProfile}s
     * @return a new {@link LeanLogCollector}
     */
    private LeanLogCollector getCollector(ProfileContext context)
    {
        return new LeanLogCollector(context.getProfileListener());
    }

    /**
     * Returns either the original source specified on {@link Task} creation if it is already a {@link FileLogSource},
     * or it gets the {@link FileLogSource} from the source {@link VirtualMachine}.
     * <p>
     * @return the {@link FileLogSource} for the profile specified by the task source
     */
    private FileLogSource getLogSource()
    {
        return (source instanceof VirtualMachine)
            ? (FileLogSource)((VirtualMachine)source).getLogSourceFromVmArgs()
            : new FileLogSource((File)source);
    }

    /**
     * Return a name constructed from information from the task source Object.
     * <p>
     * @return a name based on the task source Object
     */
    private String getName()
    {
        return (source instanceof VirtualMachine) ? getVmName((VirtualMachine)source)
            : ((File)source).getName();

    }

    /**
     * Returns a {@link ProfileContext} which monitors {@link LeanProfile}s emitted by a {@link LeanLogCollector} based
     * on a live log file.
     * <p>
     * @param fileLogSource the live log file from which the log events for constructing the {@link LeanProfile} are
     *            sourced
     * @return a new {@link ProfileContext} for live monitoring
     */
    private ProfileContext monitor(FileLogSource fileLogSource)
    {
        ProfileContext profileContext = newProfileContext(LIVE, fileLogSource);
        LeanLogCollector collector = getCollector(profileContext);
        profileContext.setProfileSource(collector);
        pipeFile(fileLogSource, collector, profileContext.getProfileListener());

        return profileContext;
    }

    /**
     * Returns a {@link ProfileContext} which will emit {@link LeanProfile}s produced by consuming a non-live log file.
     * <p>
     * @param fileLogSource the non-live log file which will be processed
     * @return a new {@link ProfileContext} for non-live log file comsumption
     */
    private ProfileContext consume(FileLogSource fileLogSource)
    {
        ProfileContext profileContext = newProfileContext(LOG, fileLogSource);
        final LogEventListener collector = new LogEventPublisher()
            // Multiplex Log Events to the LeanLogCollector
            .publishTo(getCollector(profileContext))
            // Multiplex Log Events to the FlameGraphCollector
            .publishTo(new FlameGraphCollector(profileContext.getFlameGraphListener()));

        pipe(fileLogSource, collector, false).run();

        return profileContext;
    }

    /**
     * Creates a name for a {@link VirtualMachine} which will be monitored.
     * <p>
     * @param vm the {@link VirtualMachine} which will be monitored
     * @return a name for the {@link VirtualMachine}
     */
    private String getVmName(VirtualMachine vm)
    {
        String name = vm.getDisplayName();

        StringBuilder result = new StringBuilder();
        result.append((name.contains(" ")) ? name.substring(0, name.indexOf(" ")) : name);
        result.append(" (").append(vm.getId()).append(")");
        return result.toString();
    }
}
