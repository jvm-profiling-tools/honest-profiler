package com.insightfullogic.honest_profiler.core.store;

import java.time.Instant;

public class LogMetadata {

    private final String machineId;
    private final Instant started;
    private final Instant finished;

    public LogMetadata(String machineId, Instant started, Instant finished) {
        this.machineId = machineId;
        this.started = started;
        this.finished = finished;
    }

}
