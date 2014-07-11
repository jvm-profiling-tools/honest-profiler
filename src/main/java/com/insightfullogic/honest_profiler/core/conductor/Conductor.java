package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogRepo;
import com.insightfullogic.honest_profiler.core.store.LogSaver;

import java.io.File;
import java.io.IOException;

public class Conductor {

    private final LogRepo logRepo;

    public Conductor(LogRepo logRepo) {
        this.logRepo = logRepo;
    }

    public DataConsumer pipeData(VirtualMachine machine, ProfileListener listener) {
        return pipe(machine, listener, false);
    }

    public LogConsumer pipeFile(File file, VirtualMachine machine, ProfileListener listener) throws IOException {
        return new LogConsumer(file, pipe(machine, listener, true));
    }

    public void consumeFile(File file, VirtualMachine machine, ProfileListener listener) throws IOException {
        LogConsumer consumer = new LogConsumer(file, pipe(machine, listener, false));
        while (consumer.run())
            ;
    }

    private DataConsumer pipe(VirtualMachine machine, ProfileListener listener, boolean continuous) {
        LogSaver saver = logRepo.onNewLog(machine);
        LogCollector collector = new LogCollector(listener, continuous);
        return new DataConsumer(machine, saver, collector);
    }

}
