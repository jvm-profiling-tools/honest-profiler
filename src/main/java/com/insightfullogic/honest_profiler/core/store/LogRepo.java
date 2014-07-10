package com.insightfullogic.honest_profiler.core.store;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;

public interface LogRepo {
    LogSaver onNewLog(VirtualMachine machine);
}
