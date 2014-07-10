package com.insightfullogic.honest_profiler.adapters.store;

import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogMetadata;
import com.insightfullogic.honest_profiler.core.store.LogRepo;

import java.nio.ByteBuffer;
import java.util.List;

public class FileLogRepo implements LogRepo {

    @Override
    public FileLogSaver onNewLog(VirtualMachine machine) {
        return new FileLogSaver();
    }

    @Override
    public List<LogMetadata> findLogs() {
        return null;
    }

    @Override
    public ByteBuffer loadLog(LogMetadata log) {
        return null;
    }

    @Override
    public void close() {

    }

}
