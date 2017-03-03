package com.insightfullogic.honest_profiler.core.profiles.lean;

import java.util.function.Consumer;

import com.insightfullogic.honest_profiler.core.collector.lean.LeanLogCollector;

/**
 * Marker interface for Classes which can be used to receive {@link LeanProfile}s emitted by the
 * {@link LeanLogCollector}.
 * <p>
 * Not sure if this adds anything but noise, might get rid of it.
 */
public interface LeanProfileListener extends Consumer<LeanProfile>
{
    // Marker Interface
}
