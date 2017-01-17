package com.insightfullogic.honest_profiler.ports.javafx.model.task;

import static com.insightfullogic.honest_profiler.core.Monitor.pipe;
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
        this.appCtx = applicationContext;
        this.source = source;
        this.live = live;
    }

    @Override
    protected ProfileContext call() throws Exception
    {
        ProfileContext profileContext;

        if (source instanceof VirtualMachine)
        {
            VirtualMachine vm = (VirtualMachine) source;
            profileContext = new ProfileContext(appCtx, convertVmName(vm), LIVE);
            LeanLogCollector collector = getCollector(profileContext);
            profileContext.setProfileSource(collector);
            pipe(vm.getLogSourceFromVmArgs(), collector, true).run();
        }
        else if (live)
        {
            File file = (File) source;
            profileContext = new ProfileContext(appCtx, file.getName(), LIVE);
            LeanLogCollector collector = getCollector(profileContext);
            profileContext.setProfileSource(collector);
            pipe(new FileLogSource(file), collector, true).run();
        }
        else
        {
            File file = (File) source;
            profileContext = new ProfileContext(appCtx, file.getName(), LOG);
            final LogEventListener collector = new LogEventPublisher()
                .publishTo(getCollector(profileContext))
                .publishTo(new FlameGraphCollector(profileContext.getFlameGraphListener()));
            pipe(new FileLogSource(file), collector, false).run();
        }

        appCtx.registerProfileContext(profileContext);
        return profileContext;
    }

    private LeanLogCollector getCollector(ProfileContext context)
    {
        return new LeanLogCollector(context.getProfileListener());
    }

    private String convertVmName(VirtualMachine vm)
    {
        String name = vm.getDisplayName();

        StringBuilder result = new StringBuilder();
        result.append((name.contains(" ")) ? name.substring(0, name.indexOf(" ")) : name);
        result.append(" (").append(vm.getId()).append(")");
        return result.toString();
    }
}
