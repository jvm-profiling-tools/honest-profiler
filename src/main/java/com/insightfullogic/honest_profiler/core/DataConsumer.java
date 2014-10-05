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
