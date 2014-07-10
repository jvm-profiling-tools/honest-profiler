package com.insightfullogic.honest_profiler.core.sources;

import com.insightfullogic.honest_profiler.core.conductor.MachineListener;

public interface MachineSource {

    public void poll(MachineListener listener);

}
