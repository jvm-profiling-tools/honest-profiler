package com.insightfullogic.honest_profiler.core;

import com.insightfullogic.honest_profiler.core.parser.EventListener;
import com.insightfullogic.honest_profiler.core.parser.LogParser;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.insightfullogic.honest_profiler.core.parser.LogParser.LogState;
import static com.insightfullogic.honest_profiler.core.parser.LogParser.LogState.END_OF_LOG;

public class DataConsumer {

    private final Logger logger;
    private final VirtualMachine machine;
    private final LogSaver saver;
    private final EventListener listener;
    private final LogParser parser;

    public DataConsumer(
            final Logger logger,
            final VirtualMachine machine,
            final LogSaver saver,
            final EventListener listener) {

        this.logger = logger;
        this.machine = machine;
        this.saver = saver;
        this.listener = listener;
        this.parser = new LogParser(LoggerFactory.getLogger(LogParser.class), listener);
    }

    public LogState accept(ByteBuffer data) {
        try {
            saver.save(data);
            return parser.readRecord(data);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            endOfLog();
            return END_OF_LOG;
        }
    }

    public void endOfLog() {
        listener.endOfLog();
        saver.close();
    }

    public VirtualMachine getMachine() {
        return machine;
    }

}
