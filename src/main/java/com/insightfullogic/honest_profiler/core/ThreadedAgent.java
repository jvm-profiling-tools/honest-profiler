/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
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

public class ThreadedAgent implements Runnable
{

    public interface Block
    {
        /**
         * @return false iff you're ready to stop, true otherwise
         * @throws Exception when things go bump
         */
        boolean run() throws Exception;
    }

    private final Logger logger;
    private final Block block;

    private Thread thread;

    public ThreadedAgent(final Logger logger, Block block)
    {
        this.logger = logger;
        this.block = block;
    }

    public void start()
    {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop()
    {
        thread.interrupt();
    }

    @Override
    public void run()
    {
        logger.debug("Started");
        try
        {
            while (!Thread.currentThread().isInterrupted() && block.run())
                ;

            logger.debug(Thread.currentThread().getName() + " Stopped");
        }
        catch (Throwable throwable)
        {
            // Deliberately catching throwable since we're at the top of a thread
            logger.error(throwable.getMessage(), throwable);
        }
    }

}
