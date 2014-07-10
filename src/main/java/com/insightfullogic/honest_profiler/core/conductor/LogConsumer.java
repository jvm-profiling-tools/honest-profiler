package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.parser.LogParser;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogSaver;

import java.io.IOException;
import java.nio.ByteBuffer;

public class LogConsumer {

    private final VirtualMachine machine;
    private final LogSaver saver;
    private final LogParser parser;

    public LogConsumer(VirtualMachine machine, LogSaver saver, ProfileListener listener) {
        this.machine = machine;
        this.saver = saver;

        LogCollector collector = new LogCollector(listener);
        this.parser = new LogParser(collector);
    }

    public boolean accept(ByteBuffer data) {
        try {
            saver.save(data);
            return parser.readRecord(data, true);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: log
            return false;
        }
    }

    public void close() {
        saver.close();
        parser.stop();
    }

    public VirtualMachine getMachine() {
        return machine;
    }
}
