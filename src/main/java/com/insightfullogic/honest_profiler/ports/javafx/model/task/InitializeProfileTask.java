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
import com.insightfullogic.honest_profiler.ports.javafx.controller.FlameViewController;
import com.insightfullogic.honest_profiler.ports.javafx.model.ApplicationContext;
import com.insightfullogic.honest_profiler.ports.javafx.model.ProfileContext;
import com.insightfullogic.honest_profiler.ports.sources.FileLogSource;

import javafx.concurrent.Task;

public class InitializeProfileTask extends Task<Void>
{
    private final ApplicationContext applicationContext;
    private final ProfileContext profileContext;
    private final Object source;
    private final boolean live;
    private final FlameViewController flameController;

    public InitializeProfileTask(ApplicationContext applicationContext,
                                 ProfileContext profileContext,
                                 Object source,
                                 boolean live,
                                 FlameViewController flameController)
    {
        super();
        this.applicationContext = applicationContext;
        this.profileContext = profileContext;
        this.source = source;
        this.live = live;
        this.flameController = flameController;
    }

    @Override
    protected Void call() throws Exception
    {
        if (source instanceof VirtualMachine)
        {
            VirtualMachine vm = (VirtualMachine) source;
            String vmName = vm.getDisplayName();
            profileContext.setName(
                (vmName.contains(" ") ? vmName.substring(0, vmName.indexOf(" ")) : vmName)
                    + " ("
                    + vm.getId()
                    + ")");
            profileContext.setMode(LIVE);
            monitor((VirtualMachine) source);
        }
        else if (live)
        {
            File file = (File) source;
            profileContext.setName(file.getName());
            profileContext.setMode(LIVE);
            monitor(file);
        }
        else
        {
            File file = (File) source;
            profileContext.setName(file.getName());
            profileContext.setMode(LOG);
            openFile((File) source);
        }
        applicationContext.registerProfileContext(profileContext);
        return null;
    }

    private void openFile(File file)
    {
        final LogEventListener collector = new LogEventPublisher()
            .publishTo(new LogCollector(profileContext, false))
            .publishTo(new FlameGraphCollector(flameController));
        pipe(new FileLogSource(file), collector, false).run();
    }

    private void monitor(File file)
    {
        try
        {
            pipeFile(new FileLogSource(file), profileContext);
        }
        catch (CantReadFromSourceException crfse)
        {
            throw new RuntimeException(crfse.getMessage(), crfse);
        }
    }

    private void monitor(VirtualMachine machine)
    {
        try
        {
            pipeFile(machine.getLogSourceFromVmArgs(), profileContext);
        }
        catch (CantReadFromSourceException crfse)
        {
            throw new RuntimeException(crfse.getMessage(), crfse);
        }
    }
}
