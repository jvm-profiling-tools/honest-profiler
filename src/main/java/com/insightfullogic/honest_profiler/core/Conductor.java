package com.insightfullogic.honest_profiler.core;

import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogRepo;
import com.insightfullogic.honest_profiler.core.store.LogSaver;

import java.io.File;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

// TODO: decouple saving from parsing/processing (Possibly remove this class)
public class Conductor {

    private final LogRepo logRepo;

    public Conductor(LogRepo logRepo) {
        this.logRepo = logRepo;
    }

    public DataConsumer pipeData(VirtualMachine machine, ProfileListener listener) {
        return pipe(machine, listener, false);
    }

    public void pipeFile(File file, VirtualMachine machine, ProfileListener listener) throws IOException {
        final LogConsumer logConsumer = new LogConsumer(getLogger(LogConsumer.class), file, pipe(machine, listener, true), true);
        new ThreadedAgent(getLogger(ThreadedAgent.class), logConsumer::run).start();
    }

    public void consumeFile(File file, VirtualMachine machine, ProfileListener listener) throws IOException {
        LogConsumer consumer = new LogConsumer(getLogger(LogConsumer.class), file, pipe(machine, listener, false), false);
        while (consumer.run())
            ;
    }

    private DataConsumer pipe(VirtualMachine machine, ProfileListener listener, boolean continuous) {
        LogSaver saver = logRepo.onNewLog(machine);

        if (continuous) {
            ProfileUpdateModerator moderator = new ProfileUpdateModerator(getLogger(ProfileUpdateModerator.class), listener);
            moderator.start();
            listener = moderator;
        }

        LogCollector collector = new LogCollector(listener, continuous);
        return new DataConsumer(getLogger(DataConsumer.class), machine, saver, collector);
    }

}
