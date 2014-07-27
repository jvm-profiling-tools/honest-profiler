package com.insightfullogic.honest_profiler.core;

import com.insightfullogic.honest_profiler.core.collector.Profile;

public interface ProfileListener {

    public void accept(Profile profile);

}
