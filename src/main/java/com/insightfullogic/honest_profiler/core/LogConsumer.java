package com.insightfullogic.honest_profiler.core;

import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

public class LogConsumer {

    private static final long POLL_INTERVAL = 10;

    private final Logger logger;
    private final DataConsumer consumer;
    private final boolean continuous;

    private RandomAccessFile input;
    private MappedByteBuffer buffer;

    public LogConsumer(
            final Logger logger,
            final File file,
            final DataConsumer consumer,
            final boolean continuous) throws IOException {

        this.logger = logger;
        this.consumer = consumer;
        this.continuous = continuous;
        input = new RandomAccessFile(file, "r");
        buffer = input.getChannel()
                      .map(READ_ONLY, 0, file.length());
    }

    public boolean run() throws IOException {
        switch (consumer.accept(buffer)) {
            case END_OF_LOG:
                input.close();
                return false;
            case NOTHING_READ:
                if (continuous) {
                    sleep();
                } else {
                    consumer.endOfLog();
                }
                return continuous;
            case READ_RECORD:
                return true;
        }
        // should never get here
        return false;
    }

    private void sleep() {
        try {
            Thread.sleep(POLL_INTERVAL);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
