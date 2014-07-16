package com.insightfullogic.honest_profiler.core.conductor;

import com.insightfullogic.honest_profiler.core.parser.EventListener;
import com.insightfullogic.honest_profiler.core.parser.LogParser;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogSaver;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.insightfullogic.honest_profiler.core.parser.LogParser.LogState;
import static com.insightfullogic.honest_profiler.core.parser.LogParser.LogState.END_OF_LOG;

public class DataConsumer {

    private final VirtualMachine machine;
    private final LogSaver saver;
    private final EventListener listener;
    private final LogParser parser;

    public DataConsumer(VirtualMachine machine, LogSaver saver, EventListener listener) {
        this.machine = machine;
        this.saver = saver;
        this.listener = listener;
        this.parser = new LogParser(listener);
    }

    public LogState accept(ByteBuffer data) {
        try {
            saver.save(data);
            return parser.readRecord(data);
        } catch (IOException e) {
            e.printStackTrace();
            endOfLog();
            return END_OF_LOG;
        }
    }

    public void endOfLog() {
        listener.endOfLog();
    }

    public void close() {
        saver.close();
    }

    public VirtualMachine getMachine() {
        return machine;
    }

}
