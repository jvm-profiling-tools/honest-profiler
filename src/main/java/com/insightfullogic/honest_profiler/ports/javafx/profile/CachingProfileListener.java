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
package com.insightfullogic.honest_profiler.ports.javafx.profile;

import com.insightfullogic.honest_profiler.core.filters.ProfileFilter;
import com.insightfullogic.honest_profiler.core.profiles.Profile;
import com.insightfullogic.honest_profiler.core.profiles.ProfileListener;
import javafx.application.Platform;
import org.slf4j.Logger;

public class CachingProfileListener implements ProfileListener
{

    private final Logger logger;
    private final FlatViewModel flatModel;
    private final TreeViewModel treeModel;
    private final TraceCountViewModel countModel;
    private final ProfileFilter profileFilter;

    private Profile lastProfile;

    public CachingProfileListener(
        final Logger logger,
        final FlatViewModel flatModel,
        final TreeViewModel treeModel,
        final TraceCountViewModel countModel,
        final ProfileFilter profileFilter)
    {
        this.logger = logger;
        this.flatModel = flatModel;
        this.treeModel = treeModel;
        this.countModel = countModel;
        this.profileFilter = profileFilter;
    }

    @Override
    public void accept(Profile profile)
    {
        lastProfile = profile;

        profileFilter.accept(profile);

        // All UI updates must go through here.
        onFxThread(() -> {
            try
            {
                flatModel.accept(profile);
                treeModel.accept(profile);
                countModel.accept(profile);
            }
            catch (Throwable t)
            {
                logger.error(t.getMessage(), t);
            }
        });
    }

    public void reflushLastProfile()
    {
        if (lastProfile != null)
        {
            accept(lastProfile);
        }
    }

    // ViewModel instances can happily update the UI
    // without worrying about threading implications
    private void onFxThread(final Runnable block)
    {
        if (Platform.isFxApplicationThread())
        {
            block.run();
        }
        else
        {
            Platform.runLater(block);
        }
    }

}
