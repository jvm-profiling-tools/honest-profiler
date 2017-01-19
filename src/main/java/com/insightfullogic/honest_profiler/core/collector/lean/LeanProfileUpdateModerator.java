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
package com.insightfullogic.honest_profiler.core.collector.lean;

import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;

import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfile;
import com.insightfullogic.honest_profiler.core.profiles.lean.LeanProfileListener;

public class LeanProfileUpdateModerator extends Thread implements LeanProfileListener
{

    private static final long UI_UPDATE_WINDOW_IN_MS = 1000;

    private final Logger logger;
    private final LeanProfileListener listener;
    private final AtomicReference<LeanProfile> incomingProfile = new AtomicReference<>();

    public LeanProfileUpdateModerator(final Logger logger, final LeanProfileListener listener)
    {
        this.logger = logger;
        this.listener = listener;
        setDaemon(true);
        setName(getClass().getSimpleName());
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                final LeanProfile profile = incomingProfile.get();
                if (profile != null)
                {
                    listener.accept(profile);
                    incomingProfile.compareAndSet(profile, null);
                }

                sleep(UI_UPDATE_WINDOW_IN_MS);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void accept(LeanProfile profile)
    {
        incomingProfile.set(profile);
    }

}
