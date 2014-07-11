package com.insightfullogic.honest_profiler.core.conductor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

public class LogConsumer {

    private static final long POLL_INTERVAL = 10;

    private final DataConsumer consumer;

    private RandomAccessFile input;
    private MappedByteBuffer buffer;

    public LogConsumer(File file, DataConsumer consumer) throws IOException {
        this.consumer = consumer;
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
                sleep();
                return true;
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
            e.printStackTrace();
        }
    }

}
