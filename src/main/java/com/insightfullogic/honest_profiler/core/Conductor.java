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

import com.insightfullogic.honest_profiler.core.parser.LogParser;
import com.insightfullogic.honest_profiler.core.parser.LogParser.AmountRead;
import com.insightfullogic.honest_profiler.core.sources.LogSource;
import org.slf4j.Logger;

import static com.insightfullogic.honest_profiler.core.parser.LogParser.AmountRead.NOTHING;

public class Conductor
{

    private static final long POLL_INTERVAL = 10;

    private final Logger logger;
    private final LogSource source;
    private final LogParser parser;
    private final boolean continuous;

    public Conductor(
        final Logger logger,
        final LogSource source,
        final LogParser parser,
        final boolean continuous)
    {

        this.logger = logger;
        this.source = source;
        this.parser = parser;
        this.continuous = continuous;
    }

    public boolean poll()
    {
        try
        {
            AmountRead amount = parser.readRecord(source.read());
            switch (amount)
            {
                case COMPLETE_RECORD:
                    return true;

                case PARTIAL_RECORD:
                    sleep();
                    return true;

                case NOTHING:
                    if (continuous)
                    {
                        sleep();
                    }
                    else
                    {
                        logEndOfLog(NOTHING);
                        parser.endOfLog();
                    }
                    return continuous;
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            logEndOfLog(e.getMessage());
            parser.endOfLog();
        }

        return false;
    }

    public void run()
    {
        while (poll())
        {
        }
    }

    private void logEndOfLog(final Object cause)
    {
        logger.debug("Reached end of log for {} due to {}", source, cause);
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(POLL_INTERVAL);
        }
        catch (InterruptedException e)
        {
            logger.error(e.getMessage(), e);
        }
    }

}
