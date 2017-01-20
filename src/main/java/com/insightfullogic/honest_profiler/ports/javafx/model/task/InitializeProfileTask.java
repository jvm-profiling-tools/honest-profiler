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
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;

import javafx.concurrent.Task;

public class InitializeProfileTask extends Task<ProfileContext>
{
    private final ApplicationContext appCtx;
    private final Object source;
    private final boolean live;

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

        ProfileContext context = (source instanceof VirtualMachine || live)
            ? monitor(fileLogSource) : consume(fileLogSource);

        appCtx.registerProfileContext(context);
        return context;
    }

    @Override
    protected void failed()
    {
        super.failed();
        getException().printStackTrace();
    }

    private ProfileContext newProfileContext(ProfileMode mode, FileLogSource fileLogSource)
    {
        return new ProfileContext(appCtx, getName(), mode, fileLogSource.getFile());
    }

    private LeanLogCollector getCollector(ProfileContext context)
    {
        return new LeanLogCollector(context.getProfileListener());
    }

    private FileLogSource getLogSource()
    {
        return (source instanceof VirtualMachine)
            ? (FileLogSource) ((VirtualMachine) source).getLogSourceFromVmArgs()
            : new FileLogSource((File) source);
    }

    private String getName()
    {
        return (source instanceof VirtualMachine) ? getVmName((VirtualMachine) source)
            : ((File) source).getName();

    }

    private ProfileContext monitor(FileLogSource fileLogSource)
    {
        ProfileContext profileContext = newProfileContext(LIVE, fileLogSource);
        LeanLogCollector collector = getCollector(profileContext);
        profileContext.setProfileSource(collector);
        pipeFile(fileLogSource, collector, profileContext.getProfileListener());

        return profileContext;
    }

    private ProfileContext consume(FileLogSource fileLogSource)
    {
        ProfileContext profileContext = newProfileContext(LOG, fileLogSource);
        final LogEventListener collector = new LogEventPublisher()
            .publishTo(getCollector(profileContext))
            .publishTo(new FlameGraphCollector(profileContext.getFlameGraphListener()));
        pipe(fileLogSource, collector, false).run();

        return profileContext;
    }

    private String getVmName(VirtualMachine vm)
    {
        String name = vm.getDisplayName();

        StringBuilder result = new StringBuilder();
        result.append((name.contains(" ")) ? name.substring(0, name.indexOf(" ")) : name);
        result.append(" (").append(vm.getId()).append(")");
        return result.toString();
    }
}
