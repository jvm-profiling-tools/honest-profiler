/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 **/
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
