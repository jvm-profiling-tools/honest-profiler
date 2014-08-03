package com.insightfullogic.honest_profiler.core.filters;

import com.insightfullogic.honest_profiler.core.collector.Profile;

/**
 * All implementations should be immutable and thus threadsafe.
 */
public interface Filter {

    void filter(Profile profile);

}
