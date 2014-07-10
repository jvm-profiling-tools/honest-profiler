package com.insightfullogic.honest_profiler.core.sources;

import com.insightfullogic.honest_profiler.core.conductor.MachineListener;

public interface MachineFinder {

    public void poll(MachineListener listener);

}
