package com.insightfullogic.honest_profiler.core.store;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;

import java.nio.ByteBuffer;
import java.util.List;

public interface LogRepo {

    LogSaver onNewLog(VirtualMachine machine);

    List<LogMetadata> findLogs();

    ByteBuffer loadLog(LogMetadata log);

    /**
     * Close all children and update log store information
     */
    void close();

}
