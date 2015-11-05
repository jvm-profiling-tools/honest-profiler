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

import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class ProfileUpdateModerator extends Thread implements ProfileListener
{

    private static final long UI_UPDATE_WINDOW_IN_MS = 1000;

    private final Logger logger;
    private final ProfileListener listener;
    private final AtomicReference<Profile> incomingProfile = new AtomicReference<>();

    public ProfileUpdateModerator(final Logger logger, final ProfileListener listener)
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
                final Profile profile = incomingProfile.get();
                if (profile != null)
                {
                    listener.accept(profile);
                    incomingProfile.compareAndSet(profile, null);
                }

                Thread.sleep(UI_UPDATE_WINDOW_IN_MS);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void accept(Profile profile)
    {
        incomingProfile.set(profile);
    }

}
