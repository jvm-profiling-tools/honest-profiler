package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogRepo;
import com.insightfullogic.honest_profiler.core.store.LogSaver;

public class Conductor {

    private final LogRepo logRepo;
    private final ProfileListener listener;

    public Conductor(LogRepo logRepo, ProfileListener listener) {
        this.logRepo = logRepo;
        this.listener = listener;
    }

    public LogConsumer onNewLog(VirtualMachine machine) {
        LogSaver saver = logRepo.onNewLog(machine);
        return new LogConsumer(machine, saver, listener);
    }

}
