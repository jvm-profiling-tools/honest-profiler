package com.insightfullogic.honest_profiler.core.collector.lean;

/**
 * Interface for classes a profile can be requested from.
 */
public interface ProfileSource
{
    /**
     * Request that a profile be emitted as soon as possible. There is no guarantee that any profile will be emitted.
     */
    void requestProfile();
}
