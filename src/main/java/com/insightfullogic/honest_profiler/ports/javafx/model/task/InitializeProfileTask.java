package com.insightfullogic.honest_profiler.ports.javafx.model.task;

import static com.insightfullogic.honest_profiler.core.Monitor.pipe;
import static com.insightfullogic.honest_profiler.core.Monitor.pipeFile;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LIVE;
import static com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext.ProfileMode.LOG;

import java.io.File;

import com.insightfullogic.honest_profiler.core.collector.FlameGraphCollector;
import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.parser.LogEventListener;
import com.insightfullogic.honest_profiler.core.parser.LogEventPublisher;
import com.insightfullogic.honest_profiler.core.sources.CantReadFromSourceException;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;

import javafx.concurrent.Task;

public class InitializeProfileTask extends Task<ProfileContext>
{
    private final ApplicationContext applicationContext;
    private final Object source;
    private final boolean live;

    public InitializeProfileTask(ApplicationContext applicationContext, Object source, boolean live)
    {
        super();
        this.applicationContext = applicationContext;
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
            profileContext = new ProfileContext(convertVmName(vm), LIVE);
            monitor(profileContext, (VirtualMachine) source);
        }
        else if (live)
        {
            File file = (File) source;
            profileContext = new ProfileContext(file.getName(), LIVE);
            monitor(profileContext, file);
        }
        else
        {
            File file = (File) source;
            profileContext = new ProfileContext(file.getName(), LOG);
            openFile(profileContext, (File) source);
        }

        applicationContext.registerProfileContext(profileContext);
        return profileContext;
    }

    private void openFile(ProfileContext profileContext, File file)
    {
        final LogEventListener collector = new LogEventPublisher()
            .publishTo(new LogCollector(profileContext.getProfileListener(), false))
            .publishTo(new FlameGraphCollector(profileContext.getFlameGraphListener()));
        pipe(new FileLogSource(file), collector, false).run();
    }

    private void monitor(ProfileContext profileContext, File file)
        throws CantReadFromSourceException
    {
        pipeFile(new FileLogSource(file), profileContext.getProfileListener());
    }

    private void monitor(ProfileContext profileContext, VirtualMachine machine)
    {
        pipeFile(machine.getLogSourceFromVmArgs(), profileContext.getProfileListener());
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
