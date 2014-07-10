package com.insightfullogic.honest_profiler.adapters.store;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogRepo;

public class FileLogRepo implements LogRepo {

    @Override
    public FileLogSaver onNewLog(VirtualMachine machine) {
        return new FileLogSaver();
    }

}
