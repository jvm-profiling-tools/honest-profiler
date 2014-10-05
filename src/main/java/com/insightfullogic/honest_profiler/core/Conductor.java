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

import com.insightfullogic.honest_profiler.core.collector.LogCollector;
import com.insightfullogic.honest_profiler.core.sources.VirtualMachine;
import com.insightfullogic.honest_profiler.core.store.LogRepo;
import com.insightfullogic.honest_profiler.core.store.LogSaver;

import java.io.File;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

// TODO: decouple saving from parsing/processing (Possibly remove this class)
public class Conductor {

    private final LogRepo logRepo;

    public Conductor(LogRepo logRepo) {
        this.logRepo = logRepo;
    }

    public DataConsumer pipeData(VirtualMachine machine, ProfileListener listener) {
        return pipe(machine, listener, false);
    }

    public void pipeFile(File file, VirtualMachine machine, ProfileListener listener) throws IOException {
        final LogConsumer logConsumer = new LogConsumer(getLogger(LogConsumer.class), file, pipe(machine, listener, true), true);
        new ThreadedAgent(getLogger(ThreadedAgent.class), logConsumer::run).start();
    }

    public void consumeFile(File file, VirtualMachine machine, ProfileListener listener) throws IOException {
        LogConsumer consumer = new LogConsumer(getLogger(LogConsumer.class), file, pipe(machine, listener, false), false);
        while (consumer.run())
            ;
    }

    private DataConsumer pipe(VirtualMachine machine, ProfileListener listener, boolean continuous) {
        LogSaver saver = logRepo.onNewLog(machine);

        if (continuous) {
            ProfileUpdateModerator moderator = new ProfileUpdateModerator(getLogger(ProfileUpdateModerator.class), listener);
            moderator.start();
            listener = moderator;
        }

        LogCollector collector = new LogCollector(listener, continuous);
        return new DataConsumer(getLogger(DataConsumer.class), machine, saver, collector);
    }

}
