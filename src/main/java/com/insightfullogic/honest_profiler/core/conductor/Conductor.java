package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogRepo;
import com.insightfullogic.honest_profiler.core.store.LogSaver;

public class Conductor {

    private final LogRepo logRepo;

    public Conductor(LogRepo logRepo) {
        this.logRepo = logRepo;
    }

    public LogConsumer onNewLog(VirtualMachine machine, ProfileListener listener) {
        LogSaver saver = logRepo.onNewLog(machine);
        return new LogConsumer(machine, saver, listener);
    }

}
